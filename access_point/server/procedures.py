import logging
import yaml

from .server import Server, TokenDeclinedError
from database import Database
from util import Config, DB_FILENAME

log = logging.getLogger()

################################################
# PROCEDURE: update configuration from backend #
################################################

def get_config(conf: Config):
    backend = Server(conf.backend_address, conf.token)
    database = Database(DB_FILENAME)
    new_conf_data, sensor_stations_to_add, sensor_stations_to_remove = {}, [], []

    # register at backend if not done yet
    if not backend.token:
        try:
            conf.update(token=backend.register(conf.room_name))
        except ConnectionError as e:
            log.error(e)
            return
    
    # get new configuration
    else:
        try:
            new_config_data = backend.get_config()
        except TokenDeclinedError:
            log.warning('Token not valid anymore')
            conf.update(token=None)
        except ConnectionError as e:
            log.error(e)

        # update configuration
        if new_config_data:
            conf.update(**new_config_data)
        
        # update database
        if sensor_stations_to_add:
            for sensor_station in sensor_stations_to_add:
                database.enable_sensor_station(sensor_station.get('id'))        
        if sensor_stations_to_remove:
            for sensor_station in sensor_stations_to_remove:
                database.disable_sensor_station(sensor_station.get('id'))

##############################################
# PROCEDURE: transfer sensor data to backend #
##############################################

def transfer_data(conf: Config):
    # log.info('Transfering data to backend')
    pass