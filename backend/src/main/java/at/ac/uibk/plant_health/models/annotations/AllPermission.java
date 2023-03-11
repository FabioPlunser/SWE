package at.ac.uibk.plant_health.models.annotations;

import at.ac.uibk.plant_health.models.Permission;
import java.lang.annotation.*;

@Inherited
@Documented
@Target ({ElementType.METHOD})
@Retention (RetentionPolicy.RUNTIME)
public @interface AllPermission {
	Permission[] value ();
}
