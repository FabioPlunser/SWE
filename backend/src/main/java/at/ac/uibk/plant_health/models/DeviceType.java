package at.ac.uibk.plant_health.models;

import org.springframework.security.core.GrantedAuthority;

public enum DeviceType implements GrantedAuthority {

    SensorStation,
    AccessPoint;

    @Override
    public String getAuthority() {
        return this.toString();
    }
}
