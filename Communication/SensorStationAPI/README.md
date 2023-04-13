
# Services

## Sensor Station Info Service

Custom Service identified by ID: dea07cc4-d084-11ed-a760-325096b39f47

### Characteristics 

- **Battery Status**
    - Read, Notify
    - GATT: "Battery Level Status" (Id: 0x2BED)
    - Size: 6 Bytes

> Indicates the current Battery Level and Charging Situation of the Sensor Station.

- **DIP Switch**
    - Read, Notify
    - GATT: "User Index" (Id: 0x2A9A)
    - Size: 1 Byte

> Indicates the current State of the DIP Switch.

- **Sensor Station unlocked**
    - Read, Write
    - GATT: "Boolean" (Id: 0x2AE2)
    - Size: 1 Byte  

> Can be written by the Access Point to tell the Sensor Station 
> if it is locked or unlocked.

- **Sensor Station ID**
    - Read
    - GATT: "Object Type" (Id: 0x2ABF)
    - Size: 16 Bytes

> Universally unique ID stored on the Sensor Station.
> Should never change over the Lifecycle of a Sensor Station. 

## Sensor Info Service

Custom Service identified by ID: dea07cc4-d084-11ed-a760-325096b39f48

### Characteristics

- **Sensor Data Read**
    - Read, Write
    - GATT: "Boolean" (Id: 0x2AE2)
    - Size: 1 Byte

> Can be written by the Access Point to tell the Sensor Station 
> that Sensor Values were read.

> The Sensor Station can indicate whether new Sensor Values are available by clearing the **Sensor Data Read**-Characteristic.  
> Because of this the Access Point should check that the **Sensor Data Read**-Characteristic is not set before reading the Sensor 
> Values and set it after having read all the Sensor Values to indicate to the Sensor Station that it should read new Values

## Earth Humidity Sensor Service

Custom Service identified by ID: dea07cc4-d084-11ed-a760-325096b39f49

- **Value**
    - Read
    - GATT: "Humidity" (Id: 0x2A6F)
    - Size: 2 Bytes
    - Unit per GATT specification: %
    - Resolution per GATT specification: 0.01

- **Alarm**
    - Read, Write
    - GATT: "User Index" (Id: 0x2A9A)
    - Size: 1 Byte
    - Values:
        - 0: No alarm
        - 1: Lower threshold exceeded
        - 2: Upper threshold exceeded

## Air Humidity Sensor Service

Custom Service identified by ID: dea07cc4-d084-11ed-a760-325096b39f4a

- **Value**
    - Read
    - GATT: "Humidity" (Id: 0x2A6F)
    - Size: 2 Bytes  
    - Unit per GATT specification: %
    - Resolution per GATT specification: 0.01

- **Alarm**
    - Read, Write
    - GATT: "User Index" (Id: 0x2A9A)
    - Size: 1 Byte
    - Values:
        - 0: No alarm
        - 1: Lower threshold exceeded
        - 2: Upper threshold exceeded

## Air Pressure Sensor Service

Custom Service identified by ID: dea07cc4-d084-11ed-a760-325096b39f4b

- **Value**
    - Read
    - GATT: "Pressure" (Id: 0x2A6D)
    - Size: 4 Bytes
    - Unit per GATT specification: Pa
    - Resolution per GATT specification: 0.1

- **Alarm**
    - Read, Write
    - GATT: "User Index" (Id: 0x2A9A)
    - Size: 1 Byte
    - Values:
        - 0: No alarm
        - 1: Lower threshold exceeded
        - 2: Upper threshold exceeded

## Temperature Sensor Service

Custom Service identified by ID: dea07cc4-d084-11ed-a760-325096b39f4c

- **Temperature Value**
    - Readonly
    - GATT: "Temperature 8" (Id: 0x2B0D)
    - Size: 2 Bytes
    - Unit per GATT specification: °C
    - Resolution per GATT specification: 0.5

- **Alarm**
    - Read, Write
    - GATT: "User Index" (Id: 0x2A9A)
    - Size: 1 Byte
    - Values:
        - 0: No alarm
        - 1: Lower threshold exceeded
        - 2: Upper threshold exceeded  

## Air Quality Sensor Service

Custom Service identified by ID: dea07cc4-d084-11ed-a760-325096b39f4d

- **Air Quality Value**
    - Read
    - GATT: "Percentage 8" (Id: 0x2B04)
    - Size: 2 Bytes 
    - Values:
    - Unit per GATT specification: %
    - Resolution per GATT specification: 0.5

- **Alarm**
    - Read, Write
    - GATT: "User Index" (Id: 0x2A9A)
    - Size: 1 Byte
    - Values:
        - 0: No alarm
        - 1: Lower threshold exceeded
        - 2: Upper threshold exceeded  

## Light Intensity Sensor Service

Custom Service identified by ID: dea07cc4-d084-11ed-a760-325096b39f4e

- **Light Intensity Value**
    - Read
    - GATT: "Luminous Flux" (Id: 0x2AFF)
    - Size: 2 Bytes
    - Unit per GATT specification: lm
    - Resolution per GATT specification: 1

- **Alarm**
    - Read, Write
    - GATT: "User Index" (Id: 0x2A9A)
    - Size: 1 Byte
    - Values:
        - 0: No alarm
        - 1: Lower threshold exceeded
        - 2: Upper threshold exceeded
