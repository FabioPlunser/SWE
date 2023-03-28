package at.ac.uibk.plant_health.repositories;


import at.ac.uibk.plant_health.models.plant.PlantPicture;
import at.ac.uibk.plant_health.models.plant.PlantPictureId;
import at.ac.uibk.plant_health.models.user.Person;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface PlantPictureRepository extends CrudRepository<PlantPicture, PlantPictureId> {
    @Override
    List<PlantPicture> findAll();
}
