import bleak
import asyncio

from bleak import BleakClient, exc
from typing import Optional, Literal

# Fixing wrong definition of 'Battery Level Status' characteristic UUID
bleak.uuids.uuid16_dict[0x2BED] = 'Battery Level State'

# Summarization of all exceptions that indicate a failed connection
BLEConnectionError = (exc.BleakDeviceNotFoundError,
                      exc.BleakDBusError,
                      exc.BleakError,
                      asyncio.TimeoutError,
                      OSError,
                      AttributeError,
                      RuntimeError)

class NoConnectionError(Exception):
    """
    The class SensorStation must be provided BleakClient.
    If no BleakClient is set, but a method requiring a BleakClient
    is called, this error is raised.
    """
    pass

class ReadError(Exception):
    """
    Failed to read a characteristic from a sensor station.
    """
    pass

class WriteError(Exception):
    """
    Failed to write a characteristic to a sensor station.
    """
    pass

def get_short_uuid(uuid: str) -> str:
    """
    Extracts the relevant 4 half-bytes from the UUID to identify a characteristic type.
    """
    return uuid[4:8]

class SensorDefinition:
    """
    Definition of a sensor. Does not hold any actual sensor data
    but is used to convert raw sensor values into actual measurements.
    """
    def __init__(self, name: str, unit: str, resolution: float) -> None:
        """
        Sets the sensor definition.
        :param name: Name of the sensor
        :param unit: Unit of value measured by the sensor
        :param resolution: Resolution of the sensor - sensors provide integer output.
        """
        self.name = name
        self.unit = unit
        self.resolution = resolution

    def bytes_to_value(self, bytes: bytearray) -> float:
        """
        Converts the raw output of a sensor into an actual measurement.
        """
        if not bytes:
            return None
        return int.from_bytes(bytes) * self.resolution

class SensorStation:
    """
    Handler class for a sensor station.
    """
    SERVICE_UUIDS = [
        'dea07cc4-d084-11ed-a760-325096b39f47',
        'dea07cc4-d084-11ed-a760-325096b39f48'
    ]

    SENSORS = [
        SensorDefinition('Earth Humidity', '%', 0.01),
        SensorDefinition('Air Humidity', '%', 0.01),
        SensorDefinition('Air Pressure', 'Pa', 0.1),
        SensorDefinition('Temperature', '°C', 0.5),
        SensorDefinition('Air Quality', '%', 0.5),
        SensorDefinition('Light Intensity', 'lm', 1),
        SensorDefinition('Battery Level', '%', 1)
    ]

    # UUID of characteristics for setting alarms for sensors
    ALARM_UUID = '2a9a'
    ALARM_CODES = {
        'n': 0,
        'l': 1,
        'h': 2
    }

    def __init__(self, address:str, client: BleakClient = None) -> None:
        """
        Initializes a sensor station.
        :param address: The address of the sensor station
        :param client: The BleakClient used to communicate with the sensor
        """
        self.address = address
        self.client: BleakClient = client

    def _with_connection(f):
        """
        Internal decorator method to check if the BleakClient is properly set.
        Raises a NoConnectionError if not.
        """
        async def decorated(self, *args, **kwargs):
            if self.client and self.client.is_connected:
                return await f(self, *args, **kwargs)
            else:
                raise NoConnectionError
        return decorated
    
    @_with_connection
    async def _get_characteristic(self,
                                  description: Optional[str] = None,
                                  id: Optional[str] = None,
                                  ignore_id: Optional[str] = None) -> Optional[bleak.BleakGATTCharacteristic]:
        """
        Internal method for getting a characteristic from a sensor station.
        Use _read_characteristic() or _write_characteristic() to read or write
        the actual value.
        :param description: Description of the characteristic to find
        :param id: UUID (2 bytes) of the characteristic to find
        :param ignore_id: Characteristics with this UUID (2 bytes) will be ignored
        :return: The first matching characteristic or None of nothing has been found
        :raises ValueError: If neither description nor id are provided
        :raises ValueError: If id and ignore_id are identical
        """
        if not description and not id:
            raise ValueError('Either description or id required')
        if id and id == ignore_id:
            raise ValueError('Id and ignore_id must not be identical')
        for service in self.client.services:
            # skip irrelevant services
            if service.uuid not in self.SERVICE_UUIDS:
                continue
            # iterate through characteristics and find matching description
            for characteristic in service.characteristics:
                if ((not description or characteristic.description == description) and
                    (not id or get_short_uuid(characteristic.uuid) == id) and
                    (not ignore_id or get_short_uuid(characteristic.uuid) != ignore_id)):
                    return characteristic
    
    @_with_connection
    async def _read_characteristic(self,
                                   description: Optional[str] = None,
                                   id: Optional[str] = None,
                                   ignore_id: Optional[str] = None) -> bytearray:
        """
        Reads the value of a characteristic from the sensor station.
        :param description: Description of the characteristic to find
        :param id: UUID (2 bytes) of the characteristic to find
        :param ignore_id: Characteristics with this UUID (2 bytes) will be ignored
        :return: The raw value of the characteristic
        :raises ValueError: If neither description nor id are provided
        :raises ValueError: If id and ignore_id are identical
        :raises ReadError: If there was an error reading the value of the characteristic
        """
        characteristic = await self._get_characteristic(description, id, ignore_id)
        try:
            return await self.client.read_gatt_char(characteristic)
        except Exception as e:
            raise ReadError(f'Unable to read characteristic {description if description else id}{( "(id != " + ignore_id + ")") if ignore_id else ""}: {e}')
                    
    @_with_connection
    async def _write_characteristic(self,
                                    description: Optional[str] = None,
                                    id: Optional[str] = None,
                                    ignore_id: Optional[str] = None,
                                    data: bytearray = b'\0') -> None:
        """
        Writes the value of a characteristic on the sensor station.
        :param description: Description of the characteristic to find
        :param id: UUID (2 bytes) of the characteristic to find
        :param ignore_id: Characteristics with this UUID (2 bytes) will be ignored
        :param data: Raw data to write to the characteristic value
        :return: The raw value of the characteristic
        :raises ValueError: If neither description nor id are provided
        :raises ValueError: If id and ignore_id are identical
        :raises WriteError: If there was an error writing the value of the characteristic
        """
        characteristic = await self._get_characteristic(description, id, ignore_id)
        try:
            await self.client.write_gatt_char(characteristic, data)
        except Exception as e:
            raise WriteError(f'Unable to write characteristic {description if description else id}{( "(id != " + ignore_id + ")") if ignore_id else ""}: {e}')
    
    @property
    async def dip_id(self) -> int:
        """
        Integer encoded DIP switch position.
        :raises ReadError: If it was not possible to read the DIP switch position
        :raises NoConnectionError: If the BleakClient was not properly initialized
        """
        return int.from_bytes(await self._read_characteristic(description='DIP-Switch'))
    
    @property
    async def unlocked(self) -> bool:
        """
        Flag if the sensor station has been unlocked.
        :raises ReadError: If it was not possible to read the flag
        :raises NoConnectionError: If the BleakClient was not properly initialized 
        """
        return bool.from_bytes(await self._read_characteristic(description='Unlocked'))
    
    async def set_unlocked(self, value: bool) -> None:
        """
        Sets/resets the flag that indicates whether a sensor station has been unlocked.
        :param value: 'True' to unlock the sensor station | 'False' to lock the sensor station
        :raises WriteError: If it was not possible to write the flag
        :raises NoConnectionError: If the BleakClient was not properly initialized
        """
        await self._write_characteristic(description='Unlocked', data=value.to_bytes())
    
    @property
    async def sensor_data_read(self):
        """
        Flag that indicates if new sensor value is available on the sensor station
        :return: 'True' if data has already been read and no new data is available | 'False' otherwise
        :raises ReadError: If it was not possible to read the flag
        :raises NoConnectionError: If the BleakClient was not properly initialized
        """
        return bool.from_bytes(await self._read_characteristic(description='Sensor Data Read'))
    
    async def set_sensor_data_read(self, value: bool):
        """
        Sets/resets the flag that indicates if sensor data has been read from the station.
        :param value: 'True' to indicate that the sensor values have been read | 'False' otherwise
        :raises WriteError: If it was not possible to write the flag
        :raises NoConnectionError: If the BleakClient was not properly initialized
        """
        await self._write_characteristic(description='Sensor Data Read', data=value.to_bytes())

    @property
    async def sensor_data(self) -> dict[str, float]:
        """
        Values of the individual sensors of the sensor stations.
        Checks alls descriptors that match the names in SENSORS.
        Unreadable values will be ignored.
        :return: A dictionary with the sensor names as keys and the received values
        :raises NoConnectionError: If the BleakClient was not properly initialized
        """
        values = {}
        for sensor in self.SENSORS:
            try:
                if sensor.name == 'Battery Level':
                    values[sensor.name] = sensor.bytes_to_value(await self._battery_level_bytes)
                else:
                    values[sensor.name] = sensor.bytes_to_value(await self._read_characteristic(description=sensor.name, ignore_id=self.ALARM_UUID))
            except ReadError:
                # ignore read errors on sensor data -> skip over currently unreadable sensor values
                pass
        return values

    @property
    async def _battery_level_bytes(self) -> Optional[bytearray]:
        """
        Internal method to decode the battery level from the data provided by the battery
        level state characteristic.
        :return: The raw data containing the battery level or None if not available
        :raises ReadError: If it was not possible to read the battery level state characteristic
        :raises NoConnectionError: If the BleakClient was not properly initialized
        """
        battery_level_status = await self._read_characteristic('Battery Level State')
        if battery_level_status:
            flags = {
                'IdentifierPresent':        bool((battery_level_status[0] >> 0) & 1),
                'BatteryLevelPresent':      bool((battery_level_status[0] >> 1) & 1),
                'AdditionalStatusPresent':  bool((battery_level_status[0] >> 2) & 1)
            }
            if flags['BatteryLevelPresent']:
                pos = 3 + 2 * flags['IdentifierPresent']
                return battery_level_status[pos:pos+1]
        return None

    @property
    async def alarms(self) -> dict[str, int]:
        """
        Reads the current state of alarms set on the sensor station.
        Unreadable alarms will be ignored.
        :return: A dictionary with the sensor names as keys and the alarm states
            0 -> No alarm
            1 -> Lower limit exceeded
            2 -> Upper limit exceeded
        :raises NoConnectionError: If the BleakClient was not properly initialized
        """
        alarms = {}
        for sensor in self.SENSORS:
            try:
                alarms[sensor.name] = int.from_bytes(await self._read_characteristic(description=sensor.name, id=self.ALARM_UUID))
            except ReadError:
                # ignore read errors on alarms -> skip over currently unreadable alarms
                pass
        return alarms
    
    async def set_alarms(self, alarms: dict[str, Literal['n', 'l', 'h']]) -> None:
        """
        Sets alarms to the given state:
        :param alarms: A dictionary with the sensor names as keys and the alarm states
            'n' -> No alarm
            'l' -> Lower limit exceeded
            'h' -> Upper limit exceeded
        :raises NoConnectionError: If the BleakClient was not properly initialized
        """
        for sensor, alarm in alarms.items():
            if sensor not in [s.name for s in self.SENSORS]:
                raise ValueError(f'Sensor {sensor} is not known - alarm cannot be set')
            try:
                await self._write_characteristic(description=sensor, id=self.ALARM_UUID, data=self.ALARM_CODES[alarm].to_bytes())
            except WriteError:
                pass
            except KeyError:
                raise ValueError(f'Alarm flags must be "n", "l" or "h"')
            
    @classmethod
    def get_sensor_unit(cls, sensor_name: str) -> str:
        """
        Returns the unit for a specific sensor.
        :param sensor_name: The name of the sensor
        :return: The unit in which the measured values are
        """
        for sensor in cls.SENSORS:
            if sensor.name == sensor_name:
                return sensor.unit
        return None
