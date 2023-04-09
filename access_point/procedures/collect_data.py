import logging
import asyncio

from datetime import datetime
from bleak import BleakClient

from util import Config, DB_FILENAME
from sensors import SensorStation, BLEConnectionError, ReadError, WriteError
from database import Database, DatabaseError

log = logging.getLogger()


#############################################################
# PROCEDURE: collect data from all assigned sensor stations #
#############################################################

def collect_data(conf: Config):
    database = Database(DB_FILENAME)
    try:
        addresses = database.get_all_known_sensor_station_addresses()
    except DatabaseError as e:
        log.error(f'Unable to load addresses of known sensor stations from database: {e}')
        return

    for address in addresses:
        asyncio.run(single_connection(address))

    if len(addresses) == 0:
        log.info('Did not find any assigned sensor stations')

async def single_connection(address: str):
    database = Database(DB_FILENAME)
    log.info(f'Connecting to sensor station {address}')
    try:
        async with BleakClient(address) as client:
            log.info(f'Established connection to sensor station {address}')
            sensor_station = SensorStation(address, client)
            timestamp = datetime.now()
            alarms = {}

            # set sensor station to unlocked
            await sensor_station.set_unlocked(True)

            # set DIP id (defined by DIP switches)
            try:
                database.set_dip_id(sensor_station_address=address,
                                    dip_id=await sensor_station.dip_id)
            except DatabaseError as e:
                log.error(f'Unable to update dip id for sensor station {address} in database: {e}')
                return
            
            # read sensor data if new available
            if not await sensor_station.sensor_data_read:        
                sensor_values = await sensor_station.sensor_data
                log.info(f'Got data for {len(sensor_values)} sensors from sensor station {address}')
                # get all sensor data
                for sensor_name, value in sensor_values.items():
                    # get current alarm settings
                    try:
                        (lower_limit,
                         upper_limit,
                         alarm_tripping_time,
                         last_inside_limits) = database.get_limits(sensor_station_address=address,
                                                                  sensor_name=sensor_name)
                    except DatabaseError as e:
                        log.error(f'Unable to get limits for sensor {sensor_name} on sensor station {address} from database: {e}')
                        continue
                    
                    # flag alarm if applicable
                    if (lower_limit and
                        value < lower_limit and
                        alarm_tripping_time and
                        (timestamp - last_inside_limits) > alarm_tripping_time):
                        alarms[sensor_name] = 'l'
                    elif (upper_limit and
                            value > upper_limit and
                            alarm_tripping_time and
                            (timestamp - last_inside_limits) > alarm_tripping_time):
                        alarms[sensor_name] = 'h'
                    else:
                        alarms[sensor_name] = 'n'

                    # store measurement in database
                    try:
                        database.add_measurement(sensor_station_address=address,
                                                 sensor_name=sensor_name,
                                                 unit=None,
                                                 timestamp=timestamp,
                                                 value=value,
                                                 alarm=alarms[sensor_name])
                    except DatabaseError as e:
                        log.error(f'Unable to add measurement for sensor {sensor_name} on sensor station {address} to database: {e}')
                        continue
                    
                    # set data read flag on station
                    await sensor_station.set_sensor_data_read(True)
            else:
                log.info(f'No new sensor data available on sensor station {address}')
            
            # set alarms on sensor station
            await sensor_station.set_alarms(alarms)
    except BLEConnectionError as e:
        log.error(f'Unable to connect to sensor station {address}: {e}')
        database.set_connection_lost(address)
    except ReadError as e:
        log.error(f'Unable to read value from sensor station {address}: {e}')
    except WriteError as e:
        log.error(f'Unable to write value to sensor station {address}: {e}')

