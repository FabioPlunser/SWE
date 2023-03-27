import logging
import requests
import json

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
    
    def _get_headers(self):
        headers = {}
        headers['UserAgent'] = 'AccessPoint'
        if self.token:
            headers['Authorization'] = json.dumps({'token': self.token})
        return headers
    
    def _get_endpoint_url(self, endpoint: str):
        return urljoin(self.address, f'api/{endpoint}')

    def register(self, room_name: str) -> str:
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
        
    def get_config(self) -> dict:
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
        if response.status_code == HTTPStatus.UNAUTHORIZED or response.status_code == HTTPStatus.FORBIDDEN:
            raise TokenDeclinedError()
        elif response.status_code != HTTPStatus.OK:
            raise ConnectionError(describe_not_ok_response(response))
        
        # get content
        content =  response.json()
        return content
    
    def transfer_data(self):
        # send request
        try:
            response = requests.post(
                self._get_endpoint_url('transfer-data'),
                headers=self._get_headers(),
                timeout=Server.REQUEST_TIMEOUT,
                json={
                    #TODO
                }
            )
        except (requests.ConnectTimeout, requests.ReadTimeout) as e:
            raise ConnectionError(f'Request timed out: {e}')

    def report_found_sensor_station(self, sensor_station_addresses):
        # send request
        try:
            response = requests.put(
                self._get_endpoint_url('sensor-station'),
                headers=self._get_headers(),
                timeout=Server.REQUEST_TIMEOUT,
                json={
                    'sensor-stations': [str(adr) for adr in sensor_station_addresses]
                }
            )
        except (requests.ConnectTimeout, requests.ReadTimeout) as e:
            raise ConnectionError(f'Request timed out: {e}')
        
        # check status code
        if response.status_code != HTTPStatus.OK:
            raise ConnectionError(describe_not_ok_response(response))
