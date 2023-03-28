package at.ac.uibk.plant_health.repositories;

import at.ac.uibk.plant_health.models.Log;
import org.springframework.data.repository.CrudRepository;

import java.util.UUID;

public interface LogRepository extends CrudRepository<Log, UUID> {
}
