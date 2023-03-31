#include "Defines.h"

#ifdef DO_TEST

#include "../test/HardwareTests/LedTest.cpp"
#include "../test/HardwareTests/PiezoBuzzerTest.cpp"

#include <Arduino.h>
#include <modules/communication.h>

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
}

void loop() {}

#endif
