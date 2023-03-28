package at.ac.uibk.plant_health.repositories;

import at.ac.uibk.plant_health.models.Log;
import at.ac.uibk.plant_health.models.user.Person;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.UUID;

public interface LogRepository extends CrudRepository<Log, UUID> {
    @Override
    List<Log> findAll();
}
