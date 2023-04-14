package at.ac.uibk.plant_health.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

import at.ac.uibk.plant_health.models.device.SensorStation;
import at.ac.uibk.plant_health.models.plant.PlantPicture;
import at.ac.uibk.plant_health.models.plant.SensorLimits;
import at.ac.uibk.plant_health.repositories.PlantPersonReferenceRepository;
import at.ac.uibk.plant_health.repositories.PlantPictureRepository;
import at.ac.uibk.plant_health.repositories.SensorStationRepository;
import lombok.NoArgsConstructor;

@Service
@NoArgsConstructor
public class PlantService {
	@Autowired
	private SensorStationRepository sensorStationRepository;

	@Autowired
	private PlantPictureRepository plantPictureRepository;

	@Autowired
	private PlantPersonReferenceRepository plantPersonReferenceRepository;

	public List<String> getPlantPictures(SensorStation plant) {
		// TODO
		return List.of();
	}

	public boolean uploadPlantPicture(UUID plantId, String picture) {
		// TODO
		return false;
	}

	public List<SensorStation> findAllPlants() {
		// TODO
		return List.of();
	}

	public boolean setSensorLimits(List<SensorLimits> sensorLimits) {
		// TODO
		return false;
	}

	public boolean setTransferInterval(int transferInterval) {
		// TODO
		return false;
	}

	public boolean deletePicture(PlantPicture plantPicture) {
		// TODO
		return false;
	}

	public boolean createQrCode(UUID qrCodeId) {
		// TODO
		return false;
	}
}
