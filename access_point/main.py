import time
import yaml
import logging
import threading

from util import Config, ThreadScheduler, CONFIG_FILENAME
from datetime import timedelta
from logging.handlers import RotatingFileHandler

from server import get_config, transfer_data
from sensors import find_stations, collect_data

def main():
    log.info('Program started')

    config = Config(CONFIG_FILENAME)
    try:
        config.load()
    except Exception as e:
        log.error(f'Unable to load config file: {e}')
        exit(1)

    # start sub-threads for checking/updating config, collecting and transfering data
    try:
        # initialize thread schedulers
        get_config_thread = ThreadScheduler(target=get_config, name='GetConfig', interval=config.get_config_interval, conf=config)
        find_stations_thread = ThreadScheduler(target=find_stations, name='FindStations', interval=timedelta(seconds=1), suppress_interval_warning=True, conf=config)
        collect_data_thread = ThreadScheduler(target=collect_data, name='CollectData', interval=config.collect_data_interval, conf=config)
        transfer_data_thread = ThreadScheduler(target=transfer_data, name='TransferData', interval=config.transfer_data_interval, conf=config)

        # keep threads running
        while True:
            get_config_thread.run()
            if config.scan_active:
                find_stations_thread.run()
            collect_data_thread.run()
            transfer_data_thread.run()

            # update data transfer interval if necessary
            transfer_data_thread.update_interval(config.transfer_data_interval)

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
    
