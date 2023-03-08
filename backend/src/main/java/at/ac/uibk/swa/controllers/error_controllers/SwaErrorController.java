package at.ac.uibk.swa.controllers.error_controllers;

import at.ac.uibk.swa.models.annotations.ApiRestController;
import at.ac.uibk.swa.models.exceptions.TokenExpiredException;
import at.ac.uibk.swa.models.rest_responses.MessageResponse;
import at.ac.uibk.swa.models.rest_responses.RedirectResponse;
import at.ac.uibk.swa.models.rest_responses.RestResponseEntity;
import at.ac.uibk.swa.models.rest_responses.TokenExpiredResponse;
import at.ac.uibk.swa.util.EndpointMatcherUtil;
import at.ac.uibk.swa.util.SerializationUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.io.IOException;
import java.io.PrintWriter;

import static at.ac.uibk.swa.util.EndpointMatcherUtil.ErrorEndpoints.*;

/**
 * Controller for sending Error Responses.
 *
 * @author David Rieser
 */
@ApiRestController
@SuppressWarnings("unused")
public class SwaErrorController implements ErrorController {

    @Autowired
    private EndpointMatcherUtil endpointMatcherUtil;

    private RedirectResponse generateRedirectFromException(Exception exception) {
        return generateRedirectFromException(404, exception);
    }

    private RedirectResponse generateRedirectFromException(int status, Exception exception) {
        return RedirectResponse.builder()
                .redirectLocation(String.format(
                        "%s?status=%d&header=%s&message=%s",
                        endpointMatcherUtil.getErrorBaseRoute(),
                        status, exception.getClass().getSimpleName(), exception.getMessage()
                ))
                .build();
    }

    @ResponseBody
    @RequestMapping(AUTHENTICATION_ERROR_ENDPOINT)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public RestResponseEntity handleAuthenticationError(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException accessDeniedException
    ) {
        if (endpointMatcherUtil.isApiRoute(request)) {
            return MessageResponse.builder()
                    .message("Authentication failed!")
                    .statusCode(HttpStatus.UNAUTHORIZED)
                    .toEntity();
        } else {
            return generateRedirectFromException(401, accessDeniedException).toEntity();
        }
    }

    @ResponseBody
    @RequestMapping(TOKEN_EXPIRED_ERROR_ENDPOINT)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public RestResponseEntity handleTokenExpiredError(
            HttpServletRequest request,
            HttpServletResponse response,
            TokenExpiredException tokenExpiredException
    ) {
        if (endpointMatcherUtil.isApiRoute(request)) {
        return TokenExpiredResponse.builder()
                .exception(tokenExpiredException)
                .statusCode(HttpStatus.UNAUTHORIZED)
                .toEntity();
    } else {
        return generateRedirectFromException(401, tokenExpiredException).toEntity();
    }
    }

    @ResponseBody
    @RequestMapping(AUTHORIZATION_ERROR_ENDPOINT)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public RestResponseEntity handleAuthorizationError(
            HttpServletRequest request,
            HttpServletResponse response,
            AccessDeniedException accessDeniedException
    ) {
        if (endpointMatcherUtil.isApiRoute(request)) {
            return MessageResponse.builder()
                .message("Insufficient Privileges!")
                .statusCode(HttpStatus.FORBIDDEN)
                .toEntity();
        } else {
            return generateRedirectFromException(403, accessDeniedException).toEntity();
        }
    }

    @ResponseBody
    @RequestMapping(NOT_FOUND_ERROR_ENDPOINT)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public RestResponseEntity handleNotFoundError(
            HttpServletRequest request,
            HttpServletResponse response
    ) {
        if (endpointMatcherUtil.isApiRoute(request)) {
            return MessageResponse.builder()
                .message("Endpoint not found!")
                .statusCode(HttpStatus.NOT_FOUND)
                .toEntity();
        } else {
            return generateRedirectFromException(404, new Exception()).toEntity();
        }
    }

    @ResponseBody
    @RequestMapping(ERROR_ENDPOINT)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public RestResponseEntity handleError(
            HttpServletRequest request,
            HttpServletResponse response,
            Exception exception
    ) {
        if (endpointMatcherUtil.isApiRoute(request)) {
            return MessageResponse.builder()
                .success(false)
                .message("Internal Server Error!")
                .statusCode(HttpStatus.UNAUTHORIZED)
                .toEntity();
        } else {
            return generateRedirectFromException(501, exception).toEntity();
        }
    }

    public void handleErrorManual(
            HttpServletRequest request,
            HttpServletResponse response,
            Exception exception
    ) throws IOException {
        RestResponseEntity responseEntity;

        if (exception instanceof AuthenticationException authenticationException) {
            if (exception instanceof TokenExpiredException tokenExpiredException) {
                responseEntity = handleTokenExpiredError(request, response, tokenExpiredException);
            } else {
                responseEntity = handleAuthenticationError(request, response, authenticationException);
            }
        } else if (exception instanceof AccessDeniedException accessDeniedException) {
            responseEntity = handleAuthorizationError(request, response, accessDeniedException);
        } else {
            responseEntity = handleError(request, response, exception);
        }

        try {
            // Set the Response Status to the stored one
            response.setStatus(responseEntity.getStatusCode().value());
            // Add any custom Headers from the entity
            responseEntity.getHeaders()
                    .forEach((name, values) ->
                            values.forEach(value -> response.addHeader(name, value))
                    );

            // Write the Body of the Request
            String responseBody = SerializationUtil.serializeJSON(responseEntity.getBody());
            if (responseBody != null) {
                try (PrintWriter writer = response.getWriter()) {
                    writer.write(responseBody);
                }
            }
        } catch (JsonProcessingException e) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
}
