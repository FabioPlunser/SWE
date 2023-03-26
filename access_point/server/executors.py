import logging
import yaml

from .server import Server
from database import Database
from util import Config, CONFIG_FILENAME

log = logging.getLogger()

def get_config(conf: Config):
    backend = Server(conf.backend_address, conf.token)
    database = Database()
    new_conf_data, sensor_stations_to_add, sensor_stations_to_remove = {}, [], []

    # register at backend if not done yet
    if not backend.is_registered():
        try:
            conf.token = backend.register(conf.room_name)
            conf.save()
        except ConnectionError as e:
            log.error(e)
            return
    
    # get new configuration
    else:
        try:
            new_conf_data, sensor_stations_to_add, sensor_stations_to_remove = backend.get_config()
        except ValueError:
            log.warning('Token not valid anymore')
        except ConnectionError as e:
            log.error(e)

        # update configuration
        if new_conf_data:
            conf.update_from_dict(new_conf_data)
            conf.save()
        
        # update database
        if sensor_stations_to_add:
            for sensor_station in sensor_stations_to_add:
                database.enable_sensor_station(sensor_station.get('id'))        
        if sensor_stations_to_remove:
            for sensor_station in sensor_stations_to_remove:
                database.disable_sensor_station(sensor_station.get('id'))

def transfer_data(conf: Config):
    # log.info('Transfering data to backend')
    pass