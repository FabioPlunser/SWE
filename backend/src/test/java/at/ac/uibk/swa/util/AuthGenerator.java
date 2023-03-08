package at.ac.uibk.swa.util;

import at.ac.uibk.swa.config.jwt_authentication.JwtToken;
import at.ac.uibk.swa.models.Person;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.Cookie;
import lombok.SneakyThrows;

public class AuthGenerator {

    public static String generateToken(Person person) throws JsonProcessingException {
        return new ObjectMapper().writeValueAsString(generateJwtToken(person));
    }

    public static JwtToken generateJwtToken(Person person) {
        if (person.getToken() == null)
            throw new NullPointerException("Token of Person was null");
        return new JwtToken(person.getUsername(), person.getToken());
    }

    public static Cookie[] jwtTokenToCookies(JwtToken jwt) {
        return new Cookie[] {
                new Cookie("username", jwt.getUsername()),
                new Cookie("token", jwt.getToken().toString())
        };
    }
}
