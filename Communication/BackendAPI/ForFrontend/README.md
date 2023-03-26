
## Backend API for Frontend

### Authentication

Authentication for a User requires:

- The "User-Agent"-Header needs to be specified but can have any Value.
- The "Authorization"-Header has to be specified and have a JSON-Value containing the User's Username and Token:
```json
{
    "username": "[INSERT-USERNAME-HERE]",
    "token": "[INSERT-TOKEN-HERE]"
}
```

### Public Endpoints

> **No Authentication is needed for Public Endpoints.**

##### Login User

- Endpoint: /login
- Methods: 
    - GET
    - POST
- No additional Headers
- Parameters:
    - If Method == GET:
        - "username" = "[INSERT-USERNAME-HERE]"
        - "password" = "[INSERT-PASSWORD-HERE]"
    - If Method == POST:
        - No Parameters
- Body:
    - If Method == GET:
        - No Body
    - If Method == POST:
        ```json
            {
                "username": "[INSERT-USERNAME-HERE]",
                "password": "[INSERT-PASSWORD-HERE]"
            }
        ```
- Responses:
    - If the Authentication was successful:
        ```json
            {
                "success": true,
                "token": "[INSERT-TOKEN-HERE]",
                "permissions": [
                    "[PERMISSION-1]",
                    "[PERMISSION-2]",
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

##### Register

- Endpoint: /register
- Methods: 
    - GET 
    - POST
- No additional Headers
- Parameters:
    - If Method == GET:
        - "username" = "[INSERT-USERNAME-HERE]"
        - "password" = "[INSERT-PASSWORD-HERE]"
    - If Method == POST:
        - No Parameters
- Body:
    - If Method == GET:
        - No Body
    - If Method == POST:
        ```json
            {
                "username": "[INSERT-USERNAME-HERE]",
                "password": "[INSERT-PASSWORD-HERE]"
            }
        ```
- Responses:
    - If the Authentication was successful:
        ```json
            {
                "success": true,
                "token": "[INSERT-TOKEN-HERE]",
                "permissions": [
                    "[PERMISSION-1]",
                    "[PERMISSION-2]",
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

##### Scan QR Code

> Used by Frontend to get Plant Details when Guest scans a QR-Code on a Plant.  
> The Frontend also calls the "/get-pictures"-Endpoint to get the Pictures of the Plant.

- Endpoint: /scan-qr-code
- Methods: 
    - GET
- No additional Headers
- Parameters:
    - "plant-id" = "[INSERT-QR-CODE-ID-HERE]"
- No Body
- Responses:
    - If the Plant with the given Id was found:
        ```json
            {
                "success": true,
                "plant": {
                    "name": "[INSERT-PLANT-NAME-HERE]",
                    "room-name": "[INSERT-ROOM-NAME-HERE]"
                }
            }
        ```
    - If the Plant could not be found:
        ```json
            {
                "success": false
            }
        ```

##### Get Plant Pictures

- Endpoint: /get-pictures
- Methods: 
    - GET
- No additional Headers
- Parameters:
    - "plant-id" = "[INSERT-PLANT-ID-HERE]"
- No Body
- Responses:
    - If the Plant with the given Id was found:
        ```json
            {
                "success": true,
                "pictures": [
                    "[INSERT-ENCODED-PICTURE-HERE]",
                    "[INSERT-ENCODED-PICTURE-HERE]",
                    ...
                ]
            }
        ```
    - If the Plant could not be found:
        ```json
            {
                "success": false
            }
        ```

##### Upload Plant Picture

- Endpoint: /upload-pictures
- Methods: 
    - POST
    - PUT
- No additional Headers
- Parameters:
    - "plant-id" = "[INSERT-PLANT-ID-HERE]"
- Body:
    ```json
        {
            "picture": "[INSERT-ENCODED-PICTURE-HERE]"
        }
    ```
- Responses:
    - If the Picture was saved:
        ```json
            {
                "success": true
            }
        ```
    - If the Plant could not be found:
        ```json
            {
                "success": false
            }
        ```





### User Endpoints

> **User Endpoints require the querying User to have the Permission "USER"**

###### Logout

- Endpoint: /logout
- Methods: 
    - GET
    - POST
- No additional Headers
- No Parameters
- No Body
- Response:
    ```json
        {
            "success": true or false
        }
    ```

###### Get Dashboard Data

- Endpoint: /get-dashboard-data
- Methods: 
    - GET
- No additional Headers
- No Parameters
- No Body
- Response:
    ```json
        {
            "success": true,
            "plants": [
                {
                    "plant-name": "[INSERT-PLANT-NAME-HERE]",
                    "data": [
                        [INSERT-PLANT-SENSOR-VALUES-HERE],
                        [INSERT-PLANT-SENSOR-VALUES-HERE],
                        ...
                    ]
                },
                ...
            ]
        }
    ```

##### Get all Plants

- Endpoint: /get-all-plants
- Methods: 
    - GET
- No additional Headers
- No Parameters
- No Body
- Response:
    ```json
        {
            "success": true,
            "plants": [
                {
                    "id": "[INSERT-PLANT-ID-HERE]",
                    "plant-name": "[INSERT-PLANT-NAME-HERE]",
                    "room-name": [INSERT-ROOM-NAME-HERE]"
                },
                {
                    "id": "[INSERT-PLANT-ID-HERE]",
                    "plant-name": "[INSERT-PLANT-NAME-HERE]",
                    "room-name": [INSERT-ROOM-NAME-HERE]"
                },
                ...
            ]
        }
    ```

##### Add Plant to Dashboard

- Endpoint: /add-to-dashboard
- Methods: 
    - PUT
    - POST
- No additional Headers
- No Parameters
- Body:
    ```json
        {
            "id": "[INSERT-PLANT-ID-HERE]"
        }
    ```
- Response:
    ```json
        {
            "success": true or false
        }
    ```

##### Remove Plant from Dashboard

- Endpoint: /remove-from-dashboard
- Methods: 
    - UPDATE
    - DELETE
- No additional Headers
- No Parameters
- Body:
    ```json
        {
            "id": "[INSERT-PLANT-ID-HERE]"
        }
    ```
- Response:
    ```json
        {
            "success": true or false
        }
    ```
    







### Gardener Endpoints

> **Gardener Endpoints require the querying User to have the Permission "GARDENER" and to be assigned to the Plant that they want to modify**

###### Set Sensor Limits

- Endpoint: /set-sensor-limits
- Methods: 
    - PUT
    - POST
- No additional Headers
- No Parameters
- Body:
    ```json
        {
            "id": "[INSERT-PLANT-ID-HERE]",
            "limits: { ... }
        }
    ```
- Response:
    ```json
        {
            "success": true or false
        }
    ```

###### Set Transfer Interval

- Endpoint: /set-transfer-interval
- Methods: 
    - PUT
    - POST
- No additional Headers
- No Parameters
- Body:
    ```json
        {
            "id": "[INSERT-PLANT-ID-HERE]",
            "transfer-interval: { ... }
        }
    ```
- Response:
    ```json
        {
            "success": true or false
        }
    ```

###### Add Plant Picture

- See "/upload-pictures"

###### Delete Plant Picture

**TODO: Pictures should be stored in the DB with their Path and Id**




### Admin Endpoints

> **Admin Endpoints require the querying User to have the Permission "ADMIN"**

###### Get Access Points

- Endpoint: /get-access-points
- Methods: 
    - GET
- No additional Headers
- No Parameters
- No Body
- Response:
    ```json
        {
            "success": true,
            "access-points": [
                {
                    "id": "[INSERT-ACCESS-POINT-ID-HERE]",
                    "room-name": "[INSERT-ACCESS-POINT-ROOM-NAME-HERE]",
                    "locked": true of false
                },
                {
                    "id": "[INSERT-ACCESS-POINT-ID-HERE]",
                    "room-name": "[INSERT-ACCESS-POINT-ROOM-NAME-HERE]",
                    "locked": true of false
                },
                ...
            ]
        }
    ```

###### Get Sensor Stations

- Endpoint: /get-sensor-stations
- Methods: 
    - GET
- No additional Headers
- No Parameters
- No Body
- Response:
    ```json
        {
            "success": true,
            "sensor-stations": [
                {
                    "id": "[INSERT-SENSOR-STATION-ID-HERE]",
                    "dip-switch": 0 - 255
                    "locked": true of false
                },
                {
                    "id": "[INSERT-SENSOR-STATION-ID-HERE]",
                    "dip-switch": 0 - 255
                    "locked": true of false
                },
                ...
            ]
        }
    ```

###### Scan for SensorStations

> This Endpoint might take a while to finish.  
> First the "pairing-mode"-Flag in the given Access Point's Config is set.  
> Then the Access Point gets the Config and starts to scan for Sensor Stations.  
> Then it reports it's findings back to the Backend.  
> Only at this point can the Sensor Stations be displayed.  
>
> This Endpoint only returns locked (and newly found) Sensor Stations.

- Endpoint: /scan-for-sensor-stations
- Methods: 
    - POST
- No additional Headers
- No Parameters
- Body:
    ```json
        {
            "access-point-id": "[INSERT-ACCESS-POINT-ID-HERE]"
        }
    ```
- Response:
    ```json
        {
            "success": true or false,
            "sensor-stations": [
                {
                    "id": "[INSERT-SENSOR-STATION-ID-HERE]",
                    "dip-switch": 0 - 255
                },
                {
                    "id": "[INSERT-SENSOR-STATION-ID-HERE]",
                    "dip-switch": 0 - 255
                },
                ...
            ]
        }
    ```

###### Lock/Unlock Access Point

- Endpoint: /set-lock-access-point
- Methods: 
    - POST
    - PUT
- No additional Headers
- No Parameters
- Body:
    ```json
        {
            "access-point-id": "[INSERT-ACCESS-POINT-ID-HERE]",
            "locked": true or false
        }
    ```
- Response:
    ```json
        {
            "success": true or false
        }
    ```

###### Lock/Unlock SensorStation

- Endpoint: /set-lock-sensor-station
- Methods: 
    - POST
    - PUT
- No additional Headers
- No Parameters
- Body:
    ```json
        {
            "sensor-station-id": "[INSERT-SENSOR-STATION-ID-HERE]",
            "locked": true or false
        }
    ```
- Response:
    ```json
        {
            "success": true or false
        }
    ```

###### Create Plant QR-Code

- Endpoint: /create-plant-qr-code
- Methods: 
    - GET
    - POST
    - PUT
- No additional Headers
- No Parameters
- Body:
    ```json
        {
            "plant-id": "[INSERT-PLANT-ID-HERE]",
            "qr-code-id": "[INSERT-QR-CODE-ID-HERE]"
        }
    ```
- Response:
    ```json
        {
            "success": true or false
        }
    ```

###### Add User

- Endpoint: /create-user
- Methods: 
    - POST
    - PUT
- No additional Headers
- No Parameters
- Body:
    ```json
        {
            "username": "[INSERT-USERNAME-HERE]",
            "password": "[INSERT-PASSWORD-HERE]",
            "email": "[INSERT-EMAIL-HERE]",
            "permissions": [
                "[INSERT-PERMISSION-HERE]",
                "[INSERT-PERMISSION-HERE]",
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

###### Update User

> NOTE: All Fields of the JSON in the Body are optional => Only explicitly set Fields are updated.

- Endpoint: /update-user
- Methods: 
    - POST
- No additional Headers
- No Parameters
- Body:
    ```json
        {
            "username": "[INSERT-USERNAME-HERE]",
            "password": "[INSERT-PASSWORD-HERE]",
            "email": "[INSERT-EMAIL-HERE]",
            "permissions": [
                "[INSERT-PERMISSION-HERE]",
                "[INSERT-PERMISSION-HERE]",
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

###### Delete User

- Endpoint: /delete-user
- Methods: 
    - DELETE
- No additional Headers
- No Parameters
- Body:
    ```json
        {
            "id": "[INSERT-USER-ID-HERE]"
        }
    ```
- Response:
    ```json
        {
            "success": true or false
        }
    ```

###### Get Logs
- Endpoint: /get-logs
- Methods: 
    - GET
- No additional Headers
- No Parameters
- No Body
- Response:
    ```json
        {
            "success": true or false,
            "logs": [
                {
                    "severity": "...",
                    "message": "...",
                    "timestamp": "..."
                },
                {
                    "severity": "...",
                    "message": "...",
                    "timestamp": "..."
                },
                ...
            ]
        }
    ```

