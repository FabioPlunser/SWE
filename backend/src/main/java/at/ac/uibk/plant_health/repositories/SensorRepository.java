package at.ac.uibk.plant_health.repositories;


import at.ac.uibk.plant_health.models.plant.Sensor;
import org.springframework.data.repository.CrudRepository;

import java.util.UUID;

public interface SensorRepository extends CrudRepository<Sensor, UUID> {
}
