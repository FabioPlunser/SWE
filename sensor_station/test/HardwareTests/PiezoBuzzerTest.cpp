#include <Arduino.h>

class PiezoBuzzerTest {
	private:
		int buzzerPin;
		const int duration = 1000; // ms

	public:
		PiezoBuzzerTest(int pin) {
			this->buzzerPin = pin;
			pinMode(this->buzzerPin, OUTPUT);
		}

		void executeTest() {
			for (int i = 0; i < 5; i++) {
				int frequency = (int) (100 * pow(2, (i + 1) * 5.6439 / 10));
				tone(buzzerPin, frequency);
				delay(duration);
				noTone(buzzerPin);
				delay(duration);
			}
		}
};