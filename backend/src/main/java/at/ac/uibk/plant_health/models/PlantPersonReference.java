package at.ac.uibk.plant_health.models;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.UUID;

import at.ac.uibk.plant_health.models.device.SensorStation;
import at.ac.uibk.plant_health.models.user.Person;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Entity
@Table(name = "plant_person_reference")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class PlantPersonReference {
	@Id
	@Column(name = "reference_id", nullable = false)
	@GeneratedValue(strategy = GenerationType.AUTO)
	@JdbcTypeCode(SqlTypes.NVARCHAR)
	private UUID id;

	@ManyToOne
	@MapsId("sensor_station_id")
	@JoinColumn(name = "sensor_station_id", nullable = false)
	@JdbcTypeCode(SqlTypes.NVARCHAR)
	private SensorStation sensorStation;

	@ManyToOne
	@MapsId("person_id")
	@JoinColumn(name = "person_id", nullable = false)
	@JdbcTypeCode(SqlTypes.NVARCHAR)
	private Person person;

	@JdbcTypeCode(SqlTypes.BOOLEAN)
	@Column(name = "is_assigned", nullable = false)
	private boolean isAssigned;

	@JdbcTypeCode(SqlTypes.BOOLEAN)
	@Column(name = "in_dashboard", nullable = false)
	private boolean inDashboard;
}