package at.ac.uibk.swa.util;

import at.ac.uibk.swa.config.jwt_authentication.AuthContext;
import at.ac.uibk.swa.models.Authenticable;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.UUID;

public class MockAuthContext {
    /**
     * Sets the currently logged in user so that it can be retrieved by AuthContext.getCurrentUser()
     * Logs out any other user that is currently logged in
     *
     * @param user user to be logged in
     * @return AuthContext.getCurrentUser().get() or null if none has be returned
     */
    public static Authenticable setLoggedInUser(Authenticable user) {
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(user, UUID.randomUUID()));
        if (AuthContext.getCurrentPerson().isEmpty()) {
            return null;
        } else {
            return AuthContext.getCurrentPerson().get();
        }
    }
}
