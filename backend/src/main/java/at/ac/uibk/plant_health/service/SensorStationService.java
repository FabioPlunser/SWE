package at.ac.uibk.plant_health.service;

import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import at.ac.uibk.plant_health.models.device.SensorStation;
import at.ac.uibk.plant_health.repositories.SensorDataRepository;
import at.ac.uibk.plant_health.repositories.SensorLimitsRepository;
import at.ac.uibk.plant_health.repositories.SensorRepository;
import at.ac.uibk.plant_health.repositories.SensorStationRepository;

public class SensorStationService {
	@Autowired
	private SensorStationRepository sensorStationRepository;

	public List<SensorStation> findAll() {
		return sensorStationRepository.findAll();
	}
}
