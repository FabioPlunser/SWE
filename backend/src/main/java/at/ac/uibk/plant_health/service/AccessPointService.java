package at.ac.uibk.plant_health.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import at.ac.uibk.plant_health.models.device.AccessPoint;
import at.ac.uibk.plant_health.models.device.SensorStation;
import at.ac.uibk.plant_health.repositories.AccessPointRepository;

import javax.swing.text.html.Option;

@Service
public class AccessPointService {
	@Autowired
	private AccessPointRepository accessPointRepository;

	public List<AccessPoint> findAllAccessPoints() {
		return accessPointRepository.findAll();
	}

	/**
	 * Register a new AccessPoint.
	 * Check if AccessPoint is Unlocked.
	 * @param accessPointId
	 * @param roomName
	 * @return
	 */
	public boolean register(UUID accessPointId, String roomName) {
		if(accessPointId == null || roomName == null)
			return false;

		return this.create(accessPointId, roomName);
	}

	/**
	 * Get the AccessPoint with the given ID.
	 * @param accessPointId
	 * @return
	 * @throws IllegalArgumentException
	 */
	public boolean isUnlocked(UUID accessPointId) throws IllegalArgumentException {
		Optional<AccessPoint> maybeAccessPoint = accessPointRepository.findBySelfAssignedId(accessPointId);
		if(maybeAccessPoint.isEmpty())
			throw new IllegalArgumentException("AccessPoint with ID " + accessPointId + " does not exist.");
		AccessPoint accessPoint =  maybeAccessPoint.get();
		return accessPoint.isUnlocked();
	}

	/**
	 * Set the unlocked state of the AccessPoint with the given ID.
	 * @param accessPointId
	 * @param unlocked
	 * @throws IllegalArgumentException
	 */
	public void setUnlocked(UUID accessPointId, boolean unlocked) throws IllegalArgumentException {
		Optional<AccessPoint> maybeAccessPoint = accessPointRepository.findBySelfAssignedId(accessPointId);
		if(maybeAccessPoint.isEmpty())
			throw new IllegalArgumentException("AccessPoint with ID " + accessPointId + " does not exist.");
		AccessPoint accessPoint =  maybeAccessPoint.get();
		accessPoint.setUnlocked(unlocked);
		accessPointRepository.save(accessPoint);
	}

	/**
	 * Check if the AccessPoint with the given ID is registered.
	 * @param accessPointId
	 * @return
	 */
	public boolean isAccessPointRegistered(UUID accessPointId) {
		Optional<AccessPoint> maybeAccessPoint = accessPointRepository.findBySelfAssignedId(accessPointId);
		return maybeAccessPoint.isPresent();
	}


	/**
	 * Save the given AccessPoint.
	 * If the AccessPoint with the given ID already exists, don't save it and return false.
	 *
	 * @param accessPointId
	 * @param roomName
	 */
	public boolean create(UUID accessPointId, String roomName) {
		// TODO
		if(accessPointId == null && roomName == null) {
			return false;
		}
		AccessPoint accessPoint = new AccessPoint(accessPointId, roomName, false);
		return save(accessPoint) != null;
	}

	/**
	 * Save the given AccessPoint.
	 * If the AccessPoint with the given ID already exists, update its Config and return false.
	 *
	 * @param accessPoint The AccessPoint to save.
	 * @return the saved AccessPoint
	 */
	public AccessPoint save(AccessPoint accessPoint) {
		// TODO
		try {

			return accessPointRepository.save(accessPoint);
		} catch (Exception e) {
			return null;
		}
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
