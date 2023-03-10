# Plant Health

[![Pipeline Status](https://git.uibk.ac.at/informatik/qe/swess23/group1/g1t1/badges/main/pipeline.svg)](https://git.uibk.ac.at/informatik/qe/swess23/group1/g1t1/-/commits/main)
[![Coverage](https://qe-sonarqube.uibk.ac.at/api/project_badges/measure?project=SWESS23_G1T1&metric=coverage&token=sqb_e89c28cb541c824fa55ab0dd3a5581255c4a05a4)](https://qe-sonarqube.uibk.ac.at/dashboard?id=SWESS23_G1T1)
[![Quality Gate Status](https://qe-sonarqube.uibk.ac.at/api/project_badges/measure?project=SWESS23_G1T1&metric=alert_status&token=sqb_e89c28cb541c824fa55ab0dd3a5581255c4a05a4)](https://qe-sonarqube.uibk.ac.at/dashboard?id=SWESS23_G1T1)

[![Latest Release](https://git.uibk.ac.at/informatik/qe/swess23/group1/g1t1/-/badges/release.svg)](https://git.uibk.ac.at/informatik/qe/swess23/group1/g1t1/-/releases)

A system for keeping track of the Health of various Plants.

## Overview

The System is built from 5 different Components:

- Central Backend Server managing all the data using a REST-API (named Backend).
- A persistent Database connected to the Backend for persisting the Sensor data.
- Web Server for displaying the data (named Frontend).
- Accesspoints that communicate with the Sensorstations and report data back to the central Backend.
- Sensorstations that record Earth Humidity, Air Pressure, Air Humidity, Air Quality, Temparature and Light Intensity for a given Plant.

![Component Diagram](https://git.uibk.ac.at/informatik/qe/swess23/group1/g1t1/-/blob/media/Diagrams/component-diagram.drawio.svg)

## Usage

TODO: Project Setup is still ongoing.

## License

This project is licensed under the [GPLv3 license].

[GPLv3 License]: https://git.uibk.ac.at/informatik/qe/swess23/group1/g1t1/-/blob/main/LICENSE

## Contributors:
- Lukas Kirchmair
- Fabian Margreiter
- Emanuel Prader
- Fabio Plunser 
- David Rieser