import requests
import json
import functools

from typing import Union, Optional
from datetime import datetime
from requests.compat import urljoin
from requests.adapters import HTTPAdapter, Retry


def describe_not_ok_response(r: requests.Response):
    return f'Got response [{r.status_code}] from "{r.request.method} {r.url}"'

class TokenDeclinedError(Exception):
    pass

class Server:
    _REQUEST_TIMEOUT = 3
    _REQUEST_5XX_RETRIES = 1
    _LOCKED_STATUS_CODES = [401, 403]

    def __init__(self, address, token=None):
        self.address = address
        self._client = requests.Session()
        self._token = token
        # setup default request timeout
        for method in ['get', 'options', 'head', 'post', 'put', 'patch', 'delete']:
            setattr(self._client, method, functools.partial(getattr(self._client, method), timeout=self._REQUEST_TIMEOUT))
        # setup headers
        self._client.headers.update({'User-Agent': 'AccessPoint'})
        # setup automatic retries on 5xx responses
        retries_5xx = Retry(total=self._REQUEST_5XX_RETRIES,
                            backoff_factor=0.2,
                            status_forcelist=list(range(500,600)),
                            raise_on_status=False,
                            allowed_methods=False)
        self._client.mount('http://', HTTPAdapter(max_retries=retries_5xx))
        self._client.mount('https://', HTTPAdapter(max_retries=retries_5xx))
    
    @property
    def token(self) -> Optional[str]:
        return self._token
    
    @token.setter
    def token(self, value: Optional[str]) -> None:
        """
        Assigns a new value to the token and updates requests headers.
        :param value: The new value of the token
        :raises TokenDeclinedError: If there has been a token previously and the token is reset
        """
        previous_token = self._token
        self._token = value
        # update headers if token is set or removed
        if value:
            self._client.headers.update({'Authorization': json.dumps({'token': self._token})})
        elif 'Authorization' in self._client.headers:
            self._client.headers.pop('Authorization')
        # raise exception if token has been removed
        if previous_token and not self._token:
            raise TokenDeclinedError

    def _get_endpoint_url(self, endpoint: str) -> str:
        """
        Generate the full URL for a given endpoint.
        :return: URL
        """
        return urljoin(self.address, f'api/{endpoint}')

    def register(self, id: str, room_name: str) -> str:
        """
        Tries to register at the backend. If successful the received token is stored
        within the object.
        :param id: Self assigned ID
        :param room_name: Name of the room in which the access point is located
        :return: The token, if received
        :raises ConnectionError: If no token has been received or the request failed
        """
        # send request
        try:
            response = self._client.post(
                self._get_endpoint_url('register'),
                json={'id': id,
                      'room_name': room_name}
            )
        except (requests.ConnectTimeout, requests.ReadTimeout) as e:
            raise ConnectionError(f'Request timed out: {e}')

        # check status code
        if response.status_code in self._LOCKED_STATUS_CODES:
            self.token = None
        elif not response.ok:
            raise ConnectionError(describe_not_ok_response(response))
        
        # get content
        content = response.json()
        self.token = content.get('token')
        return self.token
        
    def get_config(self) -> tuple[dict[str, Union[str, bool, int]], list[dict[str, Union[str, list[dict[str, Union[str, int]]]]]]]:
        """
        Tries to get a configuration update from the backend. The update also contains info
        which sensor stations currently are enabled.
        :return: A tuple with a dictionary and a list of dictionaries
            The former for the configuration regarding the access point:
                {
                    (opt) "room_name": Name of the room in which the access point is located -> str
                    "scan_active": Flag that inidicates if the access point shall search for new sensor stations -> bool
                    "transfer_data_interval": Time in seconds between transfering sensor data to the backend -> int
                }
            The latter for enabled sensor stations:
                [
                    {
                        "address": Address of the sensor station
                        "sensors: [
                            {
                                "name": Name of the sensor -> str
                                "limits": {
                                    "upper_limit": Upper limit for alarms -> float | int
                                    "lower_limit": Lower limit for alarms -> float | int 
                                }
                                "alarm_tripping_time": Time in seconds until an alarm is tripped -> int
                            },
                            ...
                        ]
                    },
                    ...
                ]
        :raises TokenDeclinedError: If the token is not accepted anymore
        :raises ConnectionError: If the request failed or the content of the request is greatly malformed
        """
        # send request
        try:
            response = self._client.get(
                self._get_endpoint_url('get-config')
            )
        except (requests.ConnectTimeout, requests.ReadTimeout) as e:
            raise ConnectionError(f'Request timed out: {e}')
        
        # check status code
        if response.status_code in self._LOCKED_STATUS_CODES:
            self.token = None
        elif not response.ok:
            raise ConnectionError(describe_not_ok_response(response))
        
        # get content
        content =  response.json()

        # construct and validate config
        raw_config = content.get('access-point')
        if raw_config is None: raw_config = {}
        if not isinstance(raw_config, dict): raise ConnectionError('Did not received a valid configuration')
        config = {}
        if isinstance(raw_config.get('room-name'), str):
            config['room_name'] = str(raw_config.pop('room-name'))
        if isinstance(raw_config.get('pairing-mode'), bool):
            config['scan_active'] = bool(raw_config.pop('pairing-mode'))
        if isinstance(raw_config.get('transfer-interval'), int):
            config['transfer_data_interval'] = int(raw_config.pop('transfer-interval'))

        # construct and validate sensor station data
        raw_sensor_stations = content.get('sensor-stations')
        if raw_sensor_stations is None: raw_sensor_stations = []
        if not isinstance(raw_sensor_stations, list): raise ConnectionError('Did not receive a valid sensor station list')
        sensor_stations = []
        for raw_sensor_station in raw_sensor_stations:
            sensor_station = {}
            if not isinstance(raw_sensor_station, dict): raise ConnectionError('Sensor stations not described as expected')
            if not isinstance(raw_sensor_station.get('id'), str):
                raise ConnectionError('Sensor stations not described as expected')
            else:
                sensor_station['address'] = raw_sensor_station.get('id')
            if not isinstance(raw_sensor_station.get('sensors'), list):
                if raw_sensor_station.get('sensors'):
                    raw_sensor_station.pop('sensors')
            else:
                sensor_station['sensors'] = []
                for raw_sensor in raw_sensor_station.get('sensors'):
                    sensor = {}
                    if not isinstance(raw_sensor, dict): continue
                    if not isinstance(raw_sensor.get('sensor-name'), str):
                        continue
                    else:
                        sensor['sensor_name'] = str(raw_sensor.get('sensor-name'))
                    if isinstance(raw_sensor.get('limits'), dict):        
                        if isinstance(raw_sensor['limits'].get('lower-limit'), float) or isinstance(raw_sensor['limits'].get('lower-limit'), int):
                            sensor['lower_limit'] = int(raw_sensor['limits'].get('lower-limit'))
                        if isinstance(raw_sensor['limits'].get('upper-limit'), float) or isinstance(raw_sensor['limits'].get('upper-limit'), int):
                            sensor['upper_limit'] = int(raw_sensor['limits'].get('upper-limit'))
                    if isinstance(raw_sensor.get('alarm-threshold-time'), int):
                        sensor['alarm_tripping_time'] = int(raw_sensor.get('alarm-threshold-time'))
                    sensor_station['sensors'].append(sensor)
            sensor_stations.append(sensor_station)

        return config, sensor_stations
    
    def transfer_data(self,
                      station_data: dict[str, dict[str, bool, Optional[int]]],
                      measurements: list[dict[str, Union[str, datetime, int, None]]]) -> None:
        """
        Transfers measured sensor data to the backend. Also contains info
        on the connection status of sensor stations (if connections are lost).
        :param station_data: Dictionary with sensor station addresses as keys and
            subordinated dictionaries for connection state and DIP switch id
                {
                    'connection_alive': 'True' if the connection is alive, 'False' if not
                    'dip_id': Integer encoded DIP switch position
                }
        :param measurements: A list of dictionaries structured as:
            {
                "sensor_station_address": Address of the sensor station -> str,
                "sensor_name": Name of the sensor -> str,
                (opt) "unit": Unit of the measured value -> str,
                "timestamp": Timestamp of the measurement -> datetime,
                "value": Measured value -> float,
                "alarm": Alarm active at the time of the measurement -> str ['n' no alarm | 'l' below limit | 'h' above limit]
            }
        :raises TokenDeclinedError: If the token is not accepted anymore
        :raises ConnectionError: If the request fails
        """
        # setup entries for each known sensor station
        data = [{'id': adr,
                 'connection-alive': status.get('connection_alive'),
                 'dip-switch': status.get('dip_id')}
                 for adr, status in station_data.items()]

        # assign measurements to single sensor stations
        for index, entry in enumerate(data):
            adr = entry.get('id')
            station_measurements = [m for m in measurements if m.get('sensor_station_address') == adr]
            timestamps: list[datetime] = sorted(list(set([m.get('timestamp') for m in station_measurements])), reverse=True)
            # group measurements by timestamp
            values = []
            for t in timestamps:
                filtered_station_measurements = [m for m in station_measurements if m.get('timestamp') == t]
                values.append({'timestamp': t.isoformat(),
                               'sensors': [{'sensor': m.get('sensor_name'),
                                            'value': m.get('value'),
                                            'unit': m.get('unit'),
                                            'alarm': m.get('alarm')}
                                            for m in filtered_station_measurements]})
            # update data structure
            data[index]['values'] = values

        # send request
        try:
            response = self._client.post(
                self._get_endpoint_url('transfer-data'),
                json={'sensor-stations': data}
            )
        except (requests.ConnectTimeout, requests.ReadTimeout) as e:
            raise ConnectionError(f'Request timed out: {e}')
        
        if response.status_code in self._LOCKED_STATUS_CODES:
            self.token = None
        elif not response.ok:
            raise ConnectionError(describe_not_ok_response(response))

    def report_found_sensor_station(self, sensor_stations: list[dict[str, Union[str, int]]]) -> None:
        """
        Reports newly found sensor stations to the backend.
        :param sensor_stations: A list with one dictionary for each sensor station, like
            {
                "address": The address of the sensor stations,
                "dip-switch": The integer encoded position of the dip switches
            }
        :raises TokenDeclinedError: If the token is not accepted anymore
        :raises ConnectionError: If the request fails
        """
        # send request
        try:
            response = self._client.put(
                self._get_endpoint_url('found-sensor-stations'),
                json={
                    'sensor-stations': sensor_stations
                }
            )
        except (requests.ConnectTimeout, requests.ReadTimeout) as e:
            raise ConnectionError(f'Request timed out: {e}')
        
        # check status code
        if response.status_code in self._LOCKED_STATUS_CODES:
            self.token = None
        elif not response.ok:
            raise ConnectionError(describe_not_ok_response(response))
