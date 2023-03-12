package at.ac.uibk.plant_health.repositories;

import at.ac.uibk.plant_health.models.AccessPoint;
import org.springframework.data.repository.CrudRepository;

import java.util.UUID;

public interface AccessPointRepository extends CrudRepository<AccessPoint, UUID> {
}
