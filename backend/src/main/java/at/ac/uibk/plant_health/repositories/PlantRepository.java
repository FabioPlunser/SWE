package at.ac.uibk.plant_health.repositories;

import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.UUID;

import at.ac.uibk.plant_health.models.plant.Plant;
import at.ac.uibk.plant_health.models.user.Person;

public interface PlantRepository extends CrudRepository<Plant, UUID> {
	@Override
	List<Plant> findAll();
}
