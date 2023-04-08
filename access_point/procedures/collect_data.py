import logging
import asyncio

from datetime import datetime, timedelta
from bleak import BleakClient, exc

from util import Config, DB_FILENAME
from sensors import SensorStation
from database import Database

log = logging.getLogger()


#############################################################
# PROCEDURE: collect data from all assigned sensor stations #
#############################################################

def collect_data(conf: Config):
    database = Database(DB_FILENAME)
    addresses = database.get_all_known_sensor_station_addresses()

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

            # set DIP id (defined by DIP switches)
            database.set_dip_id(sensor_station_address=address,
                                dip_id=await sensor_station.dip_id)
            
            # read sensor data if new available
            if not await sensor_station.sensor_data_read:        
                sensor_values = await sensor_station.sensor_data
                log.info(f'Got data for {len(sensor_values)} sensors from sensor station {address}')
                # get all sensor data
                for sensor_name, value in sensor_values.items():
                    # get current alarm settings
                    (lower_limit,
                    upper_limit,
                    alarm_tripping_time,
                    last_inside_limits) = database.get_limits(sensor_station_address=address,
                                                            sensor_name=sensor_name)
                    
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
                    database.add_measurement(sensor_station_address=address,
                                            sensor_name=sensor_name,
                                            unit=None,
                                            timestamp=timestamp,
                                            value=value,
                                            alarm=alarms[sensor_name])
                    
                    # set data read flag on station
                    await sensor_station.set_sensor_data_read(True)
            
            # set alarms on sensor station
            await sensor_station.set_alarms(alarms)
    except (exc.BleakDeviceNotFoundError, exc.BleakDBusError, exc.BleakError, asyncio.TimeoutError) as e:
        log.error(f'Unable to connect to sensor station {address}: {e}')
        database.set_connection_lost(address)

