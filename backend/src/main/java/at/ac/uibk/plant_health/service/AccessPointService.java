package at.ac.uibk.plant_health.service;

import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.UUID;

import at.ac.uibk.plant_health.models.device.AccessPoint;
import at.ac.uibk.plant_health.models.device.SensorStation;
import at.ac.uibk.plant_health.repositories.AccessPointRepository;

public class AccessPointService {
	@Autowired
	private AccessPointRepository accessPointRepository;

	public List<AccessPoint> findAllAccessPoints() {
		return accessPointRepository.findAll();
	}

	public boolean register(AccessPoint accessPoint) {
		return this.create(accessPoint);
	}

	/**
	 * Save the given AccessPoint.
	 * If the AccessPoint with the given ID already exists, don't save it and return false.
	 *
	 * @param accessPoint The AccessPoint to save.
	 */
	public boolean create(AccessPoint accessPoint) {
		// TODO
		return false;
	}

	/**
	 * Save the given AccessPoint.
	 * If the AccessPoint with the given ID already exists, update its Config and return false.
	 *
	 * @param accessPoint The AccessPoint to save.
	 */
	public boolean save(AccessPoint accessPoint) {
		// TODO
		return false;
	}

	public boolean startScan(AccessPoint accessPoint) {
		// TODO
		return false;
	}

	public boolean setLocked(boolean locked, AccessPoint accessPoint) {
		if (locked) {
			accessPoint.setAccessToken(null);
		} else {
			UUID token = accessPoint.getAccessToken();

			if (token == null) {
				token = UUID.randomUUID();
			}

			accessPoint.setAccessToken(token);
		}

		// TODO: Save AccessPoint

		return false;
	}

	public boolean foundNewSensorStation(AccessPoint accessPoint, SensorStation sensorStation) {
		// TODO
		return false;
	}

	public boolean reconnectedToSensorStation(
			AccessPoint accessPoint, SensorStation sensorStation
	) {
		// TODO
		return false;
	}

	public boolean lostSensorStation(AccessPoint accessPoint, SensorStation sensorStation) {
		// TODO
		return false;
	}
}
