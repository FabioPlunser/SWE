import logging

from datetime import timedelta

from server import Server
from database import Database
from util import Config, DB_FILENAME, SENSOR_STATION_NAME
from sensors import SensorStation, scan_for_new_stations, UnableToConnectError

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
    new_stations = scan_for_new_stations(known_addresses, SENSOR_STATION_NAME, timedelta(seconds=10))
    log.info(f'Found {len(new_stations)} potential new sensor stations')

    # get required data
    report_data = []
    for station in new_stations:
        try:
            report_data.append({
                'address': station.address,
                'dip-switch': station.dip_id
            })
        except UnableToConnectError:
            log.warning(f'Failed to connect to station {station.address}')

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