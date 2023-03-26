import logging
import requests
import json

from requests.compat import urljoin

log = logging.getLogger()

REQUEST_TIMEOUT = 3

def describe_not_ok_response(r: requests.Response):
    return f'Got response [{r.status_code}]'

class Server:
    def __init__(self, address, token=None):
        self.address = address
        self.token = token

    def is_registered(self):
        return self.token is not None

    def register(self, name: str) -> str:
        log.info('Trying to register at backend')

        try:
            response = requests.get(
                urljoin(self.address, 'api/register'),
                params={'name': name},
                timeout=REQUEST_TIMEOUT
            )
        except requests.ConnectTimeout as e:
            raise ConnectionError(f'Request timed out: {e}')

        if not response.ok:
            raise ConnectionError(describe_not_ok_response(response))
        
        content = response.json()
        self.token = content.get('token')
        if self.token:
            log.info('Received token')
            return self.token
        else:
            raise ConnectionError(f'Got response OK but did not receive token with response')
        
    def get_config(self) -> tuple[dict, list, list]:
        log.info('Updating configuration from backend')

        try:
            response = requests.get(
                urljoin(self.address, 'api/get-configuration'),
                headers={'Authorization': f'Bearer {self.token}'},
                timeout=REQUEST_TIMEOUT
            )
        except requests.ConnectTimeout as e:
            raise ConnectionError(f'Request timed out: {e}')
        
        # token declined
        if response.status_code == 401 or response.status_code == 403:
            log.error(describe_not_ok_response(response))
            self.token = None
            raise ValueError()
        # other error
        elif not response.ok:
            raise ConnectionError(describe_not_ok_response(response))
        
        content = response.json()
        return content.get('config'), content.get('sensor_stations_to_add'), content.get('sensor_stations_to_remove')

        

