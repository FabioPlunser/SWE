import requests
import json

from typing import Optional
from datetime import datetime
from http import HTTPStatus
from requests.compat import urljoin


def describe_not_ok_response(r: requests.Response):
    return f'Got response [{r.status_code}] from "{r.request.method} {r.url}"'

class TokenDeclinedError(Exception):
    pass

class Server:
    REQUEST_TIMEOUT = 3

    def __init__(self, address, token=None):
        self.address = address
        self.token = token
    
    def _get_headers(self) -> dict:
        """
        Generate the required headers.
        :return: A dictionary that can be used for requests
        """
        headers = {}
        headers['UserAgent'] = 'AccessPoint'
        if self.token:
            headers['Authorization'] = json.dumps({'token': self.token})
        return headers
    
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
            response = requests.post(
                self._get_endpoint_url('register'),
                headers=self._get_headers(),
                timeout=Server.REQUEST_TIMEOUT,
                json={'room_name': room_name}
            )
        except (requests.ConnectTimeout, requests.ReadTimeout) as e:
            raise ConnectionError(f'Request timed out: {e}')

        # check status code
        if response.status_code != HTTPStatus.OK:
            raise ConnectionError(describe_not_ok_response(response))
        
        # get content
        content = response.json()
        self.token = content.get('token')
        if self.token:
            return self.token
        else:
            raise ConnectionError(f'Got response OK but did not receive token with response')
        
    def get_config(self) -> tuple[dict, list]:
        """
        Tries to get a configuration update from the backend. The update also contains info
        which sensor stations currently are enabled.
        :return: A tuple with a dictionary and a list of dictionaries
            The former for the configuration regarding the access point:
                {
                    (opt) "room_name": Name of the room in which the access point is located -> str
                    "scan_active": Flag that inidicates if the access point shall search for new sensor stations -> bool
                    "transfer_interval": Time in seconds between transfering sensor data to the backend -> int
                }
            The latter for enabled sensor stations:
                [
                    {
                        "address": Address of the sensor station
                        "limits: [
                            {
                                "name": Name of the sensor -> str
                                "upper_limit": Upper limit for alarms -> float | int
                                "lower_limit": Lower limit for alarms -> float | int 
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
            response = requests.get(
                self._get_endpoint_url('get-config'),
                headers=self._get_headers(),
                timeout=Server.REQUEST_TIMEOUT 
            )
        except (requests.ConnectTimeout, requests.ReadTimeout) as e:
            raise ConnectionError(f'Request timed out: {e}')
        
        # check status code
        if response.status_code == HTTPStatus.UNAUTHORIZED:
            raise TokenDeclinedError()
        elif response.status_code != HTTPStatus.OK:
            raise ConnectionError(describe_not_ok_response(response))
        
        # get content
        content =  response.json()

        # construct and validate config
        raw_config = content.get('access-point')
        if raw_config is None: raw_config = {}
        if not isinstance(raw_config, dict): raise ConnectionError('Did not received a valid configuration')
        config = {}
        if isinstance(raw_config.get('room-name'), str):
            config['room_name'] = raw_config.pop('room-name')
        if isinstance(raw_config.get('pairing-mode'), bool):
            config['scan_active'] = raw_config.pop('pairing-mode')
        if isinstance(raw_config.get('transfer-interval'), int):
            config['transfer_interval'] = raw_config.pop('transfer-interval')

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
            if not isinstance(raw_sensor_station.get('limits'), list):
                raw_sensor_station.pop('limits')
            else:
                sensor_station['limits'] = []
                for raw_limit in raw_sensor_station.get('limits'):
                    limit = {}
                    if not isinstance(raw_limit, dict): continue
                    if not isinstance(raw_limit.get('sensor-name'), str):
                        continue
                    else:
                        limit['sensor_name'] = raw_limit.get('sensor-name')
                    if isinstance(raw_limit.get('lower-limit'), float) or isinstance(raw_limit.get('lower-limit', int)):
                        limit['lower_limit'] = raw_limit.get('lower-limit')
                    if isinstance(raw_limit.get('upper-limit'), float) or isinstance(raw_limit.get('upper-limit', int)):
                        limit['upper_limit'] = raw_limit.get('upper-limit')
                    if isinstance(raw_limit.get('alarm-tripping-time'), int):
                        limit['alarm_tripping_time'] = raw_limit.get('alarm_tripping_time')
                    sensor_station['limits'].append(limit)
                sensor_stations.append(sensor_station)

        return config, sensor_stations
    
    def transfer_data(self, measurements: list[dict], connection_alive: dict) -> None:
        """
        Transfers measured sensor data to the backend. Also contains info
        on the connection status of sensor stations (if connections are lost).
        :param connection_alive: Dictionary with sensor station addresses as keys and
            'True' if the connection is alive or 'False' if it is not. If a sensor
            station address occurrs within the measurements but not within this
            parameter the connection is assumed to be NOT alive.
        :param measurements: A list of dictionaries structured as:
            {
                "sensor_station_address": Address of the sensor station -> str,
                "sensor_name": Name of the sensor -> str,
                (opt) "unit": Unit of the measured value -> str,
                "timestamp": Timestamp of the measurement -> datetime,
                "value": Measured value -> float,
                "alarm": Alarm active at the time of the measurement -> str ['n' no alarm | 'l' below limit | 'h' above limit]
            }
        :raises ConnectionError: If the request fails
        :raises AttributeError: If the parameters do not contain all required information
        """
        # get all sensor station addresses
        sensor_station_addresses = [m['sensor_station_address'] for m in measurements]
        sensor_station_addresses.extend(connection_alive.keys())
        sensor_station_addresses = list(set(sensor_station_addresses))

        # construct request body
        sensor_station_data = {}
        for adr in sensor_station_addresses:
            values = [
                {
                    'sensor': m.get('sensor_name'),
                    'timestamp': str(m.get('timestamp')),
                    'value': m.get('value'),
                    'unit': m.get('unit'),
                    'alarm': m.get('alarm')
                }
                for m in measurements
                if m.get('sensor_station_address') == adr
            ]
            data = {
                'id': adr,
                'connection-alive': connection_alive.get(adr) if connection_alive.get(adr) is not None else False,
                'values': values
            }
            sensor_station_data[adr] = data

        # send request
        try:
            response = requests.post(
                self._get_endpoint_url('transfer-data'),
                headers=self._get_headers(),
                timeout=Server.REQUEST_TIMEOUT,
                json=sensor_station_data
            )
        except (requests.ConnectTimeout, requests.ReadTimeout) as e:
            raise ConnectionError(f'Request timed out: {e}')

    def report_found_sensor_station(self, sensor_station_addresses: list[str]) -> None:
        """
        Reports newly found sensor stations to the backend.
        :param sensor_station_addresses: A list with the addresses of the found sensor stations
        """
        # send request
        try:
            response = requests.put(
                self._get_endpoint_url('found-sensor-stations'),
                headers=self._get_headers(),
                timeout=Server.REQUEST_TIMEOUT,
                json={
                    'sensor-stations': sensor_station_addresses,
                }
            )
        except (requests.ConnectTimeout, requests.ReadTimeout) as e:
            raise ConnectionError(f'Request timed out: {e}')
        
        # check status code
        if response.status_code != HTTPStatus.OK:
            raise ConnectionError(describe_not_ok_response(response))
