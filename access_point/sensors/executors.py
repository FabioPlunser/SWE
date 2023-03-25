import logging
import asyncio
from bleak import BleakScanner, BleakClient

log = logging.getLogger()

def find_stations(conf: dict):
    log.info('Starting to scan for sensor stations')
    devices = asyncio.run(scan(conf['scan_duration'] - 1))
    log.info(f'Found {len(devices)} Bluetooth devices')

async def scan(timeout):
    devices = await BleakScanner.discover(timeout=timeout)
    for d in devices:
        print(f'Device: {d}')
        continue
        async with BleakClient(d.address) as client:
            svcs = await client.get_services()
            print('Services:')
            for service in svcs:
                print(f'\t{service}')
    return devices

def collect_data(conf: dict):
    #log.info('Collecting data from sensor stations')
    pass