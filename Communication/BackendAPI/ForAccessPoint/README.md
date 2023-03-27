
## Backend API for Access Point

### Authentication

Authentication for an Access Point requires:

- The "User-Agent"-Header needs to be specified and have the Value "AccessPoint".
- The "Authorization"-Header has to be specified and have a JSON-Value containing the Access Point's Token:
```json
{
    "token": "[INSERT-TOKEN-HERE]"
}
```

### Endpoints

##### Register Access Point

> Should be queried by the Access Point after Startup.  

- Endpoint: /ap/register
- Methods: 
    - POST
- No additional Headers
- No Parameters
- Body:
    ```json
        {
            "id": "[INSERT-ACCESS-POINT-ID-HERE]",
            "room_name": "[INSERT-ACCESS-POINT-ROOM-NAME-HERE]"
        }
    ```
- Responses: 
    Same Response as "/ap/get-config"

##### Get Configuration Endpoint

> Should be queried in a regular Interval by the Access Point.

- Endpoint: /ap/get-config
- Type: 
    - GET
- No additional Headers
- No Parameters
- No Body
- Responses:
    - If the Access Point is locked:
        ```json
            {
                "success": true,
                "locked": true,
            }
        ```
    - If the Sensor Station is unlocked, the Configuration is returned:
        ```json
            {
                "success": true,
                "locked": false,
                "pairing-mode": true or false,
                "transfer-interval": [INSERT-TRANSFER-INTERVAL-HERE],
                "sensor-stations": [
                    {
                        "id": "[INSERT-SENSOR-STATION-ID-HERE]",
                        "locked": true or false,
                        "limits": { ... }   
                    },
                    {
                        "id": "[INSERT-SENSOR-STATION-ID-HERE]",
                        "locked": true or false,
                        "limits": { ... }   
                    },
                    ...
                ]
            }
        ```
    - If an Error occured during Authentication or in the Backend:
        ```json
            {
                "success": false
            }
        ```

##### Transfer Sensor Data

> Called by the Access Point after every Transfer Interval.
> 
> Transfer all Sensor Data that was collected by the Access Point from the Sensor Stations to the Backend.  
> The Access Point can safely delete the Sensor Data after getting the Response from the Backend.

- Endpoint: /ap/transfer-data
- Type: 
    - POST
- No additional Headers
- No Parameters
- Body:
    ```json
        {
            "sensor-stations": {
                "[INSERT-SENSOR-STATION-1-ID]": [
                    {
                        "timestamp": "[INSERT_TIME_STAMP_HERE]",
                        "data": [
                            {
                                "sensor": "[INSERT_SENSOR_NAME-HERE]",
                                "value": "[INSERT_SENSOR_VALUE_HERE]",
                                "unit": "[INSERT-UNIT-HERE]"
                            },
                            {
                                "sensor": "[INSERT_SENSOR_NAME-HERE]",
                                "value": "[INSERT_SENSOR_VALUE_HERE]",
                                "unit": "[INSERT-UNIT-HERE]"
                            },
                            ...
                        ]
                        ...
                    },
                    {
                        "timestamp": "[INSERT_TIME_STAMP_HERE]",
                        "data": [
                            {
                                "sensor": "[INSERT_SENSOR_NAME-HERE]",
                                "value": "[INSERT_SENSOR_VALUE_HERE]",
                                "unit": "[INSERT-UNIT-HERE]"
                            },
                            {
                                "sensor": "[INSERT_SENSOR_NAME-HERE]",
                                "value": "[INSERT_SENSOR_VALUE_HERE]",
                                "unit": "[INSERT-UNIT-HERE]"
                            },
                            ...
                        ]
                        ...
                    },
                    ...
                ],
                "[INSERT-SENSOR-STATION-2-ID]": [
                    ...
                ],
                ...
            }
        }
    ```
- Responses:
    - The Data can safely be deleted on the Access Point after a Response with `"success": true` was received.
    ```json
        {
            "success": true or false
        }
    ```

##### Found Sensor Station

> Called by the Access Point in Pairing Mode after finding one or more Sensor Stations.  

- Endpoint: /ap/sensor-station
- Type: 
    - PUT
- No additional Headers
- No Parameters
- Body: 
    ```json
        {
            "sensor-stations": [
                "[INSERT-FIRST-SENSOR-STATION-UUID-HERE]",
                "[INSERT-SECOND-SENSOR-STATION-UUID-HERE]",
                ...
            ]
        }
    ```
- Response:
    ```json
        {
            "success": true or false
        }
    ```

##### Lost Sensor Station

> Called by the Access Point after losing connection to a Sensor Station.

- Endpoint: /ap/sensor-station
- Type: 
    - DELETE
- No additional Headers
- No Parameters
- Body: 
    ```json
        {
            "sensor-station": "[INSERT-SENSOR-STATION-UUID-HERE]"
        }
    ```
- Response:
    ```json
        {
            "success": true or false
        }
    ```

