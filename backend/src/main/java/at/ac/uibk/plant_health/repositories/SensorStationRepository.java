package at.ac.uibk.plant_health.repositories;

import org.springframework.data.repository.CrudRepository;

import java.util.UUID;

import at.ac.uibk.plant_health.models.SensorStation;

public interface SensorStationRepository extends CrudRepository<SensorStation, UUID> {}
