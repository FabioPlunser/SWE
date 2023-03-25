import logging
import yaml

from .server import Server
from database import Database
from util import config, CONFIG_FILENAME

log = logging.getLogger()

def get_config(conf: dict):
    backend = Server(conf['backend_address'], conf.get('token'))
    database = Database()
    new_conf, sensor_stations_to_add, sensor_stations_to_remove = {}, [], []

    # register at backend if not done yet
    if not backend.is_registered():
        try:
            backend.register(conf['room_name'])
        except ConnectionError as e:
            log.error(e)
            return
    
    # get new configuration
    if backend.is_registered():
        try:
            new_conf, sensor_stations_to_add, sensor_stations_to_remove = backend.get_config()
        except ValueError:
            log.warning('Token not valid anymore')
        except ConnectionError as e:
            log.error(e)

        # extend new configuration with current values
        for k, v in conf.items():
            if k not in new_conf:
                new_conf[k] = v
        # remove empty entries
        new_conf = {k: v for k, v in new_conf.items() if v is not None}
        # set token entry
        new_conf['token'] = backend.token

        # validate new configuration
        try:
            config.validate(new_conf)
            # update config (and file) if necessary
            if not conf == new_conf:    
                conf.clear()
                conf.update(new_conf)
                with open(CONFIG_FILENAME, 'w') as f:
                    f.write(yaml.dump(conf))
                    log.info('Updated config file')
            
        except (KeyError, ValueError) as e:
            log.error(f'Invalid config: {e}')
        except FileNotFoundError as e:
            log.error(f'Unable to open file')
        
        # update database
        if sensor_stations_to_add:
            for sensor_station in sensor_stations_to_add:
                database.enable_sensor_station(sensor_station.get('id'))        
        if sensor_stations_to_remove:
            for sensor_station in sensor_stations_to_remove:
                database.disable_sensor_station(sensor_station.get('id'))

def transfer_data(conf: dict):
    # log.info('Transfering data to backend')
    pass