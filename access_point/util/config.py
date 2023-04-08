import validators
import yaml

from datetime import timedelta

class Config(object):
    ATTRIBUTES_NO_FILESAVE = [
                    'filename',
                    'scan_active'
                ]

    def __init__(self, filename: str):
        self._filename = filename    # not stored in config file
        self._set_defaults()
        self._load()

    def _set_defaults(self):
        self._room_name = 'New AccessPoint'
        self._backend_address = None
        self._token = None

        self._get_config_interval = timedelta(seconds=5)
        self._collect_data_interval = timedelta(seconds=10)
        self._transfer_data_interval = timedelta(seconds=30)
        
        self._scan_active = False    # not stored in config file
        self._scan_duration = timedelta(seconds=30)

        self._debug = False

    @property
    def room_name(self):
        return self._room_name

    @property
    def backend_address(self):
        return self._backend_address
    
    @property
    def token(self):
        return self._token
    
    @property
    def get_config_interval(self):
        return self._get_config_interval
    
    @property
    def collect_data_interval(self):
        return self._collect_data_interval
    
    @property
    def transfer_data_interval(self):
        return self._transfer_data_interval
    
    @property
    def scan_active(self):
        return self._scan_active
    
    @property
    def scan_duration(self):
        return self._scan_duration
    
    @property
    def debug(self):
        return self._debug

    def update(
            self,
            room_name=None,
            backend_address=None,
            token=None,
            get_config_interval=None,
            collect_data_interval=None,
            transfer_data_interval=None,
            scan_active=None,
            scan_duration=None,
            debug=None,
            **kwargs
        ):
        """
        Updates the configuration with the given values.
        Validates before updating and throws a ValueError if validation fails.
        """
        data = {k: v for k, v in locals().items() if k != 'self'}
        self._validate(**data)

        current_state = self._get_cleaned_vars()
        change_found = len([v for k, v in locals().items() if k in current_state and v is not None and v != current_state[k]]) > 0

        if change_found:
            if room_name:       self._room_name = room_name
            if backend_address: self._backend_address = backend_address
            if token:           self._token = token

            if get_config_interval:     self._get_config_interval = timedelta(seconds=get_config_interval)
            if collect_data_interval:   self._collect_data_interval = timedelta(seconds=collect_data_interval)
            if transfer_data_interval:  self._transfer_data_interval = timedelta(seconds=transfer_data_interval)

            if scan_active is not None: self._scan_active = scan_active
            if scan_duration:           self._scan_duration = timedelta(seconds=scan_duration)

            if debug is not None:       self._debug = debug

            self._save()

    def reset_token(self):
        self._token = None
        self._save()

    def _load(self):
        """
        Loads configuration from the file given during initialization
        If validation fails, a ValueError will be raised
        """
        try:
            with open(self._filename, 'r') as f:
                try:
                    data = yaml.load(f, Loader=yaml.loader.SafeLoader)
                    try:
                        self.update(**data)
                    except (TypeError, ValueError) as e:
                        raise ValueError(e)
                except yaml.YAMLError as e:
                    raise ValueError(e)
        except FileNotFoundError as e:
            raise ValueError(e)

    def _save(self):
        """
        Saves current configuration to the file given during initialization
        """
        try:
            data = self._get_cleaned_vars()
            data = {k: v for k, v in data.items() if k not in Config.ATTRIBUTES_NO_FILESAVE and v is not None}
            with open(self._filename, 'w') as f:
                f.write(yaml.dump(data))
        except FileNotFoundError as e:
            raise ValueError(e)
    
    def _validate(
            self,
            room_name=None,
            backend_address=None,
            token=None,
            get_config_interval=None,
            collect_data_interval=None,
            transfer_data_interval=None,
            scan_active=None,
            scan_duration=None,
            debug=None,
            **kwargs
        ):

        def describe_wrong_type(name, type):
            return f'Expected value of type {type.__name__} for {name}'
        
        def describe_limits(name, lower, upper, unit=''):
            return f'Expected value of {name} between {lower}{unit} and {upper}{unit}'

        if room_name and not isinstance(room_name, str):
            raise ValueError(describe_wrong_type('room_name', str))
        if backend_address and not isinstance(backend_address, str):
            raise ValueError(describe_wrong_type('backend_address', str))
        if token and not isinstance(token, str):
            raise ValueError(describe_wrong_type('token', str))
        if get_config_interval and not isinstance(get_config_interval, int):
            raise ValueError(describe_wrong_type('get_config_interval', int))
        if collect_data_interval and not isinstance(collect_data_interval, int):
            raise ValueError(describe_wrong_type('collect_data_interval', int))
        if transfer_data_interval and not isinstance(transfer_data_interval, int):
            raise ValueError(describe_wrong_type('transfer_data_interval', int))
        if scan_active is not None and not isinstance(scan_active, bool):
            raise ValueError(describe_wrong_type('scan_active', bool))
        if scan_duration and not isinstance(scan_duration, int):
            raise ValueError(describe_wrong_type('scan_duration', int))
        if debug and not isinstance(debug, bool):
            raise ValueError(describe_wrong_type('debug', bool))

        if get_config_interval and not 1 <= get_config_interval <= 10:
            raise ValueError(describe_limits('get_config_interval', 1, 10, 'sec'))
        if collect_data_interval and not 1 <= collect_data_interval <= 3600:
            raise ValueError(describe_limits('collect_data_interval', 1, 3600, 'sec'))
        if transfer_data_interval:
            if collect_data_interval:
                transfer_data_interval_lower_limit = collect_data_interval
            else:
                transfer_data_interval_lower_limit = self._collect_data_interval.total_seconds()
            if not transfer_data_interval_lower_limit <= transfer_data_interval <= 3600:
                raise ValueError(describe_limits('transfer_data_interval', transfer_data_interval_lower_limit, 3600, 'sec'))
        if scan_duration and not 10 <= scan_duration <= 120:
            raise ValueError(describe_limits('scan_duration', 10, 120, 'sec'))
        
        if not backend_address and not self._backend_address:
            raise ValueError('Expected a value for backend_address')
        if backend_address and not validators.url(backend_address):
            raise ValueError('Expected valid URL for backend_address')
        
    def _get_cleaned_vars(self):
        # vars(self) has leading '_' for each key -> remove that leading '_'
        data = {k[1:]: v for k, v in vars(self).items()}
        # transform all timedelta objects to an integer value representing their total seconds
        data = {k: int(v.total_seconds()) if isinstance(v, timedelta) else v for k, v in data.items()}
        return data