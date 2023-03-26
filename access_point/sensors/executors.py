import logging
import asyncio

from bleak import BleakScanner, BleakClient
from server import Server
from database import Database
from util import Config, SENSOR_STATION_NAME

log = logging.getLogger()

def find_stations(conf: Config):
    backend = Server(conf.backend_address, conf.token)
    database = Database()

    log.info('Starting to scan for sensor stations')
    sensor_station_addresses = asyncio.run(scan(conf.scan_duration.total_seconds()))
    log.info(f'Found {len(sensor_station_addresses)} new sensor stations')

async def scan(timeout):
    devices = await BleakScanner.discover(timeout=timeout)
    sensor_station_addresses = [d.address for d in devices if d.name == SENSOR_STATION_NAME]
    return sensor_station_addresses

def collect_data(conf: dict):
    #log.info('Collecting data from sensor stations')
    pass