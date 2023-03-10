package at.ac.uibk.swa.service;

import at.ac.uibk.swa.config.jwt_authentication.JwtToken;
import at.ac.uibk.swa.models.Authenticable;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class LoginService {

    @Autowired
    private PersonService personService;

    public Optional<? extends Authenticable> login(JwtToken token) {
        return personService.findByUsernameAndToken(token);
    }
}
