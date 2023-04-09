import logging

from datetime import datetime

from util import Config, DB_FILENAME
from server import Server
from database import Database, DatabaseError

log = logging.getLogger()


##############################################
# PROCEDURE: transfer sensor data to backend #
##############################################

def transfer_data(conf: Config):
    backend = Server(conf.backend_address, conf.token)
    database = Database(DB_FILENAME)

    # get data
    log.info('Collecting data for transfer to backend')
    try:
        station_data = database.get_all_connection_states()
        measurements = database.get_all_measurements()
    except DatabaseError as e:
        log.error(f'Unable to load data from database: {e}')
        return
    log.info(f'Found {len(measurements)} measurements for {len(station_data)} sensor stations')

    # transfer to backend
    if len(station_data):
        try:
            log.info('Starting transfer to backend')
            backend.transfer_data(station_data, measurements)
            log.info('Completed transfer to backend')
        except ConnectionError as e:
            log.error(e)
            return
    else:
        log.info('Nothing to transfer')
    
    # delete transferred measurements from database
    measurement_ids = [m.get('id') for m in measurements]
    if measurement_ids:
        log.info('Deleting transfered measurements from database')
        try:
            database.delete_all_measurements(measurement_ids)
            log.info(f'Deleted {len(measurement_ids)} measurements from database')
        except DatabaseError as e:
            log.error(f'Unable to delete data from database: {e}')
            return
