
// Forum Post: https://forum.arduino.cc/t/derive-total-ram-size-programatically/486808/7
// Code Example from: https://playground.arduino.cc/Code/AvailableMemory/

#include <stdint.h>

uint8_t * heapptr, * stackptr;
void check_mem();

void setup() {
  check_mem();

  Serial.begin(115200);

  Serial.print("Heap Pointer: ");
  Serial.println((size_t) heapptr);

  Serial.print("Stack Pointer: ");
  Serial.println((size_t) stackptr);

  Serial.print("RAM Size: ");
  Serial.println((size_t) (stackptr - heapptr));
}

void loop() {
  // put your main code here, to run repeatedly:

}

void check_mem() {
  stackptr = (uint8_t *)malloc(4);          // use stackptr temporarily
  heapptr = stackptr;                     // save value of heap pointer
  free(stackptr);      // free up the memory again (sets stackptr to 0)
  stackptr =  (uint8_t *)(SP);           // save value of stack pointer
}
