import bleak
import asyncio

from bleak import BleakClient, exc
from typing import Optional, Literal

# fixing wrong definition of 'Battery Level Status' characteristic UUID
bleak.uuids.uuid16_dict[0x2BED] = 'Battery Level State'


BLEConnectionError = (exc.BleakDeviceNotFoundError,
                      exc.BleakDBusError,
                      exc.BleakError,
                      asyncio.TimeoutError,
                      OSError,
                      AttributeError,
                      RuntimeError)

class NoConnectionError(Exception):
    pass

class ReadError(Exception):
    pass

class WriteError(Exception):
    pass

def get_short_uuid(uuid: str):
    return uuid[4:8]

class SensorDefinition:
    def __init__(self, name: str, unit: str, resolution: float):
        self.name = name
        self.unit = unit
        self.resolution = resolution

    def bytes_to_value(self, bytes: bytearray) -> float:
        if not bytes:
            return None
        return int.from_bytes(bytes) * self.resolution

class SensorStation:
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

    ALARM_UUID = '2a9a'
    ALARM_CODES = {
        'n': 0,
        'l': 1,
        'h': 2
    }

    def __init__(self, address:str, client: BleakClient = None):
        self.address = address
        self.client: BleakClient = client

    def _with_connection(f):
        async def decorated(self, *args, **kwargs):
            if self.client:
                return await f(self, *args, **kwargs)
            else:
                raise NoConnectionError
        return decorated
    
    @_with_connection
    async def _get_characteristic(self,
                                  description: Optional[str] = None,
                                  id: Optional[str] = None,
                                  ignore_id: Optional[str] = None):
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
                                   ignore_id: Optional[str] = None):
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
                                    data: bytearray = b'\0'):
        characteristic = await self._get_characteristic(description, id, ignore_id)
        try:
            await self.client.write_gatt_char(characteristic, data)
        except Exception as e:
            raise WriteError(f'Unable to write characteristic {description if description else id}{( "(id != " + ignore_id + ")") if ignore_id else ""}: {e}')
    
    @property
    async def dip_id(self):
        return int.from_bytes(await self._read_characteristic(description='DIP-Switch'))
    
    @property
    async def unlocked(self):
        return bool.from_bytes(await self._read_characteristic(description='Unlocked'))
    
    async def set_unlocked(self, value: bool):
        await self._write_characteristic(description='Unlocked', data=value.to_bytes())
    
    @property
    async def sensor_data_read(self):
        return bool.from_bytes(await self._read_characteristic(description='Sensor Data Read'))
    
    async def set_sensor_data_read(self, value: bool):
        await self._write_characteristic(description='Sensor Data Read', data=value.to_bytes())

    @property
    async def sensor_data(self) -> dict[str, int]:
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
    async def _battery_level_bytes(self):
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
    async def alarms(self):
        alarms = {}
        for sensor in self.SENSORS:
            try:
                alarms[sensor.name] = int.from_bytes(await self._read_characteristic(description=sensor.name, id=self.ALARM_UUID))
            except ReadError:
                # ignore read errors on alarms -> skip over currently unreadable alarms
                pass
        return alarms
    
    async def set_alarms(self, alarms: dict[str, Literal['n', 'l', 'h']]):
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
        for sensor in cls.SENSORS:
            if sensor.name == sensor_name:
                return sensor.unit
        return None
