package at.ac.uibk.plant_health.models.rest_responses;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.io.Serializable;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.security.core.GrantedAuthority;

@Getter
@SuperBuilder
@NoArgsConstructor(access = AccessLevel.MODULE)
@AllArgsConstructor
public class PermissionResponse extends RestResponse implements Serializable {
    @Override
    @JsonInclude
    public String getType() {
        return "Permission";
    }

    private GrantedAuthority[] permissions;

    public PermissionResponse(boolean successful, GrantedAuthority[] permissions) {
        super(successful);
        this.permissions = permissions;
    }
}
