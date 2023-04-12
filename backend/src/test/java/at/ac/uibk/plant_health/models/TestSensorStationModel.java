package at.ac.uibk.plant_health.models;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.*;

import at.ac.uibk.plant_health.models.device.AccessPoint;
import at.ac.uibk.plant_health.models.device.SensorStation;
import at.ac.uibk.plant_health.repositories.SensorStationRepository;
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class TestSensorStationModel {
	@Autowired
	private SensorStationRepository sensorStationRepository;

	@Test
	void addRemoveSensorStation() {
		SensorStation sensorStation = new SensorStation("Rose", UUID.randomUUID(), 255);
		sensorStationRepository.save(sensorStation);
		assertEquals(1, sensorStationRepository.count());
		sensorStationRepository.delete(sensorStation);
		assertEquals(0, sensorStationRepository.count());
	}

	@Test
	void getSensorStationById() {
		SensorStation sensorStation = new SensorStation("Rose", UUID.randomUUID(), 255);
		sensorStationRepository.save(sensorStation);
		assertEquals(1, sensorStationRepository.count());
		assertEquals(
				sensorStation, sensorStationRepository.findById(sensorStation.getDeviceId()).get()
		);
		//        assertEquals(sensorStation,
		//        sensorStationRepository.findByIdAndName(sensorStation.getDeviceId(),
		//        sensorStation.getName()).get());
	}

	@Test
	void addAccessPoint() {
		SensorStation sensorStation = new SensorStation("Rose", UUID.randomUUID(), 255);
		sensorStation.setAccessPoint(new AccessPoint("Room1", 600, false, UUID.randomUUID()));
	}

	@Test
	void addSensorStationToUser() {}

	@Test
	void assignGardenerToSensorStation() {}

	@Test
	void addSensorData() {}

	@Test
	void addSensorLimits() {}
}
