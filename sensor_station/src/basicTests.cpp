#include "Defines.h"

#ifdef DO_TEST

#include "../test/HardwareTests/LedTest.cpp"

#include <Arduino.h>
#include <modules/communication.h>

/**
 * DO_TEST -> Will execute tests
 */

LedTest ledTest(PIN_RGB_RED, PIN_RGB_GREEN, PIN_RGB_BLUE);

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
	Serial.print("Execute LED-Test");
	ledTest.executeTest();
}

#endif
