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
            conf.update(token=backend.register('1234', conf.room_name)) #TODO: create access point UUID
        except ConnectionError as e:
            log.error(e)
            return
    
    # get new configuration
    else:
        try:
            new_config_data, sensor_stations = backend.get_config()

            # update configuration
            conf.update(**new_config_data)

            # enable/disable sensor stations
            known_sensor_station_addresses = database.get_all_known_sensor_station_addresses()
            sensor_stations_to_enable = [station.get('address')
                                         for station in sensor_stations
                                         if station.get('address') not in known_sensor_station_addresses]
            sensor_stations_to_disable = [adr
                                          for adr in known_sensor_station_addresses
                                          if adr not in [station.get('address') for station in sensor_stations]]
            if sensor_stations_to_enable: log.info(f'Enabling sensor stations: {sensor_stations_to_enable}')
            for adr in sensor_stations_to_enable:
                database.enable_sensor_station(adr)
            if sensor_stations_to_disable: log.info(f'Disabling sensor stations: {sensor_stations_to_disable}')
            for adr in sensor_stations_to_disable:
                database.disable_sensor_station(adr)

            # update limits for sensors if applicable
            for sensor_station in sensor_stations:
                address = sensor_station.get('address')
                sensors = sensor_station.get('sensors')
                if sensors:
                    log.info(f'Updating limits for sensor station {address} to {sensors}')
                    for sensor in sensors:
                        database.update_sensor_setting(sensor_station_address=address, **sensor)
            
        except TokenDeclinedError:
            log.warning('Token not valid anymore')
            conf.update(token=None)
        except ConnectionError as e:
            log.error(e)