package at.ac.uibk.swa.config.test_controller;

import at.ac.uibk.swa.models.Permission;
import at.ac.uibk.swa.models.annotations.AnyPermission;
import at.ac.uibk.swa.models.rest_responses.MessageResponse;
import at.ac.uibk.swa.models.rest_responses.RestResponseEntity;
import jakarta.annotation.security.PermitAll;
import org.springframework.boot.test.context.TestComponent;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {
    public final static String TEST_ANONYMOUS_VALUE = "${swa.api.base:/api}/test";
    public final static String TEST_API_VALUE = "${swa.api.base:/api}/testAdmin";
    public final static String TEST_ADMIN_VALUE = "${swa.admin.base:/admin}/test";

    @GetMapping(TEST_ANONYMOUS_VALUE)
    @PermitAll
    public RestResponseEntity testAPI() {
        return MessageResponse.builder().ok().message("").toEntity();
    }

    @AnyPermission(Permission.ADMIN)
    @GetMapping(TEST_API_VALUE)
    public RestResponseEntity testAPIAdmin() {
        return MessageResponse.builder().ok().message("").toEntity();
    }

    @GetMapping(TEST_ADMIN_VALUE)
    @AnyPermission(Permission.ADMIN)
    public RestResponseEntity testAdmin() {
        return MessageResponse.builder().ok().message("").toEntity();
    }
}
