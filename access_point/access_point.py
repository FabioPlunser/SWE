import time
import logging
import threading
import procedures
import sys

from logging.handlers import RotatingFileHandler

from util import *
from database import Database, DatabaseError


LOG_LEVEL = logging.INFO

def main():
    log.info('---------------')
    log.info('Program started')

    try:
        # setup
        log.info(f'Loading config from file {CONFIG_FILENAME}')
        config = Config(CONFIG_FILENAME)
        log.info(f'Initializing database in file {DB_FILENAME}')
        database = Database(DB_FILENAME)
        database.setup()
        # set log_level to DEBUG if desired
        if config.debug:
            log.info('Debugging output for logging enabled')
            log.setLevel(logging.DEBUG)
            for handler in log.handlers:
                handler.setLevel(logging.DEBUG)
        # start sub-threads for checking/updating config, collecting and transfering data
        try:
            # initialize thread schedulers
            get_config_thread = ThreadScheduler(target=procedures.get_config,
                                                name='GetConfig',
                                                interval=config.get_config_interval,
                                                start_immediately=True,
                                                conf=config)
            find_stations_thread = ThreadScheduler(target=procedures.find_stations,
                                                   name='FindStations',
                                                   conf=config)
            collect_data_thread = ThreadScheduler(target=procedures.collect_data,
                                                  name='CollectData',
                                                  interval=config.collect_data_interval,
                                                  conf=config)
            transfer_data_thread = ThreadScheduler(target=procedures.transfer_data,
                                                   name='TransferData',
                                                   interval=config.transfer_data_interval,
                                                   conf=config)

            # keep threads running
            while True:
                get_config_thread.run()
                # only run other threads if access point is unlocked
                if config.token:
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
            print('Received interrupt, waiting for sub-threads to shut down')
            exit()
    except ValueError as e:
        log.error(f'Unable to load config file: {e}')       
    except DatabaseError as e:
        log.error(f'Unable to initialize database: {e}')

if __name__ == '__main__':
    # set up logging to file
    log_formatter = logging.Formatter('%(asctime)s | %(threadName)-12s | %(levelname)-8s |> %(message)s', datefmt='%Y-%m-%d %H:%M:%S')
    log_file = 'main.log'
    handler = RotatingFileHandler(log_file, mode='a', maxBytes=10*1024*1024, backupCount=2)
    handler.setFormatter(log_formatter)
    handler.setLevel(LOG_LEVEL)
    log = logging.getLogger()
    log.setLevel(LOG_LEVEL)
    log.addHandler(handler)
    sys.stderr = StreamToLogger(log,logging.CRITICAL)
    threading.current_thread().name = 'Main'

    # run main routine
    main()
    
