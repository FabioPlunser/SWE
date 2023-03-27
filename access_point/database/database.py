import logging
import sqlite3

class DatabaseError(Exception):
    pass

class Database:
    def __init__(self, db_filename):
        self._db_filename = db_filename
        self._conn :sqlite3.Connection = None
    
    def _with_connection(f):
        def decorated(self, *args, **kwargs):
            try:
                self._conn = sqlite3.connect(self._db_filename)
                f(self, *args, **kwargs)
                self._conn.close()
            except sqlite3.Error as e:
                raise DatabaseError(e)
        return decorated

    @_with_connection
    def enable_sensor_station(self, address):
        pass

    @_with_connection
    def disable_sensor_station(self, address):
        pass

    @_with_connection
    def get_all_known_sensor_station_addresses(self) -> list:
        return []