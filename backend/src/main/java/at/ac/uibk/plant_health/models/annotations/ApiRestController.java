package at.ac.uibk.plant_health.models.annotations;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.lang.annotation.*;

@Inherited
@Documented
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@RestController
@RequestMapping(path = "${swa.api.base}")
public @interface ApiRestController {}
