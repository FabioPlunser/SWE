package at.ac.uibk.swa.models.rest_responses;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

@Getter
@SuperBuilder
public class RedirectResponse extends RestResponse {

    @JsonIgnore
    @Builder.Default
    private HttpStatusCode statusCode = HttpStatusCode.valueOf(HttpStatus.FOUND.value());

    @Override
    @JsonInclude
    public String getType() { return "Redirect"; }

    @NonNull
    @JsonIgnore
    private String redirectLocation;

    private RedirectResponse() {
        super(true, HttpStatus.FOUND);
        this.redirectLocation = "/";
    }

    public RedirectResponse(String redirectLocation) {
        super(true, HttpStatus.FOUND);
        this.redirectLocation = redirectLocation;
    }

    @Override
    public RestResponseEntity toEntity() {
        if (this.redirectLocation != null) {
            HttpHeaders headers = new HttpHeaders();
            headers.add("Location", redirectLocation);
            return new RestResponseEntity(this, headers);
        } else {
            return super.toEntity();
        }
    }

    //region Builder Customization
    public abstract static class RedirectResponseBuilder<
            C extends RedirectResponse,
            B extends RedirectResponseBuilder<C, B>>
            extends RestResponseBuilder<C, B>
    {
        @Override
        public B statusCode(int statusCode) {
            return this.statusCode(HttpStatusCode.valueOf(statusCode));
        }

        @Override
        public B statusCode(HttpStatus httpStatus) {
            return this.statusCode(HttpStatusCode.valueOf(httpStatus.value()));
        }

        @Override
        public B statusCode(HttpStatusCode httpStatusCode) {
            if (httpStatusCode.is3xxRedirection()) {
                return super.statusCode(httpStatusCode);
            } else {
                return super.statusCode(HttpStatus.FOUND);
            }
        }
    }
    //endregion
}
