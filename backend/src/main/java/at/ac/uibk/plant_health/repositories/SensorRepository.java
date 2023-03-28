package at.ac.uibk.plant_health.repositories;


import at.ac.uibk.plant_health.models.plant.Sensor;
import at.ac.uibk.plant_health.models.user.Person;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.UUID;

public interface SensorRepository extends CrudRepository<Sensor, UUID> {
    @Override
    List<Sensor> findAll();
}
