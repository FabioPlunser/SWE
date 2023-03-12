package at.ac.uibk.plant_health.repositories;

import at.ac.uibk.plant_health.models.SensorStation;
import org.springframework.data.repository.CrudRepository;

import java.util.UUID;

public interface SensorStationRepository extends CrudRepository<SensorStation, UUID> {
}
