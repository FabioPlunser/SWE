package at.ac.uibk.swa.models.rest_responses;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;

@Getter
@SuperBuilder
@NoArgsConstructor(access = AccessLevel.MODULE)
@AllArgsConstructor
public class AuthFailedResponse extends RestResponse implements Serializable {

    @Override
    @JsonInclude
    public String getType() { return "AuthFailed"; }

    private String message;
}
