package at.ac.uibk.plant_health.controllers;

import static at.ac.uibk.plant_health.util.EndpointMatcherUtil.LOGIN_ENDPOINT;
import static at.ac.uibk.plant_health.util.EndpointMatcherUtil.LOGOUT_ENDPOINT;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.endpoint.annotation.DeleteOperation;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Optional;

import at.ac.uibk.plant_health.models.Person;
import at.ac.uibk.plant_health.models.annotations.ApiRestController;
import at.ac.uibk.plant_health.models.annotations.PublicEndpoint;
import at.ac.uibk.plant_health.models.rest_responses.AuthFailedResponse;
import at.ac.uibk.plant_health.models.rest_responses.LoginResponse;
import at.ac.uibk.plant_health.models.rest_responses.MessageResponse;
import at.ac.uibk.plant_health.models.rest_responses.RestResponseEntity;
import at.ac.uibk.plant_health.service.PersonService;
import lombok.SneakyThrows;

/**
 * Controller handling the login-, and logout-process.
 *
 * @author David Rieser
 * @see at.ac.uibk.plant_health.util.EndpointMatcherUtil
 */
@SuppressWarnings("unused")
@ApiRestController
public class LoginController {
	@Autowired
	private PersonService personService;

	/**
	 * Endpoint for the Front-End to request an Authentication Token.
	 *
	 * @param username The username of the User to create the Token for.
	 * @param password The password of the User to create the Token for.
	 * @return A Token if the user credentials are correct, otherwise an
	 *     error.
	 */
	@SneakyThrows
	@ReadOperation
	@PublicEndpoint
	@PostMapping(value = LOGIN_ENDPOINT)
	public RestResponseEntity getToken(@RequestParam("username") final String username,
			@RequestParam("password") final String password) {
		Optional<Person> maybePerson = personService.login(username, password);

		if (maybePerson.isEmpty()) {
			return AuthFailedResponse.builder()
					.statusCode(HttpStatus.UNAUTHORIZED)
					.message("Username or Password are wrong!")
					.toEntity();
		}

		return LoginResponse.builder().ok().person(maybePerson.get()).toEntity();
	}

	/**
	 * Endpoint for the Front-End to logout.
	 * This deletes the Authentication Token stored in the database.
	 *
	 * @return A Message saying whether the Logout was successful or not.
	 */
	@DeleteOperation
	@PostMapping(LOGOUT_ENDPOINT)
	public RestResponseEntity deleteToken() {
		if (!personService.logout()) {
			return MessageResponse.builder()
					.statusCode(HttpStatus.UNAUTHORIZED)
					.message("No matching Token!")
					.toEntity();
		}

		return MessageResponse.builder().ok().message("Successfully logged out!").toEntity();
	}
}