package at.ac.uibk.plant_health.models;

import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.Set;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@Entity
@SuperBuilder
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Table(name = "sensor_station")
public class SensorStation extends Device {
	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return Set.of(DeviceType.SENSOR_STATION);
	}
}
