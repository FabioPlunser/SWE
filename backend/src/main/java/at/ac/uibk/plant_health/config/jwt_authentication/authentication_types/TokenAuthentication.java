package at.ac.uibk.plant_health.config.jwt_authentication.authentication_types;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class TokenAuthentication {
    private UUID token;
}
