
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
AirSensorClass::UPDATE_ERROR setSensorValuesFromSensors(sensor_data_t * str);

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

	while (!Serial) {
		delay(50);
	}

	delay(1000);
}

void loop() {
	float pressure = 0, temperature = 0, humidity = 0;
	uint32_t gas_resistance	 = 0;
	uint16_t earth_humidity	 = hydrometer->getHumidity_10bit();
	uint16_t light_intensity = phototransistor->getLighting_10bit();

	AirSensorClass::UPDATE_ERROR updateError = airSensor->getMeasuredValues(
		&pressure, &gas_resistance, &temperature, &humidity
	);

	Serial.print("Value pressure: ");
	Serial.println(pressure);
	Serial.print("Value temperature: ");
	Serial.println(temperature);
	Serial.print("Value humidity: ");
	Serial.println(humidity);
	Serial.print("Value gas_resistance: ");
	Serial.println(gas_resistance);
	Serial.print("Value earth_humidity: ");
	Serial.println(earth_humidity);
	Serial.print("Value light_intensity: ");
	Serial.println(light_intensity);
	delay(10000);

	// BLEDevice central = BLE.central();
	// if (central) {
	// 	Serial.println("* Connected to central device!");
	// 	Serial.print("* Device MAC address: ");
	// 	Serial.println(central.address());
	// 	Serial.println(" ");
	// 	Serial.print("Value before connection: ");
	// 	Serial.println(get_sensor_data_read_flag());
	// 	if (central.connected()) {
	// 		setSensorValuesInBLE();
	// 		Serial.print("Value in connection: ");
	// 		Serial.println(get_sensor_data_read_flag());
	// 		while (central.connected()) {
	// 			;
	// 		}
	// 	}
	// 	Serial.println("* Disconnected from central device!");
	// 	Serial.print("Value after connection: ");
	// 	Serial.println(get_sensor_data_read_flag());
	// 	setSensorValuesInBLE();
	// 	Serial.print("Value after reset: ");
	// 	Serial.println(get_sensor_data_read_flag());
	// }
	// Serial.println("Searching for central device!");
	// delay(500);
}

// void setSensorValuesInBLE() {
// 	sensor_data_t sensorData;
// 	AirSensorClass::UPDATE_ERROR updateError =
// 		setSensorValuesFromSensors(&sensorData);
// 	if (updateError == AirSensorClass::UPDATE_ERROR::NOTHING) {
// 		set_sensor_data(sensorData);
// 	} else {
// 		ERROR_PRINT("Got update Error ", updateError);
// 	}
// 	set_dip_switch_id(dipSwitch->getdipSwitchValue());
// 	clearAllFlags();
// 	set_sensorstation_locked_status(false);
// 	battery_level_status_t batteryStatus = {0};
// 	set_battery_level_status(batteryStatus);
// 	set_sensorstation_id(0);
// }

// AirSensorClass::UPDATE_ERROR setSensorValuesFromSensors(sensor_data_t * str)
// { 	float pressure = 0, temperature = 0, humidity = 0; 	uint32_t
// gas_resistance = 0; 	uint16_t earth_humidity	 =
// hydrometer->getHumidity_10bit(); 	uint16_t light_intensity =
// phototransistor->getLighting_10bit();

// 	AirSensorClass::UPDATE_ERROR updateError = airSensor->getMeasuredValues(
// 		&pressure, &gas_resistance, &temperature, &humidity
// 	);

// 	str->earth_humidity	 = earth_humidity;
// 	str->air_humidity	 = humidity;
// 	str->air_pressure	 = pressure;
// 	str->air_quality	 = gas_resistance;
// 	str->temperature	 = temperature;
// 	str->light_intensity = light_intensity;
// 	return updateError;
// };

// uint16_t convertToGATT_soilHumidity(float humidity) {
// 	if (humidity < 0 || humidity > 100) {
// 		return convertToGATT_soilHumidity_notKnown();
// 	}
// 	return uint16_t(humidity * 100);
// }
// uint16_t convertToGATT_soilHumidity_notKnown() { return (uint16_t) 0xFFFF; }

// uint16_t convertToGATT_airHumidity(float humidity) {
// 	if (humidity < 0 || humidity > 100) {
// 		return convertToGATT_airHumidity_notKnown();
// 	}
// 	return uint16_t(humidity * 100);
// }
// uint16_t convertToGATT_airHumidity_notKnown() { return (uint16_t) 0xFFFF; }

// uint16_t convertToGATT_airPresure(float humidity) {
// 	if (humidity < 0 || humidity > 100) {
// 		return convertToGATT_soilHumidity_notKnown();
// 	}
// 	return uint16_t(humidity * 100);
// }
// uint16_t convertToGATT_airPresure_notKnown() { return (uint16_t) 0xFFFF; }

// uint16_t convertToGATT_airQuality(float humidity) {
// 	if (humidity < 0 || humidity > 100) {
// 		return convertToGATT_soilHumidity_notKnown();
// 	}
// 	return uint16_t(humidity * 100);
// }
// uint16_t convertToGATT_airQuality_notKnown() { return (uint16_t) 0xFFFF; }

// uint16_t convertToGATT_airTemperature(float humidity) {
// 	if (humidity < 0 || humidity > 100) {
// 		return convertToGATT_soilHumidity_notKnown();
// 	}
// 	return uint16_t(humidity * 100);
// }
// uint16_t convertToGATT_airTemperature_notKnown() { return (uint16_t) 0xFFFF;
// }

// uint16_t convertToGATT_lightIntensity(float humidity) {
// 	if (humidity < 0 || humidity > 100) {
// 		return convertToGATT_soilHumidity_notKnown();
// 	}
// 	return uint16_t(humidity * 100);
// }
// uint16_t convertToGATT_lightIntensity_notKnown() { return (uint16_t) 0xFFFF;
// }
#endif