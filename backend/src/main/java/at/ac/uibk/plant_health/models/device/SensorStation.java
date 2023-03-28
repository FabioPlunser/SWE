package at.ac.uibk.plant_health.models.device;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.Set;

import at.ac.uibk.plant_health.models.plant.Plant;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@Entity
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Table(name = "sensor_station")
// NOTE: This changes the name of the "id"-Column inherited from Device to "sensor_station_id"
@AttributeOverride(name = "id", column = @Column(name = "sensor_station_id"))
public class SensorStation extends Device {
	@JdbcTypeCode(SqlTypes.INTEGER)
	@Column(name = "dip_switch_id", nullable = false)
	private int dipSwitchId;

	@OneToOne(optional = false, orphanRemoval = true)
	@JoinColumn(name = "plant_id", nullable = false)
	private Plant plant;

	@ManyToOne
	@JoinColumn(name = "access_point_id")
	private AccessPoint accessPoint;

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return Set.of(DeviceType.SENSOR_STATION);
	}
}
