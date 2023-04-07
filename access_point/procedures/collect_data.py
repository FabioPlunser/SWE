import logging

from datetime import datetime, timedelta

from util import Config, DB_FILENAME
from sensors import SensorStation, UnableToConnectError
from database import Database

log = logging.getLogger()


#############################################################
# PROCEDURE: collect data from all assigned sensor stations #
#############################################################

def collect_data(conf: Config):
    database = Database(DB_FILENAME)

    addresses = database.get_all_known_sensor_station_addresses()

    for address in addresses:
        sensor_station = SensorStation(address)
        log.info(f'Loading data from sensor station {address}')

        try:
            timestamp = datetime.now()
            alarms = []

            # set DIP id (defined by DIP switches)
            database.set_dip_id(sensor_station_address=address,
                                dip_id=sensor_station.dip_id)
            
            sensor_values = sensor_station.sensor_values
            log.info(f'Got data for {len(sensor_values)} sensors from sensor station {address}')
            # get all sensor data
            for sensor_name, value in sensor_values.items():
                # get current alarm settings
                (lower_limit,
                 upper_limit,
                 alarm_tripping_time,
                 last_inside_limits) = database.get_limits_and_time_outside_limits(sensor_station_address=address,
                                                                                   sensor_name=sensor_name)
                
                # flag alarm if applicable
                if (lower_limit and
                    value < lower_limit and
                    alarm_tripping_time and
                    (timestamp - last_inside_limits) > alarm_tripping_time):
                    alarm = 'l'
                elif (upper_limit and
                      value > upper_limit and
                      alarm_tripping_time and
                      (timestamp - last_inside_limits) > alarm_tripping_time):
                    alarm = 'h'
                else:
                    alarm = 'n'
                
                if alarm != 'n':
                    alarms.append(sensor_name)

                # store measurement in database
                database.add_measurement(sensor_station_address=address,
                                         sensor_name=sensor_name,
                                         unit=None,
                                         timestamp=timestamp,
                                         value=value,
                                         alarm=alarm)
            
            # set alarms on sensor station if necessary
            sensor_station.set_alarms(alarms)

        except UnableToConnectError:
            log.error(f'Could not connect to sensor station {address}')
            database.set_connection_lost(sensor_station_address=address)
