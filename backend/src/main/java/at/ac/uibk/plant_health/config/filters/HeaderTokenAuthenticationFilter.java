package at.ac.uibk.plant_health.config.filters;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpHeaders.USER_AGENT;

import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.RequestMatcher;

import java.io.IOException;

import at.ac.uibk.plant_health.config.jwt_authentication.AuthenticationFactory;
import at.ac.uibk.plant_health.config.jwt_authentication.authentication_types.TokenAuthentication;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Filter for trying to get a Bearer Token from a Request.
 *
 * @author David Rieser
 * @see AbstractAuthenticationProcessingFilter
 */
public class HeaderTokenAuthenticationFilter extends AbstractAuthenticationProcessingFilter {
	public HeaderTokenAuthenticationFilter(final RequestMatcher requiresAuth) {
		super(requiresAuth);
	}

	@Override
	public Authentication attemptAuthentication(
			HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse
	) throws AuthenticationException {
		String userAgentHeader = httpServletRequest.getHeader(USER_AGENT);
		String authorizationHeader = httpServletRequest.getHeader(AUTHORIZATION);

		if (userAgentHeader == null) {
			throw new AuthenticationCredentialsNotFoundException(
					"No Token was sent with the Request!"
			);
		}
		if (authorizationHeader == null) {
			throw new AuthenticationCredentialsNotFoundException(
					"No User Agent was sent with the Request!"
			);
		}

		TokenAuthentication tokenAuthentication =
				AuthenticationFactory.create(userAgentHeader, authorizationHeader);
		UsernamePasswordAuthenticationToken token =
				new UsernamePasswordAuthenticationToken(userAgentHeader, tokenAuthentication);

		return getAuthenticationManager().authenticate(token);
	}

	@Override
	protected void successfulAuthentication(
			final HttpServletRequest request, final HttpServletResponse response,
			final FilterChain chain, final Authentication authResult
	) throws IOException, ServletException {
		// If the user was successfully authenticated, store it in the
		// Security Context.
		SecurityContextHolder.getContext().setAuthentication(authResult);
		// Continue running the Web Security Filter Chain.
		chain.doFilter(request, response);
	}
}