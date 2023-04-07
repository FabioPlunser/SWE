import logging

from server import Server, TokenDeclinedError
from database import Database
from util import Config, DB_FILENAME

from sensors import scan_for_new_stations
from datetime import timedelta

log = logging.getLogger()


################################################
# PROCEDURE: update configuration from backend #
################################################

def get_config(conf: Config):
    backend = Server(conf.backend_address, conf.token)
    database = Database(DB_FILENAME)    

    # register at backend if not done yet
    if not backend.token:
        try:
            conf.update(token=backend.register('TODO: create access point ID', conf.room_name))
        except ConnectionError as e:
            log.error(e)
            return
    
    # get new configuration
    else:
        try:
            new_config_data, sensor_stations = backend.get_config()
        except TokenDeclinedError:
            log.warning('Token not valid anymore')
            conf.update(token=None)
        except ConnectionError as e:
            log.error(e)