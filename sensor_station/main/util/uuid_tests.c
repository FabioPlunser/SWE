
#include <stdio.h>

#include "uuid.h"

int main() {
	uuid_t uuid = random_uuid();
	const char * uuid_string = format_uuid_static(uuid);
	printf("Random UUID: %s\n", uuid_string);
	
	uuid = parse_uuid(uuid_string);
	uuid_string = format_uuid_static(uuid);
	printf("Parsed UUID: %s\n", uuid_string);
}
