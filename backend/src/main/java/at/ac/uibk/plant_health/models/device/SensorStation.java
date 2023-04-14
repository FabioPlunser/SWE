package at.ac.uibk.plant_health.models.device;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.springframework.security.core.GrantedAuthority;

import java.util.*;

import at.ac.uibk.plant_health.models.SensorStationPersonReference;
import at.ac.uibk.plant_health.models.plant.SensorData;
import at.ac.uibk.plant_health.models.plant.SensorLimits;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Entity
@NoArgsConstructor(access = AccessLevel.PUBLIC)
@AllArgsConstructor(access = AccessLevel.PUBLIC)
@Table(name = "sensor_station")
// NOTE: This changes the name of the "id"-Column inherited from Device to "sensor_station_id"

@AttributeOverride(name = "id", column = @Column(name = "sensor_station_id"))
public class SensorStation extends Device {
	@Column(name = "mac_address", unique = true)
	@JdbcTypeCode(SqlTypes.NVARCHAR)
	private String macAddress = null;

	@Column(name = "plant_name")
	@JdbcTypeCode(SqlTypes.NVARCHAR)
	private String name = null;

	@Column(name = "qr_code_id", unique = true)
	@JdbcTypeCode(SqlTypes.UUID)
	private UUID qrCodeId = null;

	@JdbcTypeCode(SqlTypes.INTEGER)
	@Column(name = "dip_switch_id", nullable = false)
	private int dipSwitchId;

	@ManyToOne
	@JoinColumn(name = "access_point_id")
	private AccessPoint accessPoint;

	@OneToMany(mappedBy = "sensorStation")
	private List<SensorStationPersonReference> sensorStationPersonReferences = new ArrayList<>();

	@OneToMany(mappedBy = "sensorStation")
	private List<SensorData> sensorData = new ArrayList<>();

	@OneToMany(mappedBy = "sensorStation")
	private List<SensorLimits> sensorLimits = new ArrayList<>();

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return Set.of(DeviceType.SENSOR_STATION);
	}

	public SensorStation(String macAddress, int dipSwitchId) {
		super();
		this.macAddress = macAddress;
		this.dipSwitchId = dipSwitchId;
	}
}
