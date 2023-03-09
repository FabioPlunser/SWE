
#include <stdint.h>
#include <stdlib.h>

typedef struct uuid uuid_t;

void format_uuid(uuid_t uuid, char * string);
const char * format_uuid_static(uuid_t uuid);
const char * format_uuid_allocated(uuid_t uuid);

uuid_t parse_uuid(const char * uuid_string);
