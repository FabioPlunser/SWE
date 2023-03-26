import validators
import typing
import yaml
import logging

from datetime import timedelta

log = logging.getLogger()

class Config:
    def __init__(self, filename: str):
        self.filename = filename    # not stored in config file

        self._set_defaults()

    def load(self):
        """
        Loads configuration from the file given during initialization
        If validation fails, a ValueError will be raised
        """
        try:
            log.info('Loading config file')
            with open(self.filename, 'r') as f:
                try:
                    data = yaml.load(f, Loader=yaml.loader.SafeLoader)
                    try:
                        self.update_from_dict(data)
                        self._validate()
                    except (TypeError, ValueError) as e:
                        raise ValueError(f'Unable to load config from file: {e}')
                except yaml.YAMLError as e:
                    log.error(f'Unable to load config file: {e}')
        except FileNotFoundError as e:
            log.error(f'Unable to find given config file: {e}')

    def save(self):
        """
        Saves current configuration to the file given during initialization
        If validation fails, file will not be updated
        """
        try:
            self._validate()
            try:
                attributes_to_ignore = [
                    'filename',
                    'scan_active'
                ]
                data = {k: v for k, v in vars(self).items() if k not in attributes_to_ignore}
                for k, v in data.items():
                    if isinstance(v, timedelta):
                        data[k] = v.total_seconds()
                with open(self.filename, 'w') as f:
                    f.write(yaml.dump(data))
                    log.info('Updated config file')
            except FileNotFoundError as e:
                log.error(f'Unable to find config file: {e}')
        except ValueError as e:
            log.error(f'Current configuration not valid: {e}')
            log.warning('Configuration file not updated')

    def _set_defaults(self):
        self.room_name = 'New AccessPoint'
        self.backend_address = 'http://example.com'
        self.token = None

        self.get_config_interval = timedelta(seconds=5)
        self.collect_data_interval = timedelta(seconds=10)
        self.transfer_data_interval = timedelta(seconds=30)
        
        self.scan_active = False    # not stored in config file
        self.scan_duration = timedelta(seconds=30)

    def update_from_dict(self, data):
        """
        Updates the configuration with values from a dictionary
        Additional keys are ignored
        Values are not validated
        Call save() afterwards to make changes persistent
        """
        if 'room_name' in data:       self.room_name = data['room_name']
        if 'backend_address' in data: self.backend_address = data['backend_address']
        if 'token' in data:           self.token = data['token']

        if 'get_config_interval' in data:     self.get_config_interval = timedelta(seconds=data['get_config_interval'])
        if 'collect_data_interval' in data:   self.collect_data_interval = timedelta(seconds=data['collect_data_interval'])
        if 'transfer_data_interval' in data:  self.transfer_data_interval = timedelta(seconds=data['transfer_data_interval'])

        if 'scan_active' in data:   self.scan_active = bool(data['scan_active'])
        if 'scan_duration' in data: self.scan_duration = timedelta(seconds=data['scan_duration'])

    def _validate(self):
        def describe_wrong_type(name, type):
            return f'Expected value of {type.__name__} for {name}'
        
        def describe_limits(name, lower, upper, unit=''):
            return f'Expected value of {name} between {lower}{unit} and {upper}{unit}'

        if not isinstance(self.room_name, str):
            raise ValueError(describe_wrong_type('room_name', str))
        if not isinstance(self.backend_address, str):
            raise ValueError(describe_wrong_type('backend_address', str))
        if not isinstance(self.get_config_interval, timedelta):
            raise ValueError(describe_wrong_type('get_config_interval', timedelta))
        if not isinstance(self.collect_data_interval, timedelta):
            raise ValueError(describe_wrong_type('collect_data_interval', timedelta))
        if not isinstance(self.transfer_data_interval, timedelta):
            raise ValueError(describe_wrong_type('transfer_data_interval', timedelta))
        if not isinstance(self.scan_duration, timedelta):
            raise ValueError(describe_wrong_type('scan_duration', timedelta))

        if not timedelta(seconds=1) <= self.get_config_interval <= timedelta(seconds=10):
            raise ValueError(describe_limits('get_config_interval', 1, 10, 'sec'))
        if not timedelta(seconds=1) <= self.collect_data_interval <= timedelta(seconds=3600):
            raise ValueError(describe_limits('collect_data_interval', 1, 3600, 'sec'))
        if not self.collect_data_interval <= self.transfer_data_interval <= timedelta(seconds=3600):
            raise ValueError(describe_limits('transfer_data_interval', self.collect_data_interval, 3600, 'sec'))
        if not timedelta(seconds=10) <= self.scan_duration <= timedelta(seconds=120):
            raise ValueError(describe_limits('scan_duration', 10, 120, 'sec'))
        
        if not validators.url(self.backend_address):
            raise ValueError('Expected valid URL for backend_address')
