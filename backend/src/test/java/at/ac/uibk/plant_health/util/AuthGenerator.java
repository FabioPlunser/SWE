package at.ac.uibk.plant_health.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import at.ac.uibk.plant_health.config.jwt_authentication.JwtToken;
import at.ac.uibk.plant_health.models.Person;
import jakarta.servlet.http.Cookie;

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
					new Cookie("token", jwt.getToken().toString())};
		}
}
