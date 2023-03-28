package at.ac.uibk.plant_health.repositories;


import at.ac.uibk.plant_health.models.plant.SensorData;
import org.springframework.data.repository.CrudRepository;

import java.util.UUID;

public interface SensorDataRepository extends CrudRepository<SensorData, UUID> {
}
