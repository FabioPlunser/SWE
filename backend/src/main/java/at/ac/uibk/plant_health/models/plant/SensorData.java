package at.ac.uibk.plant_health.models.plant;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class SensorData {
	@Id
	@Column(name = "time_stamp", nullable = false)
	@JdbcTypeCode(SqlTypes.TIMESTAMP)
	private LocalDateTime timeStamp;

	@JdbcTypeCode(SqlTypes.INTEGER)
	@Column(name = "sensor_value", nullable = false)
	private int value;

	@JdbcTypeCode(SqlTypes.BOOLEAN)
	@Column(name = "above_limit", nullable = false)
	private boolean aboveLimit;

	@JdbcTypeCode(SqlTypes.BOOLEAN)
	@Column(name = "below_limit", nullable = false)
	private boolean belowLimit;

	@ManyToOne
	@JoinColumn(name = "sensor_id", nullable = false)
	private Sensor sensor;

	@ManyToOne(optional = false)
	@JoinColumn(name = "plant_id", nullable = false)
	private Plant plant;
}