package at.ac.uibk.plant_health.models;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.Column;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.Set;
import java.util.UUID;

public class AccessPoint extends Device {

    //region Fields
    @JdbcTypeCode(SqlTypes.NVARCHAR)
    @Column(name = "room_name", nullable = false)
    private String roomName;
    //endregion

    @JsonInclude
    public UUID getDeviceId() {
        return this.deviceId;
    }

    //region UserDetails Implementation
    @Override
    public String getUsername() {
        return this.roomName;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Set.of(DeviceType.AccessPoint);
    }
    //endregion
}
