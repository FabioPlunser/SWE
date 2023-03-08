package at.ac.uibk.swa.models.rest_responses;

import at.ac.uibk.swa.models.Authenticable;
import at.ac.uibk.swa.models.Person;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.security.core.GrantedAuthority;

import java.io.Serializable;
import java.util.Set;
import java.util.UUID;

@Getter
@SuperBuilder
@NoArgsConstructor(access = AccessLevel.MODULE)
public class CreatedUserResponse extends RestResponse implements Serializable {

    @Override
    @JsonInclude
    public String getType() { return "CreatedUser"; }

    private UUID id;
    private String username;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private UUID token;
    private Set<GrantedAuthority> permissions;

    public CreatedUserResponse(Authenticable authenticable) {
        super.setSuccess(true);
        this.id = authenticable.getId();
        this.username = authenticable.getUsername();
        this.token = authenticable.getToken();
        this.permissions = authenticable.getPermissions();
    }

    //region Builder Customization
    public abstract static class CreatedUserResponseBuilder<
            C extends CreatedUserResponse,
            B extends CreatedUserResponseBuilder<C, B>>
        extends RestResponseBuilder<C, B>
    {
        public CreatedUserResponseBuilder<C, B> person(Person person) {
            this.id = person.getPersonId();
            this.username = person.getUsername();
            this.token = person.getToken();
            this.permissions = person.getPermissions();
            return this;
        }
    }
    //endregion
}
