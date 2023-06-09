package at.ac.uibk.plant_health.controllers;

import static at.ac.uibk.plant_health.util.EndpointMatcherUtil.REGISTER_ENDPOINT;

import at.ac.uibk.plant_health.config.jwt_authentication.AuthContext;
import at.ac.uibk.plant_health.models.Authenticable;
import at.ac.uibk.plant_health.models.annotations.PrincipalRequired;
import at.ac.uibk.plant_health.models.rest_responses.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.endpoint.annotation.DeleteOperation;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;
import org.springframework.boot.actuate.endpoint.annotation.WriteOperation;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Stream;

import at.ac.uibk.plant_health.models.Permission;
import at.ac.uibk.plant_health.models.Person;
import at.ac.uibk.plant_health.models.annotations.AnyPermission;
import at.ac.uibk.plant_health.models.annotations.ApiRestController;
import at.ac.uibk.plant_health.models.annotations.PublicEndpoint;
import at.ac.uibk.plant_health.service.PersonService;

/**
 * Controller handling {@link Person} related Information (e.g. creating,
 * updating, deleting {@link Person})
 *
 * @author David Rieser
 */
@SuppressWarnings("unused")
@ApiRestController
public class PersonController {
	// region Autowired Components
	@Autowired
	private PersonService personService;
	// endregion

	// region User Creation Endpoints
	/**
	 * User Registration Endpoint for Users to create an Account by
	 * themselves.
	 *
	 * @param username The new Users username.
	 * @param password The new Users password (needs to be already hashed).
	 * @param email The new Users email.
	 * @return A RestResponse indicating whether the user could be created
	 *     or not.
	 */
	@WriteOperation
	@PublicEndpoint
	@PostMapping(REGISTER_ENDPOINT)
	public RestResponse register(
			@RequestParam("username") final String username,
			@RequestParam("password") final String password,
			@RequestParam("email") final String email
	) {
		UUID token = UUID.randomUUID();
		Person person =
				new Person(username, email, password, token, (Set) Permission.defaultPermissions());

		return createUser(person);
	}

	/**
	 * User Creation Endpoint for Admins to manually create Accounts.
	 *
	 * @param username The new Users username.
	 * @param password The new Users password (needs to be already hashed).
	 * @param email The new Users email.
	 * @param permissions The Permissions the new User should have.
	 * @return A RestResponse indicating whether the user could be created
	 *     or not.
	 */
	@WriteOperation
	@AnyPermission(Permission.ADMIN)
	@PostMapping("/create-user")
	public RestResponse create(
			@RequestParam("username") final String username,
			@RequestParam("password") final String password,
			@RequestParam("email") final String email,
			@RequestParam("permissions") final Set<Permission> permissions
	) {
		Person person = new Person(username, email, password, (Set) permissions);

		return createUser(person);
	}

	/**
	 * Helper Function for saving a Person and returning a corresponding
	 * RestResponse.
	 *
	 * @param person The Person to save.
	 * @return A RestResponse indicating whether the operation was
	 *     successful or not.
	 */
	private RestResponse createUser(Person person) {
		if (!personService.create(person))
			return new MessageResponse(false, "Could not create User - Username already exists!");

		return new CreatedUserResponse(person);
	}
	// endregion

	// region Update User Endpoints
	/**
	 * Endpoint for Admins to change/update a user
	 *
	 * @param personId The ID of the User to update
	 * @param username The new username
	 * @param email The new email
	 * @param password The new Password
	 * @param permissions The new Permissions
	 * @return A RESTResponse indicating Success
	 */
	@WriteOperation
	@AnyPermission(Permission.ADMIN)
	@PostMapping("/update-user")
	public RestResponse updateUser(
			@RequestParam(name = "personId") final UUID personId,
			@RequestParam(name = "username", required = false) final String username,
			@RequestParam(name = "email", required = false) final String email,
			@RequestParam(name = "permissions", required = false) final Set<Permission> permissions,
			@RequestParam(name = "password", required = false) final String password
	) {
		if (personService.update(personId, username, permissions, password))
			return new MessageResponse(true, "User " + personId + " updated successfully!");

		return new MessageResponse(
				false, "Could not update User " + personId + " - User does not exist!"
		);
	}
	// endregion

	// region Delete User Endpoints
	/**
	 * Endpoint for Admins to delete a user.
	 *
	 * @param personId The ID of the User to delete
	 * @return A RestResponse indicating whether the operation was
	 *     successful or not.
	 */
	@DeleteOperation
	@AnyPermission(Permission.ADMIN)
	@DeleteMapping("/delete-user")
	public RestResponse deleteUser(@RequestParam("personId") final UUID personId) {
		if (!personService.delete(personId))
			return new MessageResponse(
					false, "Could not delete User " + personId + " - User does not exist!"
			);

		return new MessageResponse(true, "User " + personId + " deleted successfully!");
	}
	// endregion

	// region GET Endpoints
	/**
	 * Endpoint for Admins to get all users.
	 *
	 * @return A RestReponse containing a List of all users.
	 */
	@ReadOperation
	@AnyPermission(Permission.ADMIN)
	@GetMapping("/get-all-users")
	public RestResponse getAllUsers() {
		return new ListResponse<>(personService.getPersons());
	}

	/**
	 * Endpoint for Admins to get all possible Permission so that they don't
	 * need to be changed manually on frontend.
	 *
	 * @return A List of all possible Permissions.
	 */
	@ReadOperation
	@AnyPermission(Permission.ADMIN)
	@GetMapping("/get-all-permissions")
	public RestResponse getAllPermissions() {
		return new ListResponse<>(Stream.of(Permission.values()).map(Enum::name).toList());
	}

	@ReadOperation
	@PrincipalRequired(Authenticable.class)
	@GetMapping("/get-user-permissions")
	public RestResponseEntity getUserPermissions() {
		Optional<Authenticable> maybeUser = AuthContext.getCurrentUser();
		return PermissionResponse.builder()
				.success(maybeUser.isPresent())
				.permissions(maybeUser.map(a -> a.getPermissions()).orElse(Set.of()).toArray(GrantedAuthority[]::new))
				.toEntity();
	}
	// endregion
}
