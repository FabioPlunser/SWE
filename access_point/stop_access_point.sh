#!/bin/bash
kill -SIGINT $(ps -aux | grep "python3 access_point.py" | grep -v grep | awk '{print $2}')
