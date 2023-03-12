package at.ac.uibk.plant_health.models;

import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.Set;

public class SensorStation extends Device {
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Set.of(DeviceType.SensorStation);
    }
}
