package at.ac.uibk.plant_health.repositories;

import org.springframework.data.repository.CrudRepository;

import java.util.UUID;

import at.ac.uibk.plant_health.models.device.AccessPoint;

public interface AccessPointRepository extends CrudRepository<AccessPoint, UUID> {}
