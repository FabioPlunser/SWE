
# Services

## Sensor Station Info Service

Custom Service identified by ID: dea07cc4-d084-11ed-a760-325096b39f47

### Characteristics 

- **Battery Status**
    - Readonly, Notify
    - GATT: "Battery Level Status" (Id: 0x2BED)
    - Size: 6 Bytes

> Indicates the current Battery Level and Charging Situation of the Sensor Station.

- **DIP Switch**
    - Readonly, Notify
    - GATT: "User Index" (Id: 0x2A9A)
    - Size: 1 Byte  
        - Descriptors:  
            - Read  
            - GATT: "Characteristic User Description" (Id: 0x2901)  
            - Value: "DIP-Switch"  
            - Size: String Size = 10 Bytes  

> Indicates the current State of the DIP Switch.

- **Sensor Station unlocked**
    - Read/Write
    - GATT: "Boolean" (Id: 0x2AE2)
    - Size: 1 Byte  
        - Descriptors:  
            - Read  
            - GATT: "Characteristic User Description" (Id: 0x2901)  
            - Value: "Unlocked"  
            - Size: String Size = 8 Bytes  

> Can be written by the Access Point to tell the Sensor Station 
> if it is locked or unlocked.

- **Sensor Station ID**
    - Readonly
    - GATT: "Object Type" (Id: 0x2ABF)
    - Size: 16 Bytes  
        - Descriptors:  
            - Read  
            - GATT: "Characteristic User Description" (Id: 0x2901)  
            - Value: "Arduino UUID"  
            - Size: String Size = 12 Bytes  

> Universally unique ID stored on the Sensor Station.
> Should never change over the Lifecycle of a Sensor Station. 

## Sensor Info Service

Custom Service identified by ID: dea07cc4-d084-11ed-a760-325096b39f48

### Characteristics

- **Sensor Data Read**
    - Read/Write, Notify
    - GATT: "Boolean" (Id: 0x2AE2)
    - Size: 1 Byte

> Can be written by the Access Point to tell the Sensor Station 
> that the current Sensor Values were read.

#### Sensor Value Characteristics

> These Characteristics are written by the SensorStation and read by the Access Point. 
> They hold the most recently measured Value of the Sensors.
>
> The Sensor Station can indicate whether new Sensor Values are available by clearing the **Sensor Data Read**-Characteristic.  
> Because of this the Access Point should check that the **Sensor Data Read**-Characteristic is not set before reading the Sensor 
> Values and set it after having read all the Sensor Values to indicate to the Sensor Station that it should read new Values.

- **Earth Humidity Value**
    - Readonly
    - GATT: "Humidity" (Id: 0x2A6F)
    - Size: 2 Bytes  
        - Descriptors:  
            - Read  
            - GATT: "Characteristic User Description" (Id: 0x2901)  
            - Value: "Earth Humidity"  
            - Size: String Size = 14 Bytes  

- **Air Humidity Value**
    - Readonly
    - GATT: "Humidity" (Id: 0x2A6F)
    - Size: 2 Bytes  
        - Descriptors:  
            - Read  
            - GATT: "Characteristic User Description" (Id: 0x2901)  
            - Value: "Air Humidity"  
            - Size: String Size = 12 Bytes  

- **Air Pressure Value**
    - Readonly
    - GATT: "Pressure" (Id: 0x2A6D)
    - Size: 4 Bytes  
        - Descriptors:  
            - Read  
            - GATT: "Characteristic User Description" (Id: 0x2901)  
            - Value: "Air Pressure"  
            - Size: String Size = 12 Bytes  

- **Temparature Value**
    - Readonly
    - GATT: "Temperature 8 " (Id: 0x2B0D)
    - Size: 2 Bytes  
        - Descriptors:  
            - Read  
            - GATT: "Characteristic User Description" (Id: 0x2901)  
            - Value: "Temparature"  
            - Size: String Size = 11 Bytes  

- **Air Quality Value**
    - Readonly
    - GATT: "Percentage 8 " (Id: 0x2B04)
    - Size: 2 Bytes  
        - Descriptors:  
            - Read  
            - GATT: "Characteristic User Description" (Id: 0x2901)  
            - Value: "Air Quality"  
            - Size: String Size = 11 Bytes  

- **Light Intensity Value**
    - Readonly
    - GATT: "Luminous Flux " (Id: 0x2AFF)
    - Size: 2 Bytes  
        - Descriptors:  
            - Read  
            - GATT: "Characteristic User Description" (Id: 0x2901)  
            - Value: "Light Intensity"  
            - Size: String Size = 15 Bytes  

#### Sensor Values Valid Characteristics

> These Characteristics are written by the Access Point and read by the Sensor Station.
> They indicate whether the associated Sensor Value is inside the Bounds defined by the Gardener.

- **Earth Humidity Value Valid**
    - Read/Write
    - GATT: "Boolean" (Id: 0x2AE2)
    - Size: 1 Byte  
        - Descriptors:  
            - Read  
            - GATT: "Characteristic User Description" (Id: 0x2901)  
            - Value: "Earth Humidity"  
            - Size: String Size = 14 Bytes  

- **Air Humidity Value Valid**
    - Read/Write
    - GATT: "Boolean" (Id: 0x2AE2)
    - Size: 1 Byte  
        - Descriptors:  
            - Read  
            - GATT: "Characteristic User Description" (Id: 0x2901)  
            - Value: "Air Humidity"  
            - Size: String Size = 12 Bytes  

- **Air Pressure Value Valid**
    - Read/Write
    - GATT: "Boolean" (Id: 0x2AE2)
    - Size: 1 Byte  
        - Descriptors:  
            - Read  
            - GATT: "Characteristic User Description" (Id: 0x2901)  
            - Value: "Air Pressure"  
            - Size: String Size = 12 Bytes  

- **Temparature Value Valid**
    - Read/Write
    - GATT: "Boolean" (Id: 0x2AE2)
    - Size: 1 Byte  
        - Descriptors:  
            - Read  
            - GATT: "Characteristic User Description" (Id: 0x2901)  
            - Value: "Temparature"  
            - Size: String Size = 11 Bytes  

- **Air Quality Value Valid**
    - Read/Write
    - GATT: "Boolean" (Id: 0x2AE2)
    - Size: 1 Byte  
        - Descriptors:  
            - Read  
            - GATT: "Characteristic User Description" (Id: 0x2901)  
            - Value: "Air Quality"  
            - Size: String Size = 11 Bytes  

- **Light Intensity Value Valid**
    - Read/Write
    - GATT: "Boolean" (Id: 0x2AE2)
    - Size: 1 Byte  
        - Descriptors:  
            - Read  
            - GATT: "Characteristic User Description" (Id: 0x2901)  
            - Value: "Light Intensity"  
            - Size: String Size = 15 Bytes  

