package at.ac.uibk.plant_health.models.rest_responses;

import com.fasterxml.jackson.annotation.JsonInclude;

import org.springframework.security.core.GrantedAuthority;

import java.io.Serializable;
import java.util.Set;
import java.util.UUID;

import at.ac.uibk.plant_health.models.Authenticable;
import at.ac.uibk.plant_health.models.Person;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
@NoArgsConstructor (access = AccessLevel.MODULE)
@AllArgsConstructor
public class LoginResponse extends TokenResponse implements Serializable {
		@Override
		@JsonInclude
		public String getType () {
			return "Login";
		}

		private UUID personId;
		private Set<GrantedAuthority> permissions;

		public LoginResponse (Authenticable authenticable) {
			super (true, authenticable.getToken ());
			this.personId	 = authenticable.getId ();
			this.permissions = authenticable.getPermissions ();
		}

		// region Builder Customization
		public abstract static class LoginResponseBuilder<
				C extends LoginResponse, B extends LoginResponseBuilder<C, B>>
				extends TokenResponseBuilder<C, B> {
				public LoginResponseBuilder<C, B> person (Person person) {
					this.personId	 = person.getPersonId ();
					this.permissions = person.getPermissions ();
					this.token (person.getToken ());
					return this;
				}
		}
		// endregion
}
