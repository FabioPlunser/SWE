#include "Defines.h"

#ifdef DO_TEST

#include "../test/HardwareTests/LedTest.cpp"
#include "../test/HardwareTests/PiezoBuzzerTest.cpp"

#include <Arduino.h>
#include <modules/communication.h>

/**
 * DO_TEST -> Will execute tests
 */

LedTest ledTest(PIN_RGB_RED, PIN_RGB_GREEN, PIN_RGB_BLUE);
PiezoBuzzerTest piezoBuzzerTest(PIN_PIEZO_BUZZER);

void setup() {
	Serial.begin(115200);
	// initialize_communication();
	// enable_pairing_mode();
}

/**
 * Will move through the color range, starting with the color red to blue and
 * lastely green.
 */

void loop() {
	Serial.print("Start LED test");
	delay(1000);
	ledTest.executeTest();
	Serial.print("Finished LED test");

	Serial.print("Start piezo-buzzer test");
	delay(1000);
	piezoBuzzerTest.executeTest();
	Serial.print("Finished piezo-buzzer test");
}

#endif
