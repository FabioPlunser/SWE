#include "Defines.h"

#include <Arduino.h>
#include <modules/communication.h>

#ifdef DO_TEST

#include "HardwareTests/AirSensorTest.cpp"
#include "HardwareTests/HydrometerTest.cpp"
#include "HardwareTests/LedTest.cpp"
#include "HardwareTests/PhototransistorTest.cpp"
#include "HardwareTests/PiezoBuzzerTest.cpp"

/**
 * This class will execute basic tests for all the connected components of the
 * arduino.
 * ----- Always -----
 * -> Will print notification on button press or change of dip switch with
 * current value in the message
 * ----- Setup only -----
 * -> First it will execute the LED test function, that will fade the color in
 * and out in the order red - green - blue. Afterwards the colours will
 * interwine with each other.
 * -> Second test is the piezo buzzer that will execute 10 logarithmic
 * equidistant sounds ranging from 100Hz to 5kHz.
 * ----- In the loop after setup -----
 * -> Will read the values from the phototransistor, the hydrometer and the air
 * quality sensors and print them periodically till the end of time.
 */

LedTest ledTest(PIN_RGB_RED, PIN_RGB_GREEN, PIN_RGB_BLUE);
PiezoBuzzerTest piezoBuzzerTest(PIN_PIEZO_BUZZER);
HydrometerTest hydrometerTest(PIN_HYDROMETER);
AirSensorTest airSensorTest;
PhototransistorTest phototransistorTest(PIN_PHOTOTRANSISTOR);

void setup() {
	Serial.begin(115200);
	// initialize_communication();
	// enable_pairing_mode();
	Serial.print("Start LED test");
	delay(1000);
	ledTest.executeTest();
	Serial.print("Finished LED test");

	Serial.print("Start piezo-buzzer test");
	delay(1000);
	piezoBuzzerTest.executeTest();
	Serial.print("Finished piezo-buzzer test");

	// Interrupt setup
	// Disable interrupts while setup
	noInterrupts();
	attachInterrupt(
		digitalPinToInterrupt(PIN_BUTTON_1), buttonFunction, CHANGE
	);
	attachInterrupt(
		digitalPinToInterrupt(PIN_BUTTON_2), buttonFunction, CHANGE
	);
	attachInterrupt(
		digitalPinToInterrupt(PIN_BUTTON_3), buttonFunction, CHANGE
	);
	attachInterrupt(
		digitalPinToInterrupt(PIN_DIP_1), dipSwitchFunction, CHANGE
	);
	attachInterrupt(
		digitalPinToInterrupt(PIN_DIP_2), dipSwitchFunction, CHANGE
	);
	attachInterrupt(
		digitalPinToInterrupt(PIN_DIP_3), dipSwitchFunction, CHANGE
	);
	attachInterrupt(
		digitalPinToInterrupt(PIN_DIP_4), dipSwitchFunction, CHANGE
	);
	attachInterrupt(
		digitalPinToInterrupt(PIN_DIP_5), dipSwitchFunction, CHANGE
	);
	attachInterrupt(
		digitalPinToInterrupt(PIN_DIP_6), dipSwitchFunction, CHANGE
	);
	attachInterrupt(
		digitalPinToInterrupt(PIN_DIP_7), dipSwitchFunction, CHANGE
	);
	attachInterrupt(
		digitalPinToInterrupt(PIN_DIP_8), dipSwitchFunction, CHANGE
	);
	// Reenable interrupts
	interrupts();
}

void loop() {
	static const int buffer_size = 128;
	char buffer[buffer_size];
	// Hydrometer
	sprintf(
		buffer, "Humidity 10 bit uint: %4d\n",
		hydrometerTest.getHumidity_10bit()
	);
	Serial.print(buffer);
	sprintf(
		buffer, "Humidity float percentage: %6.2f%%\n",
		hydrometerTest.getHumidity_percentage()
	);
	Serial.print(buffer);

	// Air sensor
	uint32_t pressure, gas_resistance;
	float temperaure, humidity;
	AirSensorTest::UPDATE_ERROR error = airSensorTest.getMeasuredValues(
		&pressure, &gas_resistance, &temperaure, &humidity
	);
	switch (error) {
		case AirSensorTest::UPDATE_ERROR::TO_EARLY:
			snprintf(buffer, buffer_size, "Was to early to update values.\n");
			break;
		case AirSensorTest::UPDATE_ERROR::NOT_INIT:
			snprintf(buffer, buffer_size, "BME680 not initialized.\n");
			break;
		case AirSensorTest::UPDATE_ERROR::LOST_CONNECTION:
			snprintf(buffer, buffer_size, "Lost connection to BME680.\n");
			break;
		case AirSensorTest::UPDATE_ERROR::NOTHING:
			int written = 0;
			written		+= snprintf(
				&buffer[written], buffer_size - written,
				"Temperature: %4.1f°C\n", temperaure
			);
			written += snprintf(
				&buffer[written], buffer_size - written, "Humidity: %4.1f%%\n",
				humidity
			);
			written += snprintf(
				&buffer[written], buffer_size - written, "Pressure: %4luhPa\n",
				pressure
			);
			written += snprintf(
				&buffer[written], buffer_size - written,
				"Gas resistance: %5luOhm\n", gas_resistance
			);
			if (written >= buffer_size - 1) {
				Serial.print("Buffer was not big enougth\n");
			}
			Serial.println("Values:");
			break;
			// default:
			// 	snprintf(buffer, buffer_size, "Case %d not covored.\n", error);
			// 	break;
	}
	Serial.println(buffer);

	snprintf(
		buffer, buffer_size, "Phototransistor Value: %4d -> %5.2f%%\n",
		phototransistorTest.getHumidity_10bit(),
		phototransistorTest.getHumidity_percentage()
	);
	Serial.print(buffer);

	Serial.println();
	delay(5 * 1000);
}

void buttonFunction() {
	noInterrupts();
	static PinStatus prevStatus[3] = {PinStatus::LOW};
	PinStatus currentStatus[3]	   = {
		digitalRead(PIN_BUTTON_1), digitalRead(PIN_BUTTON_2),
		digitalRead(PIN_BUTTON_3)};
	for (int i = 0; i < 3; i++) {
		if (prevStatus[i] != currentStatus[i]) {
			char buffer[50];
			snprintf(
				buffer, 50, "Value for butten %d changed from %d to %d.\n",
				i + 1, prevStatus[i], currentStatus[i]
			);
			Serial.print(buffer);
		}
	}
	interrupts();
}

void dipSwitchFunction() {
	noInterrupts();
	static uint8_t prevStatus  = 0;
	uint8_t newStatus		   = 0;
	int dipSwitchConnection[8] = {PIN_DIP_1, PIN_DIP_2, PIN_DIP_3, PIN_DIP_4,
								  PIN_DIP_5, PIN_DIP_6, PIN_DIP_7, PIN_DIP_8};
	for (int i = 0; i < 8; i++) {
		newStatus |= digitalRead(dipSwitchConnection[i]) << i;
	}
	if (newStatus != prevStatus) {
		char buffer[64];
		char bufferPrevNum[9];
		char bufferNewNum[9];
		writeNumberToBufferBinary(bufferPrevNum, prevStatus, 8);
		writeNumberToBufferBinary(bufferNewNum, newStatus, 8);
		snprintf(
			buffer, 64, "Previous number: %s, new Number: %s\n", bufferPrevNum,
			bufferNewNum
		);
		Serial.print(buffer);
		prevStatus = newStatus;
	} else {
		Serial.print("Function \"dipSwithFunction\" got called without a "
					 "different value. Value is ");
		Serial.println(prevStatus);
	}
	interrupts();
}

bool writeNumberToBufferBinary(
	char * buffer, uint64_t number, uint8_t numberSize
) {
	if (numberSize > 64) {
		return false;
	}
	// Will write buffer with highest bit first
	// Shift number rigth to get the bit at that position
	for (int i = 0; i < numberSize; i++) {
		buffer[i] = '0' + (number >> (numberSize - i - 1)) & 0b1;
	}
	buffer[numberSize] = '\0';
	return true;
}

#else
