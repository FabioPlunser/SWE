package at.ac.uibk.plant_health.repositories;

import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.UUID;

import at.ac.uibk.plant_health.models.Log;
import at.ac.uibk.plant_health.models.user.Person;

public interface LogRepository extends CrudRepository<Log, UUID> {
	@Override
	List<Log> findAll();
}
