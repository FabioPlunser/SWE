import logging
import asyncio

from datetime import timedelta
from bleak import BleakClient, exc

from server import Server
from database import Database
from util import Config, DB_FILENAME, SENSOR_STATION_NAME
from sensors import SensorStation, scan_for_new_stations

log = logging.getLogger()


###########################################
# PROCEDURE: scan for new sensor stations #
###########################################

def find_stations(conf: Config):
    backend = Server(conf.backend_address, conf.token)
    database = Database(DB_FILENAME)

    # start scan, ignore known stations
    log.info('Starting to scan for sensor stations')
    known_addresses = database.get_all_known_sensor_station_addresses()
    new_station_addresses = scan_for_new_stations(known_addresses, SENSOR_STATION_NAME, timedelta(seconds=10))
    log.info(f'Found {len(new_station_addresses)} potential new sensor stations')

    # get required data
    report_data = []
    for address in new_station_addresses:
        try:
            dip_id = asyncio.run(get_dip_id(address))
            report_data.append({
                'address': address,
                'dip-switch': dip_id
            })
        except (exc.BleakDeviceNotFoundError, exc.BleakDBusError, exc.BleakError, asyncio.TimeoutError) as e:
            log.warning(f'Unable to read DIP id from sensor station {address}: {e}')

    # remove stations that have been enabled while scanning
    known_addresses = database.get_all_known_sensor_station_addresses()
    report_data = [entry for entry in report_data if entry.get('address') not in known_addresses]

    # send data to backend
    if report_data:
        try:
            backend.report_found_sensor_station(report_data)
            log.info(f'Reported {len(report_data)} found sensor stations to backend')
        except ConnectionError as e:
            log.error(e)
            return
    else:
        log.info('Did not find any new sensor stations to report to backend')

    conf.update(scan_active=False)
    log.info(f'Disabled scanning mode')

async def get_dip_id(address: str) -> int:
    database = Database(DB_FILENAME)
    async with BleakClient(address) as client:
        sensor_station = SensorStation(address, client)
        return await sensor_station.dip_id
    
