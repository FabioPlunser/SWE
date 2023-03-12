package at.ac.uibk.plant_health.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

import at.ac.uibk.plant_health.config.jwt_authentication.JwtToken;
import at.ac.uibk.plant_health.models.Authenticable;

@Service
public class LoginService {
	@Autowired
	private PersonService personService;

	public Optional<? extends Authenticable> login(JwtToken token) {
		return personService.findByUsernameAndToken(token);
	}
}
