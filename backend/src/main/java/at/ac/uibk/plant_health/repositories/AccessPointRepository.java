package at.ac.uibk.plant_health.repositories;

import at.ac.uibk.plant_health.models.user.Person;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.UUID;

import at.ac.uibk.plant_health.models.device.AccessPoint;

public interface AccessPointRepository extends CrudRepository<AccessPoint, UUID> {
    @Override
    List<AccessPoint> findAll();
}
