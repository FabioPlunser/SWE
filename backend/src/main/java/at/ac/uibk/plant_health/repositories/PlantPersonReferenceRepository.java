package at.ac.uibk.plant_health.repositories;

import at.ac.uibk.plant_health.models.PlantPersonReference;
import org.springframework.data.repository.CrudRepository;

import java.util.UUID;

public interface PlantPersonReferenceRepository extends CrudRepository<PlantPersonReference, UUID> {
}
