package at.ac.uibk.plant_health.repositories;


import at.ac.uibk.plant_health.models.plant.SensorLimits;
import org.springframework.data.repository.CrudRepository;

import java.util.UUID;

public interface SensorLimitsRepository extends CrudRepository<SensorLimits, UUID> {
}
