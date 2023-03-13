package at.ac.uibk.plant_health.service;

import at.ac.uibk.plant_health.config.jwt_authentication.JwtToken;
import at.ac.uibk.plant_health.repositories.AccessPointRepository;
import at.ac.uibk.plant_health.repositories.SensorStationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class LoginService {
	@Autowired
	private PersonService personService;

    @Autowired
    private AccessPointRepository accessPointRepository;

    @Autowired
    private SensorStationRepository sensorStationRepository;

    public Optional<? extends UserDetails> login(String userAgent, JwtToken token) {
        return switch (userAgent) {
            case "SensorStation"    -> this.sensorStationRepository.findById(token.getToken());
            case "AccessPoint"      -> this.accessPointRepository.findById(token.getToken());
            default                 -> this.personService.findByUsernameAndToken(token);
        };
    }
}
