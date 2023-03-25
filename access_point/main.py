import time
import yaml
import logging
import random

from util import config, ThreadScheduler
from datetime import timedelta
from logging.handlers import RotatingFileHandler

# set up logging to file
log_formatter = logging.Formatter('%(asctime)s | %(threadName)-18s | %(levelname)-7s |> %(message)s',
                                  datefmt='%Y-%m-%d %H:%M:%S')
log_file = 'main.log'
handler = RotatingFileHandler(log_file, mode='a', maxBytes=10*1024*1024, backupCount=2)
handler.setFormatter(log_formatter)
handler.setLevel(logging.INFO)
log = logging.getLogger()
log.setLevel(logging.INFO)
log.addHandler(handler)


def get_config(conf):
    pass

def collect_data(conf):
    pass

def transfer_data(conf):
    pass


if __name__ == '__main__':
    log.info('Program started')

    try:
        # load config
        with open('conf.yaml') as f:
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
        
    
    # start sub-threads for checking/updating config and transfering data
    try:
        # initialize thread schedulers
        get_config_thread = ThreadScheduler(target=get_config, name='GetConfigThread', interval=timedelta(seconds=conf['get_config_interval']), conf=conf)
        collect_data_thread = ThreadScheduler(target=get_config, name='CollectDataThread', interval=timedelta(seconds=conf['collect_data_interval']), conf=conf)
        transfer_data_thread = ThreadScheduler(target=transfer_data, name='TransferDataThread', interval=timedelta(seconds=conf['transfer_data_interval']), conf=conf)

        # keep threads running
        while True:
            get_config_thread.run()
            collect_data_thread.run()
            transfer_data_thread.run()

            # delay between checking threads                
            time.sleep(1)

    except (InterruptedError, KeyboardInterrupt) as e:
        log.info(f'Program stopped by external signal')
