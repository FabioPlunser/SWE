package at.ac.uibk.plant_health.models;

import static org.junit.jupiter.api.Assertions.*;

import org.hibernate.Hibernate;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.*;

import at.ac.uibk.plant_health.models.device.AccessPoint;
import at.ac.uibk.plant_health.models.device.SensorStation;
import at.ac.uibk.plant_health.repositories.AccessPointRepository;
import at.ac.uibk.plant_health.repositories.SensorStationRepository;
import jakarta.transaction.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class TestAccessPointModel {
	@Autowired
	private AccessPointRepository accessPointRepository;
	@Autowired
	private SensorStationRepository sensorStationRepository;

	// AccessPoint lifecycle:
	// AccessPoint registers itself to the backend
	// AccessPoint gets configuration
	// AccessPoint found SensorStations
	// AccessPoint sends sensorstation data to backend
	@Test
	void testRegisterAccessPoint() {
		// given roomName and id
		AccessPoint accessPoint = new AccessPoint(UUID.randomUUID(), "TestRoom", 10, false);
		accessPointRepository.save(accessPoint);

		assertFalse(accessPointRepository.findAll().isEmpty());
	}

	@Test
	void changeAccessPointConfiguration() {
		// given AccessPoint is registered
		UUID selfAssignedId = UUID.randomUUID();
		AccessPoint accessPoint = new AccessPoint(selfAssignedId, "TestRoom", 10, false);
		accessPointRepository.save(accessPoint);

		assertEquals(selfAssignedId, accessPoint.getSelfAssignedId());
		AccessPoint savedAccessPoint =
				accessPointRepository.findById(accessPoint.getDeviceId()).get();
		assertEquals(accessPoint.getDeviceId(), savedAccessPoint.getDeviceId());
		assertEquals(accessPoint.getSelfAssignedId(), savedAccessPoint.getSelfAssignedId());
	}
}
