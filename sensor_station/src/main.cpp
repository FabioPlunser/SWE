
#include <Arduino.h>
#include <modules/communication.h>

void setup() {
	Serial.begin(115200);
	initialize_communication();
	enable_pairing_mode();
}

void loop() {}
