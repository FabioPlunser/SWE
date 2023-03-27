
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

*REMARKS FM:*
- I suggest to use the "Authorization" header as ```Authorization: "Bearer <token>"``` to be more conform with general standards.

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

*REMARKS FM:*
- What is the field "id" within the body and why is it needed? Is "room_name" alone not sufficient?
- Response should contain token required for authorization, nothing else
- What status code should be expected in case the user has not confirmed the request to register yet?


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

*REMARKS FM:*
- "success" field is unnecessary, use status code instead
- What is the expected behaviour after being locked? What should the access point do? What will happen, if the Get Configuration Endpoint is polled again?
- To confirm - we always include all sensor stations with which the access point should be communicating.
- Which status code to expect in case:
    - The access point is just being locked
    - The access point has previously been locked
    - A wrong token has been used

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

*REMARKS FM:*
- Use status code 200 in response instead of `"success": true`
- Alternative body format to keep keys static and include alarms and lost connection information - open for discussion:

    ```json
    {
        "sensor-stations": [
            {
                "id": [SENSOR-STATION-ID],
                "connection-alive": [TRUE/FALSE],
                "alarms": [
                    {
                        "sensor": [SENSOR-NAME],
                        "upper-limit": [TRUE/FALSE],
                        "lower-limit": [TRUE/FALSE]
                    },
                    ...
                ],
                "values": {
                    [
                        {
                            "timestamp": [TIMESTAMP],
                            "data": {
                                [
                                    {
                                        "sensor": [SENSOR-NAME],
                                        "value": [VALUE],
                                        "unit": [UNIT]
                                    },
                                    ...
                                ]
                            }
                        },
                        ...
                    ]
                }
            }
        ]
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
*REMARKS FM:*
- Verify endpoint url
- Use status code 200 in response instead of `"success": true`

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

*REMARKS FM:*
- Verify endpoint url
- Use status code 200 in response instead of `"success": true`
- After loosing a sensor station and finding it again - will the sensor station have to be reconfirmed by an admin? I suggest not.
- Also possible: Include information on lost sensor stations in request to api/transfer-data, discard this endpoint

