create_sensor_station_table_query = """
    CREATE TABLE IF NOT EXISTS sensor_station (
        id                  integer PRIMARY KEY,
        address             text    UNIQUE,
        connection_alive    integer NOT NULL
    );
"""

create_sensor_table_query = """
    CREATE TABLE IF NOT EXISTS sensor (
        id                  integer PRIMARY KEY,
        name                text    UNIQUE,
        unit                text,
        lower_limit         float,
        upper_limit         float,
        sensor_station_id   integer NOT NULL,
        FOREIGN KEY (sensor_station_id) REFERENCES sensor_station (id)
    );
"""

create_sensor_value_table_query = """
    CREATE TABLE IF NOT EXISTS sensor_value (
        id                          integer PRIMARY KEY,
        timestamp                   text    NOT NULL,
        value                       float   NOT NULL,
        lower_limit_alarm_active    integer NOT NULL,
        upper_limit_alarm_active    integer NOT NULL,
        sensor_id                   integer NOT NULL,
        FOREIGN KEY (sensor_id) REFERENCES sensor (id)
    );
"""