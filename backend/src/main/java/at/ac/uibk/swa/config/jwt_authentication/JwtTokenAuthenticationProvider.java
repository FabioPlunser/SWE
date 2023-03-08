package at.ac.uibk.swa.config.jwt_authentication;

import at.ac.uibk.swa.models.Person;
import at.ac.uibk.swa.models.exceptions.TokenExpiredException;
import at.ac.uibk.swa.service.PersonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.AbstractUserDetailsAuthenticationProvider;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

/**
 * Class responsible for checking that the Session Token exists for the
 * given User.
 * <br/>
 * The Token is provided by the {@link jakarta.servlet.Filter}.
 *
 * @author David Rieser
 * @see AbstractUserDetailsAuthenticationProvider
 * @see org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter
 */
@Component
public class JwtTokenAuthenticationProvider extends AbstractUserDetailsAuthenticationProvider {

    @Autowired
    private PersonService loginService;

    @Value("${swa.token.expiration-duration:1h}")
    private Duration tokenExpirationDuration;

    @Override
    protected void additionalAuthenticationChecks(
            UserDetails userDetails,
            UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken
    ) {
        // All Checks are done in retrieveUser
    }

    @Override
    protected UserDetails retrieveUser(
            String userName,
            UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken
    ) {
        JwtToken token = (JwtToken) usernamePasswordAuthenticationToken.getCredentials();

        // Try to find the User with the given Session Token
        Optional<Person> maybePerson = loginService.findByUsernameAndToken(token);
        return maybePerson
                .map(person -> {this.checkTokenExpired(person); return person;})
                .orElseThrow(() -> new BadCredentialsException(formatTokenError(token.getToken())));
    }

    private static String formatTokenError(UUID token) {
        return String.format("Cannot find user with authentication token: <%s>", token.toString());
    }

    private void checkTokenExpired(Person person)
        throws TokenExpiredException
    {
        LocalDateTime expirationDate = (LocalDateTime) tokenExpirationDuration.addTo(person.getTokenCreationDate());
        if (expirationDate.isBefore(LocalDateTime.now()))
            throw new TokenExpiredException(String.format("Token expired at %s", expirationDate));
    }
}