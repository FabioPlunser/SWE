package at.ac.uibk.swa.util;

import at.ac.uibk.swa.config.jwt_authentication.JwtToken;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.Optional;
import java.util.UUID;

/**
 * Helper Class for Conversion Method that should have slightly modified behaviour.
 *
 * @author David Rieser
 */
// All your Constructors are belong to us!
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ConversionUtil {

    /**
     * Helper Method for converting a String into a UUID without throwing an Exception.
     *
     * @param maybeToken The String that should be converted into a Token.
     * @return The Token if it could be converted, null otherwise.
     */
    public static UUID tryConvertUUID(String maybeToken) {
        try {
            return UUID.fromString(maybeToken);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Helper Method for parsing a JSON Web Token from a String.
     *
     * @param input The input to parse into a {@link JwtToken}.
     * @return The parsed {@link JwtToken}, or empty if the parsing failed.
     */
    public static Optional<JwtToken> tryConvertJwtTokenOptional(String input) {
        return Optional.ofNullable(tryConvertJwtToken(input));
    }

    /**
     * Helper Method for parsing a JSON Web Token from a String.
     *
     * @param input The input to parse into a {@link JwtToken}.
     * @return The parsed {@link JwtToken}, or null if the parsing failed.
     */
    public static JwtToken tryConvertJwtToken(String input) {
        try {
            return new ObjectMapper().readValue(input, JwtToken.class);
        } catch (JsonProcessingException e) {
            return null;
        }
    }

    /**
     * Helper Method for converting a String into a UUID without throwing an Exception.
     *
     * @param maybeToken The String that should be converted into a Token.
     * @return The Token if it could be converted, an empty Optional otherwise.
     */
    public static Optional<UUID> tryConvertUUIDOptional(String maybeToken) {
        return Optional.ofNullable(tryConvertUUID(maybeToken));
    }
}
