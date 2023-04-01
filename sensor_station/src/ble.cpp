
// ----- Imports and Constant Definitions-----

using namespace std;

#import "string"

#include <ArduinoBLE.h>
#include <modules/communication.h>

// TODO: Better Error Handling and move into Header File
// Helper Macro for checking Errors returned from Functions.
// If Condition evaluates to false, the Program hangs.
#define CHECK_ERROR(__cond) \
	do {                    \
		if (!(__cond)) {    \
			while (1)       \
				;           \
		}                   \
	} while (0)

// ----- Service and Characteristic Declaration -----

BLEService arduino_info_service("dea07cc4-d084-11ed-a760-325096b39f47");

BLECharacteristic
	battery_level_status_characteristic("2BED", 0, BLERead | BLENotify, 6);

// "User Index" Characteristic = uint8_t
BLECharacteristic
	dip_switch_id_characteristic("2A9A", 0, BLERead | BLENotify, 1);
BLEDescriptor dip_switch_id_user_descriptor("2901", "DIP-Switch");

BLECharacteristic sensor_station_unlocked_characteristic(
	"2AE2", false, BLERead | BLEWrite, 1
);
BLEDescriptor sensor_station_unlocked_user_descriptor("2901", "Unlocked");

BLECharacteristic sensor_station_id_characteristic("2ABF", false, BLERead, 16);
BLEDescriptor sensor_station_id_user_descriptor("2901", "Arduino UUID");

// ----- Sensor Data Characteristics

BLEService sensor_info_service("dea07cc4-d084-11ed-a760-325096b39f48");

BLECharacteristic
	sensor_values_read_characteristic("2AE2", true, BLERead | BLEWrite, 1);

// -----------

BLECharacteristic
	earth_humidity_characteristic("2A6F", 0, BLERead | BLENotify, 2);

BLECharacteristic
	air_humidity_characteristic("2A6F", 0, BLERead | BLENotify, 2);

BLECharacteristic
	air_pressure_characteristic("2A6D", 0, BLERead | BLENotify, 4);

BLECharacteristic temparature_characteristic("2AFF", 0, BLERead | BLENotify, 2);

BLECharacteristic air_quality_characteristic("2B0D", 0, BLERead | BLENotify, 2);

BLECharacteristic
	light_intensity_characteristic("2B04", 0, BLERead | BLENotify, 2);

// -----------

BLECharacteristic
	earth_humidity_valid_characteristic("2AE2", true, BLERead | BLEWrite, 1);

BLECharacteristic
	air_humidity_valid_characteristic("2AE2", true, BLERead | BLEWrite, 1);

BLECharacteristic
	air_pressure_valid_characteristic("2AE2", true, BLERead | BLEWrite, 1);

BLECharacteristic
	temparature_valid_characteristic("2AE2", true, BLERead | BLEWrite, 1);

BLECharacteristic
	air_quality_valid_characteristic("2AE2", true, BLERead | BLEWrite, 1);

BLECharacteristic
	light_intensity_valid_characteristic("2AE2", true, BLERead | BLEWrite, 1);

// -----------

// TODO: Check if i need to declare these seperately
//       for each Sensor Value and Sensor Valid Chararacteristic
BLEDescriptor earth_humidity_user_descriptor("0x2901", "Earth Humidity");
BLEDescriptor air_humidity_user_descriptor("0x2901", "Air Humidity");
BLEDescriptor air_pressure_user_descriptor("0x2901", "Air Pressure");
BLEDescriptor temparature_user_descriptor("0x2901", "Temparature");
BLEDescriptor air_quality_user_descriptor("0x2901", "Air Quality");
BLEDescriptor light_intensity_user_descriptor("0x2901", "Light Intensity");

// ----- Function Implementations -----

// ----- Startup -----

bool initialize_communication() {
	CHECK_ERROR(BLE.begin());

	BLE.setDeviceName("SensorStation");
	BLE.setLocalName("SensorStation");

	arduino_info_service.addCharacteristic(battery_level_status_characteristic);
	arduino_info_service.addCharacteristic(dip_switch_id_characteristic);
	arduino_info_service.addCharacteristic(
		sensor_station_unlocked_characteristic
	);
	arduino_info_service.addCharacteristic(sensor_station_id_characteristic);

	dip_switch_id_characteristic.addDescriptor(dip_switch_id_user_descriptor);
	sensor_station_unlocked_characteristic.addDescriptor(
		sensor_station_unlocked_user_descriptor
	);
	sensor_station_id_characteristic.addDescriptor();

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

	earth_humidity_characteristic.addDescriptor(earth_humidity_user_descriptor);
	air_humidity_characteristic.addDescriptor(air_pressure_user_descriptor);
	air_pressure_characteristic.addDescriptor(air_pressure_user_descriptor);
	air_quality_characteristic.addDescriptor(air_quality_user_descriptor);
	temparature_characteristic.addDescriptor(temparature_user_descriptor);
	light_intensity_characteristic.addDescriptor(light_intensity_user_descriptor
	);

	earth_humidity_valid_characteristic.addDescriptor(
		earth_humidity_user_descriptor
	);
	air_humidity_valid_characteristic.addDescriptor(air_pressure_user_descriptor
	);
	air_pressure_valid_characteristic.addDescriptor(air_pressure_user_descriptor
	);
	air_quality_valid_characteristic.addDescriptor(air_quality_user_descriptor);
	temparature_valid_characteristic.addDescriptor(temparature_user_descriptor);
	light_intensity_valid_characteristic.addDescriptor(
		light_intensity_user_descriptor
	);

	BLE.addService(arduino_info_service);
	BLE.addService(sensor_info_service);

	BLE.setAdvertisedService(arduino_info_service);
	// TODO: When do we need to advertise this?
	// BLE.setAdvertisedService(arduino_info_service);

	return true;
}

// ----- Pairing Mode -----

void enable_pairing_mode() { BLE.advertise(); }

void disable_pairing_mode() { BLE.stopAdvertise(); }

// ----- Set Event Handler -----

void set_connected_event_handler(void (*handler)()) {
	BLE.setEventHandler(BLEConnected, (BLEDeviceEventHandler) handler);
}

void set_disconnected_event_handler(void (*handler)()) {
	BLE.setEventHandler(BLEDisconnected, (BLEDeviceEventHandler) handler);
}

void set_sensor_data_read_flag_set_event_handler(void (*handler)()) {
	sensor_values_read_characteristic.setEventHandler(
		BLEWritten, (BLECharacteristicEventHandler) handler
	);
}

void set_unlocked_flag_set_event_handler(void (*handler)()) {
	sensor_station_unlocked_characteristic.setEventHandler(
		BLEWritten, (BLECharacteristicEventHandler) handler
	);
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

string get_address() { return BLE.address(); }

// ----------
