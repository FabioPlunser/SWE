package at.ac.uibk.swa.util;

import jakarta.servlet.http.HttpServletRequest;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.web.util.matcher.*;
import org.springframework.stereotype.Component;

import java.util.Arrays;

/**
 * Helper Class for keeping track of the Endpoints and their respective required {@link at.ac.uibk.swa.models.Permission}s.
 *
 * @author David Rieser
 */
@Component
public class EndpointMatcherUtil {

    //region Constants and @Value-Injected Properties
    //region Base Routes
    @Getter
    @Value(API_BASE_VALUE)
    private String apiBaseRoute;
    @Getter
    @Value(ADMIN_BASE_VALUE)
    private String adminBaseRoute;
    @Getter
    @Value(ERROR_BASE_VALUE)
    private String errorBaseRoute;
    //endregion

    //region Public API Endpoints
    private final static String API_BASE_VALUE = "${swa.api.base:/api}";
    private final static String ADMIN_BASE_VALUE = "${swa.admin.base:/admin}";
    private final static String ERROR_BASE_VALUE = "${swa.error.base:/error}";

    public final static String LOGIN_ENDPOINT = "/login";
    public final static String LOGOUT_ENDPOINT = "/logout";
    public final static String REGISTER_ENDPOINT = "/register";

    @Getter
    @Value(LOGIN_ENDPOINT)
    private String apiLoginEndpoint;
    @Getter
    @Value(LOGOUT_ENDPOINT)
    private String apiLogoutEndpoint;
    @Getter
    @Value(REGISTER_ENDPOINT)
    private String apiRegisterEndpoint;
    //endregion

    //region Error Endpoints
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class ErrorEndpoints {
        public static final String TOKEN_EXPIRED_ERROR_ENDPOINT = "/token-expired";
        public static final String AUTHENTICATION_ERROR_ENDPOINT = "/unauthorized";
        public static final String AUTHORIZATION_ERROR_ENDPOINT = "/forbidden";
        public static final String NOT_FOUND_ERROR_ENDPOINT = "/notFound";
        public static final String ERROR_ENDPOINT = "/error";
    }

    private final String[] errorEndpoints =
            // Get all Error Routes defined in this Class using Runtime Reflection
            Arrays.stream(ErrorEndpoints.class.getDeclaredFields())
                    // Only get static Fields of type <String> which contain the Error Endpoints.
                    .filter(ReflectionUtil.isAssignableFromPredicate(String.class))
                    .filter(ReflectionUtil::isStaticField)
                    // Get the Endpoints
                    .map(ReflectionUtil::<String>getStaticFieldValueTyped)
                    .toArray(String[]::new);
    //endregion
    //endregion

    //region Route Matchers
    public RequestMatcher getApiRouteRequestMatcher() {
        return new AntPathRequestMatcher(this.apiBaseRoute + "/**");
    }
    public RequestMatcher getAdminRouteRequestMatcher() {
        return new AntPathRequestMatcher(this.adminBaseRoute + "/**");
    }

    public RequestMatcher[] getErrorRouteRequestMatchers() {
        return Arrays.stream(this.errorEndpoints)
                .map(route -> apiBaseRoute + route)
                .map(AntPathRequestMatcher::new)
                .toArray(RequestMatcher[]::new);
    }

    public RequestMatcher getPublicRouteRequestMatcher() {
        return new OrRequestMatcher(
                new AntPathRequestMatcher("/api/login"),
                // NOTE: DON'T ADD THE LOGOUT-ENDPOINT TO PUBLIC ROUTES.
                //       THE LOGOUT IS DONE USING THE TOKEN FROM THE REQUEST.
                // new AntPathRequestMatcher(this.apiLogoutEndpoint),
                new AntPathRequestMatcher(this.apiRegisterEndpoint)
        );
    }

    public RequestMatcher getProtectedApiRequestMatcher() {
        return new AndRequestMatcher(
            this.getApiRouteRequestMatcher(),
                new NegatedRequestMatcher(this.getPublicRouteRequestMatcher())
        );
    }

    public RequestMatcher getProtectedRouteRequestMatcher() {
        return new AndRequestMatcher(
                new OrRequestMatcher(this.getApiRouteRequestMatcher(), this.getAdminRouteRequestMatcher()),
                new NegatedRequestMatcher(this.getPublicRouteRequestMatcher())
        );
    }
    //endregion

    public boolean isApiRoute(HttpServletRequest request) {
        return this.getApiRouteRequestMatcher().matches(request);
    }
    public boolean isAdminRoute(HttpServletRequest request) {
        return this.getApiRouteRequestMatcher().matches(request);
    }

    public String toApiEndpoint(String route) {
        return String.format("%s/%s", this.apiBaseRoute, route);
    }

    public String toAdminEndpoint(String route) {
        return String.format("%s/%s", this.adminBaseRoute, route);
    }
}
