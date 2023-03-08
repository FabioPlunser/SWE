package at.ac.uibk.swa.util;

import jakarta.annotation.PostConstruct;
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

    //region Base Routes
    @Getter
    @Value("${swa.api.base:/api}")
    private String apiBaseRoute;
    @Getter
    @Value("${swa.admin.base:/admin}")
    private String adminBaseRoute;
    @Getter
    @Value("${swa.error.base:/error}")
    private String errorBaseRoute;

    public String apiRoute(String route) {
        return apiBaseRoute + route;
    }
    public String adminRoute(String route) {
        return adminBaseRoute + route;
    }
    //endregion

    //region Login-, register-, and logout-Endpoints
    public static final String LOGIN_ENDPOINT = "/login";
    public static final String LOGOUT_ENDPOINT = "/logout";
    public static final String REGISTER_ENDPOINT = "/register";

    @Getter
    private String apiLoginEndpoint;
    @Getter
    private String apiLogoutEndpoint;
    @Getter
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

    @Getter
    private String[] apiErrorEndpoints;
    //endregion

    //region Route Matchers
    @Getter
    private RequestMatcher anonymousRoutes;
    @Getter
    private RequestMatcher apiRoutes;
    @Getter
    private RequestMatcher adminRoutes;

    @Getter
    private RequestMatcher publicApiRoutes;

    /**
     * Request Matcher matching all API-Routes that should be protected.
     */
    @Getter
    private RequestMatcher protectedApiRoutes;

    /**
     * Request Matcher matching all Routes that should be accessable to everyone.
     */
    @Getter
    private RequestMatcher publicRoutes;

    /**
     * Request Matcher matching all Routes that should be protected.
     */
    @Getter
    private RequestMatcher protectedRoutes;
    //endregion

    //region Initialization
    @PostConstruct
    private void init() {
        this.apiLoginEndpoint = this.apiRoute(LOGIN_ENDPOINT);
        this.apiLogoutEndpoint = this.apiRoute(LOGOUT_ENDPOINT);
        this.apiRegisterEndpoint = this.apiRoute(REGISTER_ENDPOINT);

        this.apiErrorEndpoints = Arrays.stream(this.errorEndpoints)
                .map(this::apiRoute)
                .toArray(String[]::new);

        RequestMatcher errorRoutes = new OrRequestMatcher(
                Arrays.stream(this.apiErrorEndpoints)
                        .map(AntPathRequestMatcher::new)
                        .toArray(AntPathRequestMatcher[]::new)
        );

        this.anonymousRoutes = AnyRequestMatcher.INSTANCE;
        this.apiRoutes = new AntPathRequestMatcher(apiRoute("/**"));
        this.adminRoutes = new AntPathRequestMatcher(adminRoute("/**"));

        this.publicApiRoutes = new OrRequestMatcher(
                new AntPathRequestMatcher(this.apiLoginEndpoint),
                // NOTE: DON'T ADD THE LOGOUT-ENDPOINT TO PUBLIC ROUTES, THE LOGOUT IS DONE USING THE TOKEN FROM THE REQUEST.
                // new AntPathRequestMatcher(API_LOGOUT_ENDPOINT),
                new AntPathRequestMatcher(this.apiRegisterEndpoint),
                errorRoutes
        );
        this.protectedApiRoutes = new AndRequestMatcher(
                this.apiRoutes, new NegatedRequestMatcher(this.publicApiRoutes)
        );

        this.publicRoutes = new OrRequestMatcher(
                new NegatedRequestMatcher(new OrRequestMatcher(this.apiRoutes, this.adminRoutes)),
                this.anonymousRoutes
        );
        this.protectedRoutes = new NegatedRequestMatcher(this.publicRoutes);
    }
    //endregion

    //region Route Matching Helper Methods
    public boolean isPublicRoute(HttpServletRequest request) {
        return publicRoutes.matches(request);
    }

    public boolean isApiRoute(HttpServletRequest request) {
        return apiRoutes.matches(request);
    }

    public boolean isAdminRoute(HttpServletRequest request) {
        return adminRoutes.matches(request);
    }
    //endregion
}
