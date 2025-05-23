package edu.chnu.recman_backend.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.chnu.recman_backend.TestcontainersConfiguration;
import edu.chnu.recman_backend.auth.dtos.LoginRequest;
import edu.chnu.recman_backend.auth.dtos.RegisterRequest;
import edu.chnu.recman_backend.auth.exceptions.UsernameAlreadyExistsException;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@Import(TestcontainersConfiguration.class)
@SpringBootTest
@AutoConfigureMockMvc
class AuthControllerIntegrationTest {
    private static final RegisterRequest REGISTER_REQUEST = new RegisterRequest("user", "password");
    private static final LoginRequest LOGIN_REQUEST = new LoginRequest("user", "password");

    private static final LoginRequest INVALID_LOGIN_REQUEST =
            new LoginRequest("user", "wrong-password");

    private static final UsernameAlreadyExistsException USERNAME_ALREADY_EXISTS_EXCEPTION =
            new UsernameAlreadyExistsException();

    private static final BadCredentialsException BAD_CREDENTIALS_EXCEPTION =
            new BadCredentialsException("Bad credentials");

    private static final String REGISTER_URL = "/api/recman/auth/register";
    private static final String LOGIN_URL = "/api/recman/auth/login";
    private static final String VALIDATE_URL = "/api/recman/auth/validate";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private JdbcTemplate template;

    @BeforeEach
    void setUp() {
        template.execute("TRUNCATE TABLE users RESTART IDENTITY CASCADE");
    }

    @Test
    void register_thenLogin_successfully() throws Exception {
        performRegister();

        mockMvc.perform(buildLoginRequest(LOGIN_REQUEST))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string(Matchers.containsString("ey")));
    }

    @Test
    void register_sameUsername_shouldReturnConflict() throws Exception {
        performRegister();

        mockMvc.perform(json(MockMvcRequestBuilders.post(REGISTER_URL), REGISTER_REQUEST))
                .andExpect(MockMvcResultMatchers.status().isConflict())
                .andExpect(MockMvcResultMatchers.jsonPath("$.error")
                        .value(USERNAME_ALREADY_EXISTS_EXCEPTION.getMessage()));
    }

    @Test
    void login_withInvalidCredentials_shouldReturnUnauthorized() throws Exception {
        performRegister();

        mockMvc.perform(buildLoginRequest(INVALID_LOGIN_REQUEST))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized())
                .andExpect(MockMvcResultMatchers.jsonPath("$.error")
                        .value(BAD_CREDENTIALS_EXCEPTION.getMessage()));
    }

    @Test
    void validate_withValidToken_shouldReturnOk() throws Exception {
        performRegister();

        String token = mockMvc.perform(buildLoginRequest(LOGIN_REQUEST))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        mockMvc.perform(MockMvcRequestBuilders.get(VALIDATE_URL)
                        .header("Authorization", "Bearer " + token))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    void validate_withoutToken_shouldReturnUnauthorized() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(VALIDATE_URL))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized());
    }

    private void performRegister() throws Exception {
        mockMvc.perform(json(MockMvcRequestBuilders.post(REGISTER_URL), REGISTER_REQUEST))
                .andExpect(MockMvcResultMatchers.status().isNoContent());
    }

    private MockHttpServletRequestBuilder buildLoginRequest(Object request) throws Exception {
        return json(MockMvcRequestBuilders.post(LOGIN_URL), request);
    }

    private MockHttpServletRequestBuilder json(MockHttpServletRequestBuilder builder, Object body) throws Exception {
        return builder.contentType(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(body));
    }
}
