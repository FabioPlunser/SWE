package at.ac.uibk.plant_health.config.jwt_authentication;

import at.ac.uibk.plant_health.models.Authenticable;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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