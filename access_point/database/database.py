import logging
import sqlite3

from util import DB_FILENAME

log = logging.getLogger()

class Database:
    def __init__(self):
        self.conn = None
        try:
            conn = sqlite3.connect(DB_FILENAME)
        except sqlite3.Error as e:
            log.error(f'Unable to open database file: {e}')
    
    def __del__(self):
        if self.conn:
            self.conn.close()

    def enable_sensor_station(self, id):
        log.info(f'Enabling sensor station {id}')

    def disable_sensor_station(self, id):
        log.info(f'Disabling sensor station {id}')