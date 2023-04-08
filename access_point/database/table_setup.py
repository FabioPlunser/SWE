create_sensor_station_table_query = """
    CREATE TABLE IF NOT EXISTS sensor_station (
        id                  integer PRIMARY KEY,
        address             text    UNIQUE,
        dip_id              integer,
        timestamp_added     text    NOT NULL,
        connection_alive    integer NOT NULL
    );
"""

create_sensor_table_query = """
    CREATE TABLE IF NOT EXISTS sensor (
        id                  integer PRIMARY KEY,
        name                text,
        unit                text,
        lower_limit         integer,
        upper_limit         integer,
        last_inside_limits  text,
        alarm_tripping_time integer,
        sensor_station_id   integer NOT NULL,
        FOREIGN KEY (sensor_station_id) REFERENCES sensor_station (id) ON DELETE CASCADE,
        UNIQUE (name, sensor_station_id)
    );
"""

create_sensor_value_table_query = """
    CREATE TABLE IF NOT EXISTS sensor_value (
        id                  integer PRIMARY KEY,
        timestamp           text    NOT NULL,
        value               integer NOT NULL,
        alarm               text    NOT NULL,
        sensor_id           integer NOT NULL,
        FOREIGN KEY (sensor_id) REFERENCES sensor (id) ON DELETE CASCADE,
        UNIQUE (timestamp, sensor_id)
    );
"""