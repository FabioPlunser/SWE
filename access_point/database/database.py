import sqlite3

from datetime import datetime, timedelta
from typing import Optional, Literal, Union

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
                self._conn.execute('PRAGMA foreign_keys = 1')
                result = f(self, *args, **kwargs)
                self._conn.commit()
                self._conn.close()
                return result
            except sqlite3.Error as e:
                raise DatabaseError(e)
        return decorated
    
    @_with_connection
    def setup(self) -> None:
        """
        Initializes all required database tables.
        """
        create_table = lambda query : self._conn.cursor().execute(query)
        
        create_table(create_sensor_station_table_query)
        create_table(create_sensor_table_query)
        create_table(create_sensor_value_table_query)

    @_with_connection
    def enable_sensor_station(self, address: str) -> None:
        """
        Adds a sensor station to the database.
        :param address: Address of the sensor station to add
        """
        query = """
            INSERT INTO sensor_station(address, timestamp_added, connection_alive)
            VALUES (?,?,?)
        """
        cursor = self._conn.cursor()
        cursor.execute(query, (address, datetime.now(), False))

    @_with_connection
    def disable_sensor_station(self, address: str) -> None:
        """
        Removes a sensor station from the database.
        Will also remove all associated sensors and measured values.
        :param address: Address of the sensor station to remove
        """
        query = """
            DELETE FROM sensor_station
            WHERE address = ?
        """
        cursor = self._conn.cursor()
        cursor.execute(query, (address,))
    
    @_with_connection
    def add_measurement(self,
                        sensor_station_address: str,
                        sensor_name: str,
                        unit: Optional[str],
                        timestamp: datetime,
                        value: int,
                        alarm: Literal['n', 'l', 'h']) -> None:
        """
        Adds a measured value for a single sensor of a given sensor station to the database.
        Will also update the timestamp of the last measured value within limits for the sensor
        and flags the connection to the sensor station as alive.
        :param sensor_station_address: Address of the sensor station
        :param sensor_name: Name of the sensor
        :param unit: Unit of the measurement - changes after initial input will be ignored
        :param timestamp: Time at which the measurement was done
        :param value: Measured value
        :param alarm: Flag for active alarm ('n' -> no alarm | 'l' -> lower treshold | 'h' -> upper treshold)
        """
        # add sensor if necessary
        query = """
            INSERT OR IGNORE INTO sensor(name, unit, sensor_station_id)
            SELECT ?, ?, st.id
            FROM sensor_station st
            WHERE st.address = ?
        """
        cursor = self._conn.cursor()
        cursor.execute(query, (sensor_name, unit, sensor_station_address))

        # add measurement
        query = """
            INSERT INTO sensor_value(timestamp, value, alarm, sensor_id)
            SELECT ?, ?, ?, s.id
            FROM sensor s
                JOIN sensor_station st ON s.sensor_station_id = st.id
            WHERE
                st.address = ? AND
                s.name = ?
        """
        cursor = self._conn.cursor()
        cursor.execute(
            query,
            (
                timestamp,
                value,
                alarm,
                sensor_station_address,
                sensor_name
            )
        )

        # update timestamp of last measurement within limits if applicable
        query = """
            UPDATE sensor
            SET last_inside_limits = ?
            WHERE
                name = ? AND
                sensor_station_id IN (
                    SELECT id
                    FROM sensor_station
                    WHERE address = ?
                ) AND
                (lower_limit IS NULL OR lower_limit <= ?) AND
                (upper_limit IS NULL OR ? <= upper_limit)
        """
        cursor.execute(query, (timestamp, sensor_name, sensor_station_address, value, value))

        # set connection to sensor station to alive
        query = """
            UPDATE sensor_station
            SET connection_alive = 1
            WHERE address = ?
        """
        cursor.execute(query, (sensor_station_address,))

    @_with_connection
    def set_dip_id(self, sensor_station_address: str, dip_id: int) -> None:
        """
        Sets the DIP switch position for a sensor station.
        :param sensor_station_address: Address of the sensor station
        :param dip_id: Integer decoded position of the DIP switches
        """
        query = """
            UPDATE sensor_station
            SET dip_id = ?
            WHERE address = ?
        """
        cursor = self._conn.cursor()
        cursor.execute(query, (dip_id, sensor_station_address))

    @_with_connection
    def set_connection_lost(self, sensor_station_address: str) -> None:
        """
        Flags the connection to a sensor station as lost.
        :param sensor_station_address: Address of the sensor station
        """
        query = """
            UPDATE sensor_station
            SET connection_alive = 0
            WHERE address = ?
        """
        cursor = self._conn.cursor()
        cursor.execute(query, (sensor_station_address,))
    
    @_with_connection
    def update_sensor_setting(self,
                              sensor_station_address: str,
                              sensor_name: str,
                              lower_limit: Optional[int] = None,
                              upper_limit: Optional[int] = None,
                              alarm_tripping_time: Optional[int] = None,
                              **kwargs) -> None:
        """
        Updates the settings for a specific sensor.
        :param sensor_station_address: Address of the sensor station
        :param sensor_name: Name of the sensor
        :param lower_limit: Lower limit for the sensor value to trigger alarms
        :param upper_limit: Upper limit for the sensor value to trigger alarms
        :param alarm_tripping_time: Time in seconds until an alarm is triggered
        """
        query = """
            UPDATE sensor
            SET
                lower_limit = ?,
                upper_limit = ?,
                alarm_tripping_time = ?
            WHERE
                name = ? AND
                sensor_station_id IN (
                    SELECT id
                    FROM sensor_station
                    WHERE address = ?)
        """
        cursor = self._conn.cursor()
        cursor.execute(
            query,
            (
                lower_limit,
                upper_limit,
                alarm_tripping_time,
                sensor_name,
                sensor_station_address
            )
        )

    @_with_connection
    def get_all_known_sensor_station_addresses(self) -> list[str]:
        """
        Gets all sensor stations that are stored in the database / enabled.
        :return: List with the addresses of all enabled sensor stations
        """
        query = """
            SELECT address
            FROM sensor_station
        """
        cursor = self._conn.cursor()
        cursor.execute(query)

        rows = cursor.fetchall()
        addresses = [adr for (adr,) in rows]

        return addresses

    @_with_connection
    def get_all_measurements(self) -> list[dict]:
        """
        Gets all measurements that are currently stored in the database.
        :return: A list of dictionaries constructed as
            {
                "id": Id of the measurement for later deletion -> int
                "sensor_station_address": Address of the sensor station -> str,
                "sensor_name": Name of the sensor -> str,
                "unit": Unit of the measured value -> str | None,
                "timestamp": Timestamp of the measurement -> datetime,
                "value": Measured value -> int,
                "alarm": Alarm active at the time of the measurement -> str ['n' no alarm | 'l' below limit | 'h' above limit]
            }
        """
        query = """
            SELECT v.id, st.address, s.name, s.unit, v.timestamp, v.value, v.alarm
            FROM sensor_station st
                JOIN sensor s on st.id = s.sensor_station_id
                JOIN sensor_value v on s.id = v.sensor_id
        """
        cursor = self._conn.cursor()
        cursor.execute(query)
        rows = cursor.fetchall()
        measurements = [
            {
                'id': id,
                'sensor_station_address': sensor_station_address,
                'sensor_name': sensor_name,
                'unit': unit,
                'timestamp': datetime.fromisoformat(timestamp),
                'value': value,
                'alarm': alarm
            } for (
                id,
                sensor_station_address,
                sensor_name,
                unit,
                timestamp,
                value,
                alarm
            ) in rows
        ]

        return measurements

    @_with_connection
    def get_limits(self,
                   sensor_station_address: str,
                   sensor_name: str) -> tuple[Optional[int], Optional[int], Optional[timedelta], datetime]:
        """
        Gets the currently set limits and timing information on limits.
        :param sensor_station_address: Address of the sensor station
        :param sensor_name: Name of the sensor
        :return: A tuple with three values
            lower_limit: The currently set lower limit
            upper_limit: The currently set upper limit
            alarm_tripping_time: The currently set time until tripping an alarm
            last_inside_limits: The time at which the value has been inside limits at last
                (or the time at which the sensor station has been enabled)
        """
        query = """
            SELECT
                s.lower_limit,
                s.upper_limit,
                s.alarm_tripping_time,
                COALESCE(s.last_inside_limits, st.timestamp_added)
            FROM sensor_station st
                LEFT OUTER JOIN (
                    SELECT *
                    FROM sensor
                    WHERE name = ?
                ) s ON st.id = s.sensor_station_id
            WHERE
                st.address = ?
        """
        cursor = self._conn.cursor()
        cursor.execute(query, (sensor_name, sensor_station_address))
        (lower_limit, upper_limit, alarm_tripping_time, last_inside_limits) = cursor.fetchone()
        alarm_tripping_time = timedelta(seconds=alarm_tripping_time) if alarm_tripping_time else None
        last_inside_limits = datetime.fromisoformat(last_inside_limits)
        return lower_limit, upper_limit, alarm_tripping_time, last_inside_limits
    
    @_with_connection
    def get_all_connection_states(self) -> dict[str, dict[str, Union[bool, Optional[int]]]]:
        """
        Gets the connection states of all known sensor stations and their currently set DIP ids
        :return: A dictionary with the sensor station addresses as keys and subordinated dictionaries:
            {
                'connection_alive': 'True' if the connection is alive, 'False' if not
                'dip_id': Integer encoded DIP switch position
            }
        """
        query = """
            SELECT address, connection_alive, dip_id
            FROM sensor_station
        """
        cursor = self._conn.cursor()
        cursor.execute(query)
        rows = cursor.fetchall()
        connection_states = {adr: {'connection_alive': bool(connection),
                                   'dip_id': dip_id}
                             for (adr, connection, dip_id) in rows}
        return connection_states
    
    @_with_connection
    def delete_all_measurements(self, ids: list[int]) -> None:
        """
        Deletes all measurements with the given ids from the database.
        NOTE: This method DOES NOT use a prepared SQL statement. Ensure
        that the ids parameter is not used for SQL injection.
        :param ids: List of IDs of measurements to delete
        """
        if len(ids) == 0:
            return
        elif len(ids) == 1:
            args = f'({ids[0]})'
        else:
            args = tuple(ids)
        query = f"""
            DELETE FROM sensor_value
            WHERE id IN {args}
        """
        cursor = self._conn.cursor()
        cursor.execute(query)