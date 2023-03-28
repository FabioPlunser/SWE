package at.ac.uibk.plant_health.repositories;


import at.ac.uibk.plant_health.models.plant.Plant;
import org.springframework.data.repository.CrudRepository;

import java.util.UUID;

public interface PlantRepository extends CrudRepository<Plant, UUID> {
}
