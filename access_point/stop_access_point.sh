#!/bin/bash
kill $(ps -aux | grep "python3 main.py" | grep -v grep | awk '{print $2}')
kill $(ps -aux | grep "start_access_point.sh" | grep -v grep | awk '{print $2}')
