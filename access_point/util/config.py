import validators
import typing

def validate(conf: dict):
    required_keys = [
        'get_config_interval',
        'collect_data_interval',
        'transfer_data_interval',
        'room_name',
        'backend_address'
    ]
    for k in required_keys:
        if k not in conf:
            raise KeyError(f'Unable to find key "{k}"')
        
    k = 'get_config_interval'
    check_type(k, conf, int)
    check_limits(k, conf, 1, 10)
    
    k = 'collect_data_interval'
    check_type(k, conf, int)
    check_limits(k, conf, 1, 3600)

    k = 'transfer_data_interval'
    check_type(k, conf, int)
    check_limits(k, conf, conf['collect_data_interval'], 3600)

    k = 'room_name'
    check_type(k, conf, str)
    
    k = 'backend_address'
    check_type(k, conf, str)
    check_misc(k, conf, [validators.url, validators.ipv4], any=True)

    
def check_type(k, d: dict, type):
    if not isinstance(d[k], type):
        raise ValueError(f'Expected value of type {type} for key "{k}"')
    

def check_limits(k, d, lower, upper):
    if not lower <= d[k] <= upper:
        raise ValueError(f'Expected value between {lower} and {upper} for key "{k}"')
    

def check_misc(k, d, rules: list[typing.Callable], any=False):
    if any:
        for rule in rules:
            any_matched = False
            if rule(d[k]):
                any_matched = True
        if not any_matched:
            raise ValueError(f'Expected value that matches any of {[rule.__name__ for rule in rules]} for key "{k}"')
    else:
        for rule in rules:
            if not rule(d[k]):
                raise ValueError(f'Expected value that matches {rule.__name__} for key "{k}"')
