
#include "Defines.h"

#ifdef DO_MAIN

#include "SensorClasses/AirSensor.cpp"
#include "SensorClasses/DipSwitch.cpp"
#include "SensorClasses/Hydrometer.cpp"
#include "SensorClasses/Phototransistor.cpp"

#include <Adafruit_BME680.h>
#include <Arduino.h>
#include <ArduinoBLE.h>
#include <modules/communication.h>

void setSensorValuesInBLE();

AirSensorClass * airSensor;
HydrometerClass * hydrometer;
PhototransistorClass * phototransistor;
DipSwitchClass * dipSwitch;
Adafruit_BME680 bme680;

void setup() {
	Serial.begin(115200);
	airSensor				= new AirSensorClass(&bme680);
	hydrometer				= new HydrometerClass(PIN_HYDROMETER);
	phototransistor			= new PhototransistorClass(PIN_PHOTOTRANSISTOR);
	uint8_t pinConnection[] = {PIN_DIP_1, PIN_DIP_2, PIN_DIP_3, PIN_DIP_4,
							   PIN_DIP_5, PIN_DIP_6, PIN_DIP_7, PIN_DIP_8};
	dipSwitch				= new DipSwitchClass(pinConnection, 8);

	initialize_communication();
	enable_pairing_mode();
}

void loop() {
	BLEDevice central = BLE.central();
	if (central) {
		Serial.println("* Connected to central device!");
		Serial.print("* Device MAC address: ");
		Serial.println(central.address());
		Serial.println(" ");
		while (central.connected()) {
			setSensorValuesInBLE();
		}
		Serial.println("* Disconnected from central device!");
	}
	Serial.println("Searching for central device!");
	delay(500);
}

void setSensorValuesInBLE() {
	float pressure, temperature, humidity;
	uint32_t gas_resistance;
	AirSensorClass::UPDATE_ERROR updateError = airSensor->getMeasuredValues(
		&pressure, &gas_resistance, &temperature, &humidity
	);
	sensor_data_t sensorData = {
		.earth_humidity	 = hydrometer->getHumidity_10bit(),
		.air_humidity	 = humidity,
		.air_pressure	 = pressure,
		.air_quality	 = gas_resistance,
		.temperature	 = temperature,
		.light_intensity = phototransistor->getLighting_10bit()};
	if (updateError == AirSensorClass::UPDATE_ERROR::NOTHING) {
		set_sensor_data(sensorData);
	}
	set_dip_switch_id(dipSwitch->getdipSwitchValue());
}

#endif