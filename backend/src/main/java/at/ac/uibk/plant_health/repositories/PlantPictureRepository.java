package at.ac.uibk.plant_health.repositories;


import at.ac.uibk.plant_health.models.plant.PlantPicture;
import at.ac.uibk.plant_health.models.plant.PlantPictureId;
import org.springframework.data.repository.CrudRepository;

public interface PlantPictureRepository extends CrudRepository<PlantPicture, PlantPictureId> {
}
