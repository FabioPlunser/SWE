package at.ac.uibk.swa.config.filters;

import at.ac.uibk.swa.config.jwt_authentication.JwtToken;
import at.ac.uibk.swa.util.ConversionUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.RequestMatcher;

import java.io.IOException;
import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;

/**
 * Filter for trying to get a Token from the Cookies of a Request.
 *
 * @author David Rieser
 * @see AbstractAuthenticationProcessingFilter
 */
public class CookieTokenAuthenticationFilter extends AbstractAuthenticationProcessingFilter {

    public CookieTokenAuthenticationFilter(final RequestMatcher requiresAuth) {
        super(requiresAuth);
    }

    @Override
    public Authentication attemptAuthentication(
            HttpServletRequest httpServletRequest,
            HttpServletResponse httpServletResponse
    ) throws AuthenticationException {
        Cookie[] cookies = httpServletRequest.getCookies();

        if (cookies == null)
            throw new AuthenticationCredentialsNotFoundException("No Credentials were sent with the request!");

        Optional<Cookie> usernameCookie = Arrays.stream(cookies).filter(c -> c.getName().equalsIgnoreCase("username")).findFirst();
        Optional<Cookie> tokenCookie = Arrays.stream(cookies).filter(c -> c.getName().equalsIgnoreCase("token")).findFirst();
        if (usernameCookie.isEmpty() || tokenCookie.isEmpty())
            throw new AuthenticationCredentialsNotFoundException("Missing Credentials!");

        Optional<UUID> maybeToken = ConversionUtil.tryConvertUUIDOptional(tokenCookie.get().getValue());
        if (maybeToken.isEmpty())
            throw new AuthenticationCredentialsNotFoundException("Missing Credentials!");

        JwtToken jwtToken = new JwtToken(usernameCookie.get().getValue(), maybeToken.get());
        return getAuthenticationManager().authenticate(new UsernamePasswordAuthenticationToken(null, jwtToken));

    }

    @Override
    protected void successfulAuthentication(
            final HttpServletRequest request, final HttpServletResponse response,
            final FilterChain chain, final Authentication authResult
    ) throws IOException, ServletException {
        // If the user was successfully authenticated, store it in the Security Context.
        SecurityContextHolder.getContext().setAuthentication(authResult);
        // Continue running the Web Security Filter Chain.
        chain.doFilter(request, response);
    }
}
