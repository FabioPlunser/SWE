package at.ac.uibk.swa.config.exception_handling;

import at.ac.uibk.swa.controllers.error_controllers.SwaErrorController;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * The Authentication Entry Point of the Spring Security Chain.
 * <br/>
 * This is used for catching {@link AuthenticationException}s.
 *
 * @author David Rieser
 * @see AuthenticationException
 * @see AuthenticationEntryPoint
 */
@Component
public class RestAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Autowired
    private SwaErrorController errorController;

    @Override
    public void commence(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException authException
    ) throws IOException {
        errorController.handleErrorManual(request, response, authException);
    }
}
