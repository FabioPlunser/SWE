package at.ac.uibk.plant_health.repositories;


import at.ac.uibk.plant_health.models.plant.Sensor;
import at.ac.uibk.plant_health.models.plant.SensorData;
import at.ac.uibk.plant_health.models.user.Person;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.UUID;

public interface SensorDataRepository extends CrudRepository<SensorData, UUID> {
    @Override
    List<SensorData> findAll();
}
