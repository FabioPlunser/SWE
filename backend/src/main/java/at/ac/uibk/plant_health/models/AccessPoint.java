package at.ac.uibk.plant_health.models;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@Entity
@SuperBuilder
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Table(name = "access_point")
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
