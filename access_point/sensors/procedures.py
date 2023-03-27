import logging
import asyncio

from bleak import BleakScanner, BleakClient
from server import Server
from database import Database
from util import Config, SENSOR_STATION_NAME
from .sensor_station import SensorStation

log = logging.getLogger()

###########################################
# PROCEDURE: scan for new sensor stations #
###########################################

def find_stations(conf: Config):
    backend = Server(conf.backend_address, conf.token)
    database = Database()

    log.info('Starting to scan for sensor stations')
    sensor_station_addresses = asyncio.run(scan(conf.scan_duration.total_seconds()))
    known_addresses = database.get_all_known_sensor_station_addresses()
    sensor_station_addresses = [adr for adr in sensor_station_addresses if adr not in known_addresses]
    log.info(f'Found {len(sensor_station_addresses)} new sensor stations')
    sensor_stations = [SensorStation(adr) for adr in sensor_station_addresses]
    for sensor_station in sensor_stations:
        sensor_station.poll()

    conf.update(scan_active=False)

async def scan(timeout):
    devices = await BleakScanner.discover(timeout=timeout)
    sensor_station_addresses = [d.address for d in devices if d.name == SENSOR_STATION_NAME]
    return sensor_station_addresses


#############################################################
# PROCEDURE: collect data from all assigned sensor stations #
#############################################################

def collect_data(conf: dict):
    #log.info('Collecting data from sensor stations')
    pass