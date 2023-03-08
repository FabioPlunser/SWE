package at.ac.uibk.swa.config.jwt_authentication;

import at.ac.uibk.swa.models.Authenticable;
import at.ac.uibk.swa.models.Person;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.*;

/**
 * Helper Class for accessing security Information contained in the current request.
 *
 * @author David Rieser
 * @version 1.1
 */
// All your constructors are belong to us!
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class AuthContext {

    /**
     * Check whether the User that made the current Request is authenticated.
     * A User is authenticated if he is logged-in with Username and Password (and in turn with his Token).
     * Otherwise, the Request is anonymous.
     *
     * @return true if a logged-in user made the request, false if the request is anonymous or if the Authentication failed.
     */
    public static boolean isAuthenticated() {
        // Get the Authentication set by the FilterChain.
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        // Ensure that the Authentication succeeded by ensuring it is not null and that
        // the Authentication is not anonymous.
        return authentication != null && !(authentication instanceof AnonymousAuthenticationToken);
    }

    /**
     * Helper Method for accessing the current Authentication Token.
     * Currently, the only supported AuthenticationToken is a UsernamePasswordAuthenticationToken.
     *
     * @return The AuthenticationToken sent with the current request (if a valid one was sent).
     * @see UsernamePasswordAuthenticationToken
     */
    private static Optional<UsernamePasswordAuthenticationToken> getAuthentication() {
        if (!isAuthenticated())
            return Optional.empty();
        return Optional.of((UsernamePasswordAuthenticationToken) SecurityContextHolder.getContext().getAuthentication());
    }

    /**
     * Get the user that is currently logged-in.
     *
     * @return The Authenticable whose credentials were sent with the request (if valid credentials were sent).
     * @see Authenticable
     */
    private static Optional<Authenticable> getCurrentUser() {
        return getAuthentication().map(x -> (Authenticable) x.getPrincipal());
    }

    /**
     * Get the person that is currently logged-in.
     *
     * @return The Person whose credentials were sent with the request (if valid credentials were sent).
     * @see Person
     */
    public static Optional<Person> getCurrentPerson() {
        return getCurrentUser().map(a -> a instanceof Person p ? p : null);
    }

    /**
     * Get the Token that was sent with the request (either as a Bearer-, or Cookie-Token).
     *
     * @return The Token sent with the request (if a valid one was sent).
     */
    public static Optional<UUID> getLoginToken() {
        return getCurrentUser().map(Authenticable::getToken);
    }

    /**
     * Checks whether the currently logged-in user has the required Permission.
     * This method also works if the user is not logged-in or anonymous (and will return false in those cases).
     *
     * @param required The Permission that the User needs to have.
     * @return true if the user has the required Permission, false otherwise.
     */
    public static boolean hasPermission(GrantedAuthority required) {
        return anyPermission(List.of(required));
    }

    /**
     * Checks whether the currently logged-in user has any of the required Permissions.
     * This method also works if the user is not logged-in or anonymous (and will return false in those cases).
     *
     * @param required The Permissions that the User needs to have.
     * @return true if the user has any of the required Permissions, false otherwise.
     */
    public static boolean anyPermission(Collection<GrantedAuthority> required) {
        return getCurrentUser()
                .map(Authenticable::getPermissions)
                .map(permissions -> required.stream().anyMatch(permissions::contains))
                .orElse(false);
    }

    /**
     * Checks whether the currently logged-in user has all the required Permissions.
     * This method also works if the user is not logged-in or anonymous (and will return false in those cases).
     *
     * @param required The Permissions that the User needs to have.
     * @return true if the user has all the required Permissions, false otherwise.
     */
    public static boolean allPermissions(Collection<GrantedAuthority> required) {
        return getCurrentUser()
                .map(Authenticable::getPermissions)
                .map(permissions -> required.stream().allMatch(permissions::contains))
                .orElse(false);
    }

    /**
     * Checks whether the currently logged-in user does not have the required Permission.
     * This method also works if the user is not logged-in or anonymous (and will return false in those cases).
     *
     * @param permission The Permission that the User needs to miss.
     * @return true if the user does not have the required Permissions, false otherwise.
     */
    public static boolean missingPermission(GrantedAuthority permission) {
        return !anyPermission(Set.of(permission));
    }

    /**
     * Checks whether the currently logged-in user does not have the required Permissions.
     * This method also works if the user is not logged-in or anonymous (and will return false in those cases).
     *
     * @param permissions The Permissions that the User needs to miss.
     * @return true if the user does not have any of the required Permissions, false otherwise.
     */
    public static boolean missingPermissions(Collection<GrantedAuthority> permissions) {
        return !anyPermission(permissions);
    }
}
