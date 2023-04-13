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

	// taste case for code coverage
	@BeforeEach
	void setup() {
		accessPointRepository.deleteAll();
	}

	// taste case for code coverage
	@Test
	void saveAccessPoint() {
		AccessPoint accessPoint = new AccessPoint("TestRoom", 10, false);
		accessPointRepository.save(accessPoint);
		assertNotNull(accessPointRepository.findAll());
	}

	// taste case for code coverage
	@Test
	void removeAccessPoint() {
		AccessPoint accessPoint = new AccessPoint("TestRoom", 10, false);
		accessPointRepository.save(accessPoint);
		accessPointRepository.delete(accessPoint);
		assertTrue(accessPointRepository.findAll().isEmpty());
	}

	@Test
	void changeStandardValues() {
		AccessPoint accessPoint = new AccessPoint("TestRoom", 10, false);
		accessPointRepository.save(accessPoint);

		AccessPoint aP = accessPointRepository.findByRoomName("TestRoom").get();

		// check if it's the same object
		assertEquals(accessPoint.getDeviceId(), aP.getDeviceId());

		aP.setRoomName("Hallo");
		aP.setPairingModeActive(true);
		aP.setUnlocked(true);
		accessPointRepository.save(aP);

		AccessPoint aP2 = accessPointRepository.findByRoomName("Hallo").get();

		// check if the original object is changed
		assertEquals(aP.getDeviceId(), aP2.getDeviceId());

		assertEquals(aP2.getRoomName(), "Hallo");
		assertTrue(aP2.isPairingModeActive());
		assertTrue(aP2.isUnlocked());
	}

	@Test
	@Transactional
	void addRemoveSensorStation() {
		AccessPoint accessPoint = new AccessPoint("TestRoom", 10, false);
		accessPointRepository.save(accessPoint);

		SensorStation sensorStation = new SensorStation(255);
		sensorStationRepository.save(sensorStation);

		// get accessPoint from database and add sensorStation
		AccessPoint accessPoint1 = accessPointRepository.findById(accessPoint.getDeviceId()).get();
		List<SensorStation> sensorStationList = accessPoint1.getSensorStations();
		sensorStationList.add(sensorStation);
		accessPoint1.setSensorStations(sensorStationList);
		accessPointRepository.save(accessPoint1);

		assertEquals(
				1,
				accessPointRepository.findById(accessPoint1.getDeviceId())
						.get()
						.getSensorStations()
						.size()
		);

		// get accessPoint from database and remove sensorStation
		AccessPoint accessPoint2 = accessPointRepository.findById(accessPoint.getDeviceId()).get();
		List<SensorStation> sensorStationList2 = accessPoint1.getSensorStations();
		sensorStationList2.remove(sensorStation); // remove the sensorStation from the list
		accessPoint1.setSensorStations(sensorStationList2);
		accessPointRepository.save(accessPoint1);

		assertEquals(
				0,
				accessPointRepository.findById(accessPoint.getDeviceId())
						.get()
						.getSensorStations()
						.size()
		);
	}
}
