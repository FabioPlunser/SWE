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

    log.info('Starting to scan for sensor stations')
    known_addresses = database.get_all_known_sensor_station_addresses()
    new_stations = scan_for_new_stations(known_addresses, SENSOR_STATION_NAME, timedelta(seconds=10))
    log.info(f'Found {len(new_stations)} new sensor stations')

    report_data = []
    for station in new_stations:
        try:
            report_data.append({
                'address': station.address,
                'dip-switch': station.dip_id
            })
        except UnableToConnectError:
            log.warning(f'Failed to connect to station "{station.address}"')

    backend.report_found_sensor_station(report_data)
    log.info(f'Reported found sensor stations to backend')

    conf.update(scan_active=False)
    log.info(f'Disabled scanning mode')