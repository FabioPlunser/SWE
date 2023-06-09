package at.ac.uibk.plant_health.models;

import com.fasterxml.jackson.annotation.JsonInclude;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@Entity
@SuperBuilder
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Table(name = "access_point")
public class AccessPoint extends Device {
	// region Fields
	@JdbcTypeCode(SqlTypes.NVARCHAR)
	@Column(name = "room_name", nullable = false)
	private String roomName;
	// endregion

	@Override
	@JsonInclude
	public UUID getDeviceId() {
		return this.deviceId;
	}

	// region UserDetails Implementation
	@Override
	public String getUsername() {
		return this.roomName;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return Set.of(DeviceType.ACCESS_POINT);
	}
	// endregion

	@Override
	public boolean equals(Object o) {
		return (this == o)
				|| ((o instanceof AccessPoint a) && (this.deviceId != null)
					&& (this.deviceId.equals(a.deviceId)));
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(this.deviceId);
	}
}
