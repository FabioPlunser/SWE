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
		AccessPoint accessPoint = new AccessPoint("TestRoom", 10, false, UUID.randomUUID());
		accessPointRepository.save(accessPoint);

		assertFalse(accessPointRepository.findAll().isEmpty());
	}

	@Test
	void changeAccessPointConfiguration() {
		// given AccessPoint is registered
		UUID deviceId = UUID.randomUUID();
		AccessPoint accessPoint = new AccessPoint("TestRoom", 10, false, deviceId);
		accessPointRepository.save(accessPoint);

		assertEquals(deviceId, accessPoint.getDeviceId());

		System.out.println(accessPointRepository.findById(accessPoint.getDeviceId()));
	}
}
