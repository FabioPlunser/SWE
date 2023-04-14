package at.ac.uibk.plant_health.repositories;

import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import at.ac.uibk.plant_health.models.device.AccessPoint;
import at.ac.uibk.plant_health.models.user.Person;

import javax.swing.text.html.Option;

public interface AccessPointRepository extends CrudRepository<AccessPoint, UUID> {
	@Override
	List<AccessPoint> findAll();

	@Override
	Optional<AccessPoint> findById(UUID deviceId);

	Optional<AccessPoint> findBySelfAssignedId(UUID selfAssignedId);

	Optional<AccessPoint> findByRoomName(String roomName);
}
