package at.ac.uibk.swa.config.test_controller;

import at.ac.uibk.swa.models.Permission;
import at.ac.uibk.swa.models.annotations.AnyPermission;
import at.ac.uibk.swa.models.rest_responses.MessageResponse;
import at.ac.uibk.swa.models.rest_responses.RestResponseEntity;
import org.springframework.boot.test.context.TestComponent;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {
    @GetMapping("/testAnonymous")
    public RestResponseEntity anonymousTestAPI() {
        return MessageResponse.builder().ok().message("").toEntity();
    }

    @GetMapping("${swa.api.base:/api}/test")
    public RestResponseEntity testAPI() {
        return MessageResponse.builder().ok().message("").toEntity();
    }

    @AnyPermission(Permission.ADMIN)
    @GetMapping("${swa.api.base:/api}/testAdmin")
    public RestResponseEntity testAPIAdmin() {
        return MessageResponse.builder().ok().message("").toEntity();
    }

    @GetMapping("${swa.admin.base:/admin}/test")
    public RestResponseEntity testAdmin() {
        return MessageResponse.builder().ok().message("").toEntity();
    }
}
