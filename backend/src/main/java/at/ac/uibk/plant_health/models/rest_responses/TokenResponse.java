package at.ac.uibk.plant_health.models.rest_responses;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.io.Serializable;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
@NoArgsConstructor (access = AccessLevel.MODULE)
@AllArgsConstructor
public class TokenResponse extends RestResponse implements Serializable {
		@Override
		@JsonInclude
		public String getType () {
			return "TokenExpired";
		}

		private UUID token;

		public TokenResponse (boolean success, UUID token) {
			super (success);
			this.token = token;
		}
}
