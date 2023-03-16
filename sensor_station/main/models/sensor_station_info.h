
#ifndef SENSOR_STATION_INFO_H
#define SENSOR_STATION_INFO_H

#include <stdbool.h>
#include <stdint.h>

#include "../util/uuid.h"

#define BATTERY_LEVEL_STATUS_FLAGS 0b010

typedef struct {
    uint8_t flags;
    uint16_t power_state;
    uint16_t battery_level;
} battery_level_status_t;

typedef struct {
    uuid_t uuid;
    battery_level_status_t battery_status;
    bool unlocked;
} sensor_station_info_t;

#endif
