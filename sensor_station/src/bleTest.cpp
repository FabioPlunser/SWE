#include "Defines.h"

#ifdef DO_BLE_TEST

#include <Arduino.h>
// #include <modules/communication.h>
#include <ArduinoBLE.h>

void setup() {
	Serial.begin(115200);
	// initialize_communication();
	// enable_pairing_mode();
	bool correct = true;

	BLEService arduino_info_service("dea07cc4-d084-11ed-a760-325096b39f47");

	if (!arduino_info_service) {
		Serial.print("Arduin info service failes.");
		while (true) {
			;
		}
	}

	BLECharacteristic battery_level_status_characteristic(
		"2BED", 0, BLERead | BLEIndicate, 6
	);

	if (!battery_level_status_characteristic) {
		Serial.print("Battery_level_status_characteristic failes.");
		while (true) {
			;
		}
	}

	// bool initialize_communication() {

	BLE.setDeviceName("SensorStation");
	BLE.setLocalName("SensorStation");

	BLE.addService(arduino_info_service);

	arduino_info_service.addCharacteristic(battery_level_status_characteristic);

	BLE.setAdvertisedService(arduino_info_service);
	BLE.advertise();
}

void loop() {}

#endif