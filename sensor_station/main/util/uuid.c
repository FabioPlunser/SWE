
#include "uuid.h"

#define UUID_STRING_LENGTH 37

#define LOWER_BITS(x) ((x)& 0xF)
#define HIGHER_BITS(x) (((x) >> 4) & 0xF)

typedef struct uuid {
	uint8_t version;
	uint8_t variant;
	uint8_t bits[16];
} uuid_t;

void format_uuid(uuid_t uuid, char * string);
const char * format_uuid_static(uuid_t uuid); 
const char * format_uuid_allocated(uuid_t uuid); 

uuid_t parse_uuid(const char * uuid_string);

void format_uint8_t(char * string, uint8_t number);
char format_hex_number(uint8_t num);

static char STATIC_UUID_STRING[UUID_STRING_LENGTH];

// Format a Version 4 Variant 8 UUID 
void format_uuid(uuid_t uuid, char * string) {
	uint8_t i = 0, bit_idx = 0;
	
	string[8] = string[13] = string[18] = string[23] = '-';

	i = 0;
	while (i < 8) {
		format_uint8_t(string + i, uuid.bits[bit_idx++]);
		i += 2;
	}
	
	i = 9;
	while (i < 13) {
		format_uint8_t(string + i, uuid.bits[bit_idx++]);
		i += 2;
	}

	string[14] = format_hex_number(uuid.version);
	i += 2;
	format_uint8_t(string + i, uuid.bits[bit_idx++]); 
	i += 2; 
	string[i++] = format_hex_number(HIGHER_BITS(uuid.bits[bit_idx]));

	string[19] = format_hex_number(uuid.variant);
	i += 2;
	string[i++] = format_hex_number(LOWER_BITS(uuid.bits[bit_idx++]));
	format_uint8_t(string + i, uuid.bits[bit_idx++]); 

	i = 24;
	while (i < 36) {
		format_uint8_t(string + i, uuid.bits[bit_idx++]);
		i += 2;
	}

	string[36] = '\0';
}

const char * format_uuid_static(uuid_t uuid) {
	format_uuid(uuid, STATIC_UUID_STRING);
	return STATIC_UUID_STRING;
}

const char * format_uuid_allocated(uuid_t uuid) {
	char * uuid_string = (char *) malloc(UUID_STRING_LENGTH * sizeof(char));
	format_uuid(uuid, uuid_string);
	return uuid_string;
}

uuid_t parse_uuid(const char * uuid_string) {
	// TODO
	return (uuid_t) {
		.bits = {0}
	};
}

void format_uint8_t(char * string, uint8_t number) {
	string[0] = format_hex_number(HIGHER_BITS(number));
	string[1] = format_hex_number(LOWER_BITS(number));
}

char format_hex_number(uint8_t num) {
	static const char * characters = "0123456789abcdef";
	return characters[num & 0xF];
}
