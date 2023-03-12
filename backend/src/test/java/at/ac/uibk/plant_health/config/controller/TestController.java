package at.ac.uibk.plant_health.config.controller;

import at.ac.uibk.plant_health.models.Permission;
import at.ac.uibk.plant_health.models.annotations.AnyPermission;
import at.ac.uibk.plant_health.models.annotations.PublicEndpoint;
import at.ac.uibk.plant_health.models.rest_responses.MessageResponse;
import at.ac.uibk.plant_health.models.rest_responses.RestResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {
    public final static String TEST_ANONYMOUS_VALUE = "/test";
    public final static String TEST_API_VALUE = "/test/api";
    public final static String TEST_ADMIN_VALUE = "/test/admin";

    @GetMapping(TEST_ANONYMOUS_VALUE)
    @PublicEndpoint
    public RestResponseEntity testAPI() {
        return MessageResponse.builder().ok().message("").toEntity();
    }

    @GetMapping(TEST_API_VALUE)
    @AnyPermission(Permission.USER)
    public RestResponseEntity testAPIAdmin() {
        return MessageResponse.builder().ok().message("").toEntity();
    }

    @GetMapping(TEST_ADMIN_VALUE)
    @AnyPermission(Permission.ADMIN)
    public RestResponseEntity testAdmin() {
        return MessageResponse.builder().ok().message("").toEntity();
    }
}
