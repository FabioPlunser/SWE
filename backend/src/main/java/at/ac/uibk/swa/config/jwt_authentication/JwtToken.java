package at.ac.uibk.swa.config.jwt_authentication;

import at.ac.uibk.swa.models.Authenticable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class JwtToken {
    private String username;
    private UUID token;

    public JwtToken(Authenticable authenticable) {
        this.token = authenticable.getToken();
        this.username = authenticable.getUsername();
    }
}