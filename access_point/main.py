import time
import yaml
import logging
import threading

from util import config, ThreadScheduler, CONFIG_FILENAME
from datetime import timedelta
from logging.handlers import RotatingFileHandler

from server import get_config, transfer_data
from sensors import find_stations, collect_data

def main():
    log.info('Program started')

    try:
        # load config
        with open(CONFIG_FILENAME) as f:
            log.info('Loading config file')
            try:
                conf = yaml.load(f, Loader=yaml.loader.SafeLoader)
            except yaml.YAMLError as e:
                log.error(f'Unable to load config file: {e}')
                exit(1)

            # check conf
            try:
                config.validate(conf)
                log.info('Successfully loaded config file')
            except (KeyError, ValueError) as e:
                log.error(f'Invalid config: {e}')
                exit(1)
       
    except FileNotFoundError as e:
        log.error(f'Unable to load config file: {e}')
        exit(1)

    if not conf:
            log.error('Did not load config file')
            exit(1)        
    
    # start sub-threads for checking/updating config, collecting and transfering data
    try:
        # initialize thread schedulers
        get_config_thread = ThreadScheduler(target=get_config, name='GetConfig', interval=timedelta(seconds=conf['get_config_interval']), conf=conf)
        find_stations_thread = ThreadScheduler(target=find_stations, name='FindStations', interval=timedelta(seconds=conf['scan_duration']), conf=conf)
        collect_data_thread = ThreadScheduler(target=collect_data, name='CollectData', interval=timedelta(seconds=conf['collect_data_interval']), conf=conf)
        transfer_data_thread = ThreadScheduler(target=transfer_data, name='TransferData', interval=timedelta(seconds=conf['transfer_data_interval']), conf=conf)

        # keep threads running
        while True:
            get_config_thread.run()
            if conf.get('pairing_mode_active'):
                find_stations_thread.run()
            collect_data_thread.run()
            transfer_data_thread.run()

            # update data transfer interval if necessary
            transfer_data_thread.update_interval(timedelta(seconds=conf['transfer_data_interval']))

            # delay between checking threads                
            time.sleep(1)

    except (InterruptedError, KeyboardInterrupt) as e:
        log.warning(f'Program stopped by external signal')

if __name__ == '__main__':
    # set up logging to file
    log_formatter = logging.Formatter('%(asctime)s | %(threadName)-12s | %(levelname)-7s |> %(message)s', datefmt='%Y-%m-%d %H:%M:%S')
    log_file = 'main.log'
    handler = RotatingFileHandler(log_file, mode='a', maxBytes=10*1024*1024, backupCount=2)
    handler.setFormatter(log_formatter)
    handler.setLevel(logging.INFO)
    log = logging.getLogger()
    log.setLevel(logging.INFO)
    log.addHandler(handler)
    threading.current_thread().name = 'Main'

    # run main routine
    main()
    
