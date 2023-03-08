package at.ac.uibk.swa.config.exception_handling;

import at.ac.uibk.swa.controllers.error_controllers.SwaErrorController;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Handler for {@link AuthenticationException}s inside the filters of the Security Chain.
 *
 * @author David Rieser
 * @see AuthenticationException
 * @see AuthenticationFailureHandler
 */
@Component
public class RestAuthenticationFailureHandler implements AuthenticationFailureHandler {

    @Autowired
    private SwaErrorController errorController;

    @Override
    public void onAuthenticationFailure(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException authException
    ) throws IOException {
        errorController.handleErrorManual(request, response, authException);
    }
}
