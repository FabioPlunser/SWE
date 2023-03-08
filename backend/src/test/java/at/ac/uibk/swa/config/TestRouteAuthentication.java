package at.ac.uibk.swa.config;

import at.ac.uibk.swa.config.jwt_authentication.JwtToken;
import at.ac.uibk.swa.models.Permission;
import at.ac.uibk.swa.models.Person;
import at.ac.uibk.swa.service.PersonService;
import at.ac.uibk.swa.util.*;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.util.Pair;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class TestRouteAuthentication {
    //region Autowired + Fields
    @Autowired
    private PersonService personService;

    @Autowired
    private EndpointMatcherUtil endpointMatcherUtil;

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Autowired
    private MockMvc mockMvc;

    private String TEST_ANONYMOUS_ENDPOINT() {
        return "/testAnonymous";
    }
    private String TEST_API_ENDPOINT() {
        return endpointMatcherUtil.apiRoute("/test");
    }
    private String TEST_API_ADMIN_ENDPOINT() {
        return endpointMatcherUtil.apiRoute("/testAdmin");
    }
    private String TEST_ADMIN_ENDPOINT() {
        return endpointMatcherUtil.adminRoute("/test");
    }

    private String[] TEST_ERROR_ENDPOINTS() {
        return endpointMatcherUtil.getApiErrorEndpoints();
    }
    //endregion

    //region Helper Methods
    private Person createUserWithToken(boolean alsoAdmin) {
        String username = StringGenerator.username();
        String password = StringGenerator.password();
        String email = StringGenerator.email();
        Set<GrantedAuthority> permissions = alsoAdmin ? Permission.adminAuthorities() : Permission.defaultAuthorities();
        Person person = new Person(username, email, password, UUID.randomUUID(), permissions);
        assertTrue(personService.create(person), "Unable to create user");
        return person;
    }

    @SafeVarargs
    private HttpHeaders generateHttpHeaders(Pair<String, String> ... headers) {
        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        for (Pair<String, String> header : headers) {
            map.add(header.getFirst(), header.getSecond());
        }
        return new HttpHeaders(map);
    }
    //endregion

    //region Anonymous Route Tests
    @Test
    public void testAnonymousAccessingAnonymousRouteWithOutCredentials() throws Exception {
        // given: No created Persons

        // when: Accessing an Anonymous Route without Credentials (anonymous)
        mockMvc.perform(MockMvcRequestBuilders.get(TEST_ANONYMOUS_ENDPOINT())
                        .contentType(MediaType.APPLICATION_JSON)
                // then: Expect that the Page is returned
        ).andExpectAll(
                status().isOk()
        );
    }

    @Test
    public void testAnonymousAccessingAnonymousRouteWithCredentials() throws Exception {
        // given: A Person not stored in the database
        Person notSavedPerson = new Person(StringGenerator.username(), StringGenerator.email(), StringGenerator.password(), UUID.randomUUID(), Set.of());

        // when: Accessing an Anonymous Page with Credentials (anonymous)
        mockMvc.perform(MockMvcRequestBuilders.get(TEST_ANONYMOUS_ENDPOINT())
                        .header(HttpHeaders.AUTHORIZATION, AuthGenerator.generateToken(notSavedPerson))
                        .contentType(MediaType.APPLICATION_JSON)
                // then: Expect that the Page is returned
        ).andExpectAll(
                status().isOk()
        );
    }

    @Test
    public void testUserAccessingAnonymousRoute() throws Exception {
        // given: A Person without Admin-Permission
        Person person = createUserWithToken(false);

        // when: Accessing an Anonymous Page with Credentials
        mockMvc.perform(MockMvcRequestBuilders.get(TEST_ANONYMOUS_ENDPOINT())
                        .header(HttpHeaders.AUTHORIZATION, AuthGenerator.generateToken(person))
                        .contentType(MediaType.APPLICATION_JSON)
                // then: Expect that the Page is returned
        ).andExpectAll(
                status().isOk()
        );
    }

    @Test
    public void testAdminAccessingAnonymousRoute() throws Exception {
        // given: A Person with Admin-Permission
        Person person = createUserWithToken(true);

        // when: Accessing an Anonymous Page with Credentials
        mockMvc.perform(MockMvcRequestBuilders.get(TEST_ANONYMOUS_ENDPOINT())
                        .header(HttpHeaders.AUTHORIZATION, AuthGenerator.generateToken(person))
                        .contentType(MediaType.APPLICATION_JSON)
                // then: Expect that the Page is returned
        ).andExpectAll(
                status().isOk()
        );
    }
    //endregion

    //region Error Route Tests
    /* TODO: This doesn't seem to work because it needs an injected Exception in the Endpoints
    @Test
    public void testErrorRoutesWithoutCredentials() {
        // given: No created Persons

        // when: accessing any Error Page with Credentials
        testAllErrorRoutes(HttpHeaders.EMPTY);

        // then: expect that the Page is returned.
    }

    @Test
    public void testErrorRoutesWithInvalidCredentials() throws JsonProcessingException {
        // given: A Person that isn't stored in the database
        Person notSavedPerson = new Person("", "", "", UUID.randomUUID(), Set.of());

        // when: accessing any Error Page with unknown Credentials
        testAllErrorRoutes(new HttpHeaders(
                generateHttpHeaders(Pair.of(
                        HttpHeaders.AUTHORIZATION,
                        AuthGenerator.generateToken(notSavedPerson)
                ))
        ));

        // then: expect that the Page is returned.
    }

    @Test
    public void testErrorRoutesWithCredentials() throws JsonProcessingException {
        // given: A Person without Admin Permission
        Person person = createUserWithToken(false);

        // when: accessing any Error Page with unknown Credentials
        testAllErrorRoutes(new HttpHeaders(
                generateHttpHeaders(Pair.of(
                        HttpHeaders.AUTHORIZATION,
                        AuthGenerator.generateToken(person)
                ))
        ));

        // then: expect that the Page is returned.
    }

    @Test
    public void testErrorRoutesWithAdminCredentials() throws JsonProcessingException {
        // given: A Person with Admin Permission
        Person person = createUserWithToken(true);

        // when: accessing any Error Page with unknown Credentials
        testAllErrorRoutes(new HttpHeaders(
                generateHttpHeaders(Pair.of(
                        HttpHeaders.AUTHORIZATION,
                        AuthGenerator.generateToken(person)
                ))
        ));

        // then: expect that the Page is returned.
    }

    public void testAllErrorRoutes(HttpHeaders headers) {
        // when: accessing any Error Page
        for (String endpoint : TEST_ERROR_ENDPOINTS()) {
            // when: using any HTTP Method
            for (HttpMethod method : List.of(HttpMethod.GET, HttpMethod.PUT, HttpMethod.POST, HttpMethod.DELETE)) {
                try {
                    mockMvc.perform(MockMvcRequestBuilders.request(method, endpoint)
                                    .headers(headers)
                                    .contentType(MediaType.APPLICATION_JSON)
                    ).andExpectAll(
                            // then: Expect that the Page is returned
                            status().isOk()
                    );
                } catch (Exception e) {
                    assertFalse(true, String.format("Could not perform %s Request to ", method, endpoint));
                }
            }
        }
    }
     */
    //endregion

    //region Api Route Tests
    //region Api Route Tests
    @Test
    public void testAnonymousAccessingApiRouteWithOutCredentials() throws Exception {
        // given: No created Persons

        // when: Accessing an Api Route without Credentials (anonymous)
        mockMvc.perform(MockMvcRequestBuilders.get(TEST_API_ENDPOINT())
                        .contentType(MediaType.APPLICATION_JSON)
                // then: Expect an Authentication Exception resulting in a 401 Error Code
        ).andExpectAll(
                status().is(Matchers.allOf(Matchers.greaterThan(300), Matchers.lessThan(500)))
        );
    }

    @Test
    public void testAnonymousAccessingApiRouteWithCredentials() throws Exception {
        // given: A Person not stored in the database
        Person notSavedPerson = new Person("", "", "", UUID.randomUUID(), Set.of());

        // when: Accessing an Admin Page with Credentials (anonymous)
        mockMvc.perform(MockMvcRequestBuilders.get(TEST_API_ENDPOINT())
                        .header(HttpHeaders.AUTHORIZATION, AuthGenerator.generateToken(notSavedPerson))
                        .contentType(MediaType.APPLICATION_JSON)
                // then: Expect an Authentication Exception resulting in a 401 Error Code
        ).andExpectAll(
                status().is(Matchers.allOf(Matchers.greaterThan(300), Matchers.lessThan(500)))
        );
    }

    @Test
    public void testUserAccessingApiRoute() throws Exception {
        // given: A Person without Admin-Permission
        Person person = createUserWithToken(false);

        // when: Accessing an Admin Page with Credentials
        mockMvc.perform(MockMvcRequestBuilders.get(TEST_API_ENDPOINT())
                        .header(HttpHeaders.AUTHORIZATION, AuthGenerator.generateToken(person))
                        .contentType(MediaType.APPLICATION_JSON)
                // then: Expect that the Page is returned
        ).andExpectAll(
                status().isOk()
        );
    }

    @Test
    public void testAdminAccessingApiRoute() throws Exception {
        // given: A Person with Admin-Permission
        Person person = createUserWithToken(true);

        // when: Accessing an Admin Page with Credentials
        mockMvc.perform(MockMvcRequestBuilders.get(TEST_API_ENDPOINT())
                        .header(HttpHeaders.AUTHORIZATION, AuthGenerator.generateToken(person))
                        .contentType(MediaType.APPLICATION_JSON)
                // then: Expect that the Page is returned
        ).andExpectAll(
                status().isOk()
        );
    }
    //endregion

    //region Api Admin Route Tests
    @Test
    public void testAnonymousAccessingApiAdminRouteWithOutCredentials() throws Exception {
        // given: No created Persons

        // when: Accessing an Api Route without Credentials (anonymous)
        mockMvc.perform(MockMvcRequestBuilders.get(TEST_API_ADMIN_ENDPOINT())
                        .contentType(MediaType.APPLICATION_JSON)
                // then: Expect an Authentication Exception resulting in a 401 Error Code
        ).andExpectAll(
                status().is(Matchers.allOf(Matchers.greaterThan(300), Matchers.lessThan(500)))
        );
    }

    @Test
    public void testAnonymousAccessingApiAdminRouteWithCredentials() throws Exception {
        // given: No created Persons
        Person notSavedPerson = new Person("", "", "", UUID.randomUUID(), Set.of());

        // when: Accessing an Admin Page with Credentials (anonymous)
        mockMvc.perform(MockMvcRequestBuilders.get(TEST_API_ADMIN_ENDPOINT())
                        .header(HttpHeaders.AUTHORIZATION, AuthGenerator.generateToken(notSavedPerson))
                        .contentType(MediaType.APPLICATION_JSON)
                // then: Expect an Authentication Exception resulting in a 401 Error Code
        ).andExpectAll(
                status().is(Matchers.allOf(Matchers.greaterThan(300), Matchers.lessThan(500)))
        );
    }

    @Test
    public void testUserAccessingApiAdminRoute() throws Exception {
        // given: A Person without Admin-Permission
        Person person = createUserWithToken(false);

        // when: Accessing an Admin Page with Credentials
        mockMvc.perform(MockMvcRequestBuilders.get(TEST_API_ADMIN_ENDPOINT())
                        .header(HttpHeaders.AUTHORIZATION, AuthGenerator.generateToken(person))
                        .contentType(MediaType.APPLICATION_JSON)
                // then: Expect an Authorization Exception resulting in a 403 Error Code
        ).andExpectAll(
                status().is(Matchers.allOf(Matchers.greaterThan(300), Matchers.lessThan(500)))
        );
    }

    @Test
    public void testAdminAccessingApiAdminRoute() throws Exception {
        // given: A Person with Admin-Permission
        Person person = createUserWithToken(true);

        // when: Accessing an Admin Page with Credentials
        mockMvc.perform(MockMvcRequestBuilders.get(TEST_API_ADMIN_ENDPOINT())
                        .header(HttpHeaders.AUTHORIZATION, AuthGenerator.generateToken(person))
                        .contentType(MediaType.APPLICATION_JSON)
                // then: Expect that the Page is returned
        ).andExpectAll(
                status().isOk()
        );
    }
    //endregion
    //endregion

    //region Admin Route Tests
    @Test
    public void testAnonymousAccessingAdminRouteWithOutCredentials() throws Exception {
        // given: No created Persons

        // when: Accessing an Admin Page without Credentials (anonymous)
        mockMvc.perform(MockMvcRequestBuilders.get(TEST_ADMIN_ENDPOINT())
                        .contentType(MediaType.APPLICATION_JSON)
                // then: Expect an Authentication Exception resulting in a 401 Error Code
        ).andExpectAll(
                status().is(Matchers.allOf(Matchers.greaterThan(300), Matchers.lessThan(500)))
        );
    }

    @Test
    public void testAnonymousAccessingAdminRouteWithCredentials() throws Exception {
        // given: No created Persons

        // when: Accessing an Admin Page with Credentials (anonymous)
        mockMvc.perform(MockMvcRequestBuilders.get(TEST_ADMIN_ENDPOINT())
                .cookie(AuthGenerator.jwtTokenToCookies(new JwtToken("", UUID.randomUUID())))
                .contentType(MediaType.APPLICATION_JSON)
        // then: Expect an Authentication Exception resulting in a 401 Error Code
        ).andExpectAll(
                status().is(Matchers.allOf(Matchers.greaterThan(300), Matchers.lessThan(500)))
        );
    }

    @Test
    public void testUserAccessingAdminRoute() throws Exception {
        // given: A Person without Admin-Permission
        Person person = createUserWithToken(false);
        JwtToken jwt = new JwtToken(person);

        // when: Accessing an Admin Page with Credentials
        mockMvc.perform(MockMvcRequestBuilders.get(TEST_ADMIN_ENDPOINT())
                .cookie(AuthGenerator.jwtTokenToCookies(jwt))
                .contentType(MediaType.APPLICATION_JSON)
        // then: Expect an Authorization Exception resulting in a 403 Error Code
        ).andExpectAll(
                status().is(Matchers.allOf(Matchers.greaterThan(300), Matchers.lessThan(500)))
        );
    }

    @Test
    public void testAdminAccessingAdminRoute() throws Exception {
        // given: A Person with Admin-Permission
        Person person = createUserWithToken(true);
        JwtToken jwt = new JwtToken(person);

        // when: Accessing an Admin Page with Credentials
        mockMvc.perform(MockMvcRequestBuilders.get(TEST_ADMIN_ENDPOINT())
                        .cookie(AuthGenerator.jwtTokenToCookies(jwt))
                        .contentType(MediaType.APPLICATION_JSON)
        // then: Expect that the Page is returned
        ).andExpectAll(
                status().isOk()
        );
    }
    //endregion
}