package at.ac.uibk.plant_health.models.rest_responses;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.io.Serializable;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
@NoArgsConstructor(access = AccessLevel.MODULE)
@AllArgsConstructor
public class MessageResponse extends RestResponse implements Serializable {
    @Override
    @JsonInclude
    public String getType() {
        return "Message";
    }

    private String message;

    public MessageResponse(boolean successful, String message) {
        super(successful);
        this.message = message;
    }
}
