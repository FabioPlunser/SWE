package at.ac.uibk.plant_health.models.device;

import com.fasterxml.jackson.annotation.JsonInclude;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.springframework.security.core.GrantedAuthority;

import java.util.*;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Entity
@NoArgsConstructor(access = AccessLevel.PUBLIC)
//@AllArgsConstructor(access = AccessLevel.PUBLIC)
@Table(name = "access_point")
// NOTE: This changes the name of the "id"-Column inherited from Device to "access_point_id"
@AttributeOverride(name = "id", column = @Column(name = "access_point_id"))
public class AccessPoint extends Device {
	// region Fields
	@JdbcTypeCode(SqlTypes.NVARCHAR)
	@Column(name = "room_name", nullable = false)
	private String roomName;

	@JdbcTypeCode(SqlTypes.INTEGER)
	@Column(name = "transfer_interval", nullable = false)
	private int transferInterval;

	@JdbcTypeCode(SqlTypes.BOOLEAN)
	@Column(name = "pairing_mode_active", nullable = false)
	private boolean pairingModeActive;

	@JdbcTypeCode(SqlTypes.NVARCHAR)
	@Column(name = "access_token")
	private UUID accessToken = null;

	@OneToMany(fetch = FetchType.EAGER, mappedBy = "accessPoint")
	private List<SensorStation> sensorStations = new ArrayList<>();
	// endregion

	public AccessPoint(String roomName, int transferInterval, boolean pairingModeActive) {
		super();
		this.roomName = roomName;
		this.transferInterval = transferInterval;
		this.pairingModeActive = pairingModeActive;
	}

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
