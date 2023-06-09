package at.ac.uibk.plant_health.models.rest_responses;

import com.fasterxml.jackson.annotation.JsonInclude;

import at.ac.uibk.plant_health.models.exceptions.TokenExpiredException;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
public class TokenExpiredResponse extends RestResponse {
	@Override
	@JsonInclude
	public String getType() {
		return "TokenExpired";
	}

	private String message;

	public TokenExpiredResponse() {
		this.message = this.getType();
	}

	public TokenExpiredResponse(TokenExpiredException tokenExpiredException) {
		this.message = tokenExpiredException.getMessage();
	}

	public static TokenExpiredResponse fromException(TokenExpiredException tokenExpiredException) {
		if (tokenExpiredException == null)
			return new TokenExpiredResponse();
		else
			return new TokenExpiredResponse(tokenExpiredException);
	}

	// region Builder Customization
	public abstract static class TokenExpiredResponseBuilder<
			C extends TokenExpiredResponse, B extends TokenExpiredResponseBuilder<C, B>>
			extends RestResponseBuilder<C, B> {
		public TokenExpiredResponseBuilder<C, B> exception(
				TokenExpiredException tokenExpiredException
		) {
			this.message = tokenExpiredException.getMessage();
			return this;
		}
	}
	// endregion
}