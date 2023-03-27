
// ----- Imports and Constant Definitions-----  

#include <ArduinoBLE.h>

#include <modules/communication.h>

// TODO: Better Error Handling and move into Header File
// Helper Macro for checking Errors returned from Functions.
// If Condition evaluates to false, the Program hangs. 
#define CHECK_ERROR(__cond) \
do { \
    if (!(__cond)) { \
        while (1); \
    } \
} while(0)

// ----- Service and Characteristic Declaration -----  

BLEService arduino_info_service("19B10000-E8F2-537E-4F6C-D104768A1214");

BLECharacteristic battery_level_status_characteristic(
    "2BED", 0, BLERead | BLENotify, 6
);

// "User Index" Characteristic = uint8_t
BLECharacteristic dip_switch_id_characteristic(
    "2A9A", 0, BLERead | BLENotify, 1
);

BLECharacteristic sensor_station_unlocked_characteristic(
    "2AE2", false, BLERead | BLEWrite, 1
);

// ----- Sensor Data Characteristics

BLEService sensor_info_service("19B10000-E8F2-537E-4F6C-D104768A1215");

BLECharacteristic sensor_values_read_characteristic(
    "2AE2", true, BLERead | BLEWrite, 1
);

// -----------

BLECharacteristic earth_humidity_characteristic(
    "2A6F", 0, BLERead | BLENotify, 2
);

BLECharacteristic air_humidity_characteristic(
    "2A6F", 0, BLERead | BLENotify, 2
);

BLECharacteristic air_pressure_characteristic(
    "2A6D", 0, BLERead | BLENotify, 4
);

BLECharacteristic temparature_characteristic(
    "2AFF", 0, BLERead | BLENotify, 2
);

BLECharacteristic air_quality_characteristic(
    "2B0D", 0, BLERead | BLENotify, 2
);

BLECharacteristic light_intensity_characteristic(
    "2B04", 0, BLERead | BLENotify, 2
);

// -----------

BLECharacteristic earth_humidity_valid_characteristic(
    "2AE2", true, BLERead | BLEWrite, 1
);

BLECharacteristic air_humidity_valid_characteristic(
    "2AE2", true, BLERead | BLEWrite, 1
);

BLECharacteristic air_pressure_valid_characteristic(
    "2AE2", true, BLERead | BLEWrite, 1
);

BLECharacteristic temparature_valid_characteristic(
    "2AE2", true, BLERead | BLEWrite, 1
);

BLECharacteristic air_quality_valid_characteristic(
    "2AE2", true, BLERead | BLEWrite, 1
);

BLECharacteristic light_intensity_valid_characteristic(
    "2AE2", true, BLERead | BLEWrite, 1
);

// ----- Function Implementations -----

// ----- Startup -----

bool initialize_communication() {
    CHECK_ERROR(BLE.begin());

	BLE.setDeviceName("SensorStation");
	BLE.setLocalName("SensorStation");
    
    arduino_info_service.addCharacteristic(battery_level_status_characteristic);
    arduino_info_service.addCharacteristic(dip_switch_id_characteristic);
    arduino_info_service.addCharacteristic(sensor_station_unlocked_characteristic);

    sensor_info_service.addCharacteristic(sensor_values_read_characteristic);

    sensor_info_service.addCharacteristic(earth_humidity_characteristic);
    sensor_info_service.addCharacteristic(air_humidity_characteristic);
    sensor_info_service.addCharacteristic(air_pressure_characteristic);
    sensor_info_service.addCharacteristic(air_quality_characteristic);
    sensor_info_service.addCharacteristic(temparature_characteristic);
    sensor_info_service.addCharacteristic(light_intensity_characteristic);

    sensor_info_service.addCharacteristic(earth_humidity_valid_characteristic);
    sensor_info_service.addCharacteristic(air_humidity_valid_characteristic);
    sensor_info_service.addCharacteristic(air_pressure_valid_characteristic);
    sensor_info_service.addCharacteristic(air_quality_valid_characteristic);
    sensor_info_service.addCharacteristic(temparature_valid_characteristic);
    sensor_info_service.addCharacteristic(light_intensity_valid_characteristic);

    BLE.addService(arduino_info_service);
    BLE.addService(sensor_info_service);

    BLE.setAdvertisedService(arduino_info_service);
    // TODO: When do we need to advertise this?
    // BLE.setAdvertisedService(arduino_info_service);

    return true;
}

// ----- Pairing Mode -----

void enable_pairing_mode() {
    BLE.advertise();
}

void disable_pairing_mode() {
    BLE.stopAdvertise();
}

// ----- Set Event Handler -----

void set_connected_event_handler(void (*handler)()) {
    BLE.setEventHandler(BLEConnected, (BLEDeviceEventHandler) handler);
}

void set_disconnected_event_handler(void (*handler)()) {
    BLE.setEventHandler(BLEDisconnected, (BLEDeviceEventHandler) handler);
}

void set_sensor_data_read_flag_set_event_handler(void (*handler)()) {
    sensor_values_read_characteristic.setEventHandler(BLEWritten, (BLECharacteristicEventHandler) handler);
}

void set_unlocked_flag_set_event_handler(void (*handler)()) {
    sensor_station_unlocked_characteristic.setEventHandler(BLEWritten, (BLECharacteristicEventHandler) handler);
}

void set_limit_violation_event_handler(void (*handler)()) {
    // TODO
}

// ----- Set Event Handler -----

void clear_sensor_data_read_flag() {
    sensor_values_read_characteristic.writeValue((uint8_t) false);
}

void set_sensor_data(sensor_data_t sensor_data) {
    // TODO
    clear_sensor_data_read_flag();
}

void set_battery_level_status(battery_level_status_t battery_level_status) {
    // TODO
}

void set_dip_switch_id(uint8_t id) {
    dip_switch_id_characteristic.writeValue(id);
}

// ----------
