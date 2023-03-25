import threading
import typing
import logging

from datetime import datetime, timedelta

log = logging.getLogger()

class ThreadScheduler:
    def __init__(self, target: typing.Callable, name: str, interval: timedelta, **kwargs):
        self.target = target
        self.name = name
        self.interval = interval
        self.kwargs = kwargs

        # set timestamp so that thread is immediately started when calling run()
        self.start_timestamp = datetime.now() - self.interval
        self.thread = None
        self.interval_warning_logged = False

    def run(self):
        if (datetime.now() - self.start_timestamp) >= self.interval:
            if self.thread is None or not self.thread.is_alive():
                self.thread = threading.Thread(target=self.target, name=self.name, kwargs=self.kwargs)
                log.info(f'Starting thread {self.name}')
                self.thread.start()
                self.start_timestamp = datetime.now()
                self.interval_warning_logged = False
            elif self.thread.is_alive() and not self.interval_warning_logged:
                log.warning(f'{self.name} has not finished within given interval ({self.interval.total_seconds()}s)')
                self.interval_warning_logged = True

    def update_interval(self, interval: timedelta):
        if interval != self.interval:
            self.interval = interval
            self.interval_warning_logged = False
            log.info(f'Updated interval for calling thread {self.name} to {self.interval.total_seconds()}s')
