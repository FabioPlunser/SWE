package at.ac.uibk.swa.config;

import at.ac.uibk.swa.config.jwt_authentication.JwtToken;
import at.ac.uibk.swa.models.Authenticable;
import at.ac.uibk.swa.models.Permission;
import at.ac.uibk.swa.models.Person;
import at.ac.uibk.swa.repositories.PersonRepository;
import at.ac.uibk.swa.service.PersonService;
import at.ac.uibk.swa.util.*;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class TestRouteAuthentication {
    /*
    //region Autowired + Fields
    @Autowired
    private PersonService personService;

    @Autowired
    private EndpointMatcherUtil endpointMatcherUtil;

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PersonRepository personRepository;

    @Value("${swa.token.expiration-duration:1h}")
    private Duration tokenExpirationDuration;

    private String TEST_ANONYMOUS_ENDPOINT() {
        return "/";
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
    private Person createUserWithToken() {
        return createUserWithToken(false);
    }

    private Person createUserWithToken(boolean alsoAdmin) {
        String username = StringGenerator.username();
        String password = StringGenerator.password();
        Set<GrantedAuthority> permissions = alsoAdmin ? Permission.adminAuthorities() : Permission.defaultAuthorities();
        Person person = new Person(username, StringGenerator.email(), password, UUID.randomUUID(), permissions);
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
        Person notSavedPerson = new Person("", "", "", UUID.randomUUID(), Set.of());

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

    //region Test Token Expiration
    private void setTokenCreationDate(Authenticable authenticable, LocalDateTime creationDate) throws NoSuchFieldException {
        Field tokenCreationDateField = Authenticable.class.getDeclaredField("tokenCreationDate");
        tokenCreationDateField.setAccessible(true);
        ReflectionUtils.setField(tokenCreationDateField, authenticable, creationDate);
    }

    @Test
    public void testNotExpiredToken() throws Exception {
        // given: A Person created with a Token
        Person person = createUserWithToken();

        // when: Accessing a secured Page with the expired Token
        mockMvc.perform(MockMvcRequestBuilders.get(TEST_API_ENDPOINT())
                        .header(HttpHeaders.AUTHORIZATION, AuthGenerator.generateToken(person))
                        .contentType(MediaType.APPLICATION_JSON)
                // then: Expect an OK Response
        ).andExpectAll(
                status().isOk()
        );

    }

    @Test
    public void testExpiredToken() throws Exception {
        // given: A Person created with a Token
        Person person = createUserWithToken();

        // given: Setting the Token Creation Date to be expired
        setTokenCreationDate(person, LocalDateTime.now().minus(tokenExpirationDuration));
        personRepository.updateToken(person);

        // when: Accessing a secured Page with the expired Token
        mockMvc.perform(MockMvcRequestBuilders.get(TEST_API_ENDPOINT())
                        .header(HttpHeaders.AUTHORIZATION, AuthGenerator.generateToken(person))
                        .contentType(MediaType.APPLICATION_JSON)
                // then: Expect an Unauthorized Response
        ).andExpectAll(
                status().isUnauthorized()
        );

    }
    //endregion
    */
}