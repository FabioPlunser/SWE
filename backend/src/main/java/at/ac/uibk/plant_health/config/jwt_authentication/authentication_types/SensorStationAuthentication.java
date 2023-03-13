package at.ac.uibk.plant_health.config.jwt_authentication.authentication_types;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SensorStationAuthentication extends TokenAuthentication {
}
