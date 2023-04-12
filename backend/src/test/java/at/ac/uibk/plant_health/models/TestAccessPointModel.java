package at.ac.uibk.plant_health.models;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.UUID;

import at.ac.uibk.plant_health.models.device.AccessPoint;
import at.ac.uibk.plant_health.models.device.SensorStation;
import at.ac.uibk.plant_health.repositories.AccessPointRepository;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class TestAccessPointModel {
	@Autowired
	private AccessPointRepository accessPointRepository;

	@BeforeEach
	void clearDatabase() {
		accessPointRepository.deleteAll();
	}
	@Test
	void saveAndGetAccessPoint() {
		AccessPoint accessPoint = new AccessPoint("Room1", 600, false, UUID.randomUUID());
		accessPointRepository.save(accessPoint);
		assertEquals(1, accessPointRepository.count());
		assertEquals(accessPoint, accessPointRepository.findById(accessPoint.getDeviceId()).get());

		AccessPoint accessPoint1 = new AccessPoint("Room2", 600, false, UUID.randomUUID());
		accessPointRepository.save(accessPoint1);

		assertEquals(2, accessPointRepository.findAll().size());
	}

	@Test
	void addSensorStations() {
		AccessPoint accessPoint = new AccessPoint("Room1", 600, false, UUID.randomUUID());
		accessPointRepository.save(accessPoint);
		assertEquals(1, accessPointRepository.count());
		assertEquals(accessPoint, accessPointRepository.findById(accessPoint.getDeviceId()).get());
		System.out.println(accessPoint.getSensorStations());

		accessPoint.addSensorStation(new SensorStation());
		assertEquals(1, accessPoint.getSensorStations().size());
	}

	@Test
	void removeSensorStations() {
		AccessPoint accessPoint = new AccessPoint("Room1", 600, false, UUID.randomUUID());
		accessPointRepository.save(accessPoint);
		assertEquals(1, accessPointRepository.count());
		assertEquals(accessPoint, accessPointRepository.findById(accessPoint.getDeviceId()).get());
		System.out.println(accessPoint.getSensorStations());

		accessPoint.addSensorStation(new SensorStation());
		assertEquals(1, accessPoint.getSensorStations().size());

		accessPoint.removeSensorStation(accessPoint.getSensorStations().get(0));
		assertEquals(0, accessPoint.getSensorStations().size());
	}
}
