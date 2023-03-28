package at.ac.uibk.plant_health.repositories;


import at.ac.uibk.plant_health.models.plant.PlantPicture;
import org.springframework.data.repository.CrudRepository;

import java.util.UUID;

public interface PlantPictureRepository extends CrudRepository<PlantPicture, UUID> {
}
