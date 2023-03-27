import sqlite3

from .table_setup import (
    create_sensor_station_table_query,
    create_sensor_table_query,
    create_sensor_value_table_query
)

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
    def setup(self):
        create_table = lambda query : self._conn.cursor().execute(query)
        
        create_table(create_sensor_station_table_query)
        create_table(create_sensor_table_query)
        create_table(create_sensor_value_table_query)

    @_with_connection
    def enable_sensor_station(self, address):
        pass

    @_with_connection
    def disable_sensor_station(self, address):
        pass

    @_with_connection
    def get_all_known_sensor_station_addresses(self) -> list:
        return []