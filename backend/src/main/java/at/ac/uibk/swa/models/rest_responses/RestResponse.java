package at.ac.uibk.swa.models.rest_responses;

import at.ac.uibk.swa.util.SerializationUtil;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

import java.io.Serializable;

/**
 * Base Class for sending back JSON-Data from REST-Endpoints.
 * Contains a "success"-Field so the Front-End can determine
 * if an Operation was completed successfully.
 * Also contains a "statusCode"-Field which the {@link org.springframework.http.ResponseEntity}
 * can use to set custom Error Codes.
 *
 * @author davirieser
 * @version 1.1
 */
@Getter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.MODULE)
public abstract class RestResponse implements Serializable {

    //region Constructors
    protected RestResponse(boolean success) {
        this(success, success ? HttpStatus.OK : HttpStatus.NOT_FOUND);
    }

    protected RestResponse(boolean success, int statusCode) {
        this(success, HttpStatusCode.valueOf(statusCode));
    }

    protected RestResponse(boolean success, HttpStatus status) {
        this(success, status.value());
    }
    //endregion

    /**
     * Indicates the Success-State of an Operation to the Front-End.
     */
    @Setter(AccessLevel.PROTECTED)
    @Builder.Default
    private boolean success = false;

    /**
     * Indicate the Type of the Response.
     *
     * @return The Type of the Response.
     */
    @JsonInclude
    public abstract String getType();

    //region Status Code
    @JsonIgnore
    @Builder.Default
    private HttpStatusCode statusCode = HttpStatusCode.valueOf(HttpStatus.NOT_FOUND.value());

    public static HttpStatusCode successToStatusCode(boolean success) {
        return HttpStatusCode.valueOf((success ? HttpStatus.OK : HttpStatus.NO_CONTENT).value());
    }
    //endregion

    //region Response Conversions
    @SuppressWarnings("unused")
    public String toResponse() {
        return SerializationUtil.serializeJSON(this);
    }

    public RestResponseEntity toEntity() {
        return new RestResponseEntity(this);
    }
    //endregion

    //region Builder Customization
    public abstract static class RestResponseBuilder<
            C extends RestResponse,
            B extends RestResponseBuilder<C, B>>
    {
        /**
         * Indicate that the Operation succeeded.
         * Synonymous with {@link RestResponseBuilder#success}(true)
         */
        public B ok() {
            return this.success(true);
        }

        /**
         * Indicate that the Operation failed.
         * Synonymous with {@link RestResponseBuilder#success}(false)
         */
        public B error() {
            return this.success(false);
        }

        /**
         * Set the {@link RestResponse#success}-Flag of the {@link RestResponse} that is being built.
         * If the {@link RestResponse#statusCode} was not already set, it is set according to the success-Parameter.
         *
         * @param success The Result of the Operation.
         */
        public B success(boolean success) {
            this.success$value = success;
            this.success$set = true;

            // If the Status Code was not set yet, set it using the success-Flag.
            if (!this.statusCode$set) this.statusCode(successToStatusCode(success));

            return (B) this;
        }

        /**
         * Manually set the Status Code of the Response.
         *
         * @param statusCode The Status Code to set
         */
        public B statusCode(int statusCode) {
            return this.statusCode(HttpStatusCode.valueOf(statusCode));
        }

        /**
         * Manually set the Status Code of the Response.
         *
         * @param httpStatus The Status Code to set
         */
        public B statusCode(HttpStatus httpStatus) {
            return this.statusCode(httpStatus.value());
        }

        /**
         * Manually set the Status Code of the Response.
         *
         * @param httpStatusCode The Status Code to set
         */
        public B statusCode(HttpStatusCode httpStatusCode) {
            this.statusCode$value = httpStatusCode;
            this.statusCode$set = true;

            return (B) this;
        }

        /**
         * Automatically the {@link RestResponse} into a {@link RestResponseEntity}
         *
         * @return The {@link RestResponseEntity} containing the built {@link RestResponse}.
         */
        public RestResponseEntity toEntity() {
            return new RestResponseEntity(this.build());
        }
    }
    //endregion
}
