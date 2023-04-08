import asyncio
import bleak

from uuid import UUID
from bleak import BleakClient, exc
from typing import Any

# fixing wrong definition of 'Battery Level Status' characteristic UUID
bleak.uuids.uuid16_dict[0x2BED] = 'Battery Level State'

class UnableToConnectError(Exception):
    pass

class SensorStation(object):
    SENSORS = [
        'Earth Humidity',
        'Air Humidity',
        'Air Pressure',
        'Temperature',
        'Air Quality',
        'Light Intensity',
        'Battery Level'
    ]

    def __init__(self, address):
        self._address: str = address
        self._characteristics: dict[str, bytearray] = {}
        self._connection_failed = False

    @property
    def address(self) -> str:
        """
        The address of the sensor station
        """
        return self._address

    @property
    def dip_id(self) -> int:
        """
        The current position of the DIP switches
        """
        if not self._characteristics:
            self.update()
        if self._connection_failed:
            raise UnableToConnectError
        return int.from_bytes(self._characteristics.get('DIP-Switch'))

    @property
    def sensor_values(self) -> dict[str, int]:
        """
        The values of the sensors defined in SensorStation.SENSORS
        """
        if not self._characteristics:
            self.update()
        if self._connection_failed:
            raise UnableToConnectError

        sensor_values = {}
        if not bool.from_bytes(self._characteristics.get('Sensor Data Read')):
            sensor_values = {k: int.from_bytes(v) for k, v in self._characteristics.items() if k in self.SENSORS}
            asyncio.run(_write_characteristics(self._address, {'Sensor Data Read': bool.to_bytes(True)}))
            self._characteristics['Sensor Data Read'] = True.to_bytes()
        sensor_values['Battery Level'] = self.battery_level

        return sensor_values
    
    @property
    def battery_level(self) -> int:
        """
        The current battery level from 0% to 100%
        """
        if not self._characteristics:
            self.update()
        if self._connection_failed:
            raise UnableToConnectError

        battery_level_status = self._characteristics.get('Battery Level State')
        if battery_level_status:
            flags = {
                'IdentifierPresent':        bool((battery_level_status[0] >> 0) & 1),
                'BatteryLevelPresent':      bool((battery_level_status[0] >> 1) & 1),
                'AdditionalStatusPresent':  bool((battery_level_status[0] >> 2) & 1)
            }

            if flags['BatteryLevelPresent']:
                pos = 3 + 2 * flags['IdentifierPresent']
                battery_level = int.from_bytes(battery_level_status[pos:pos+1])
                return battery_level
        
        return 100
    
    def update(self) -> None:
        """
        Updates the values read from the sensor station
        """
        try:
            self._characteristics = asyncio.run(_read_all_characteristics(self._address))
        except UnableToConnectError:
            self._connection_failed = True
    
    def set_alarms(self, sensors: list[str], reset_others: bool = True) -> None:
        """
        Sets the given alarms on the sensor station
        :param sensors: A list of the sensors for which the alarms should be set. Must
            be a subset of SensorStation.SENSORS
        :param reset_others: Sets all alarms that are not specified to False
        :raises AttributeError: If an unknown sensor was specified
        """
        if [unknown_sensor for unknown_sensor in sensors if unknown_sensor not in self.SENSORS]:
            raise AttributeError

        asyncio.run(_write_characteristics(self._address,
                                           {f'{sensor} Alarm': bool.to_bytes(True if sensor in sensors else False)
                                            for sensor in (self.SENSORS if reset_others else sensors)}))

async def _read_all_characteristics(address: str) -> dict[str, bytearray]:
    values = {}
    try:
        async with BleakClient(address) as client:
            for service in client.services:
                for c in service.characteristics:
                    if not client.is_connected:
                        raise UnableToConnectError
                    try:
                        values[c.description] = await client.read_gatt_char(c)
                    except exc.BleakError as e:
                        if 'Protocol Error 0x02: Read Not Permitted' in str(e):
                            pass
                        else:
                            raise UnableToConnectError
        return values
    except (exc.BleakDeviceNotFoundError, exc.BleakError, TimeoutError):
        raise UnableToConnectError
    
async def _write_characteristics(address: str, values: dict[str, bytearray]) -> None:
    try:
        async with BleakClient(address) as client:
            for service in client.services:
                for c in service.characteristics:
                    if c.description in values:
                        if not client.is_connected:
                            raise UnableToConnectError
                        await client.write_gatt_char(c, values[c.description])
    except (exc.BleakDeviceNotFoundError, exc.BleakError, TimeoutError):
        raise UnableToConnectError
