package edu.chnu.recman_backend.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.chnu.recman_backend.TestcontainersConfiguration;
import edu.chnu.recman_backend.auth.dtos.LoginRequest;
import edu.chnu.recman_backend.auth.dtos.RegisterRequest;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@Import(TestcontainersConfiguration.class)
@SpringBootTest
@AutoConfigureMockMvc
class AuthControllerIntegrationTest {
    private static final RegisterRequest REGISTER_REQUEST = new RegisterRequest("user", "password");
    private static final LoginRequest LOGIN_REQUEST = new LoginRequest("user", "password");

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private JdbcTemplate template;

    @BeforeEach
    void setUp() {
        template.execute("TRUNCATE TABLE users");
    }

    @Test
    void register_thenLogin_successfully() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/api/recman/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(REGISTER_REQUEST)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.username").value("user"));

        mockMvc.perform(MockMvcRequestBuilders.post("/api/recman/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(LOGIN_REQUEST)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.token").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.token", Matchers.containsString("ey")));
    }

    @Test
    void register_sameUsername_shouldReturnConflict() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/api/recman/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(REGISTER_REQUEST)))
                .andExpect(MockMvcResultMatchers.status().isOk());

        mockMvc.perform(MockMvcRequestBuilders.post("/api/recman/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(LOGIN_REQUEST)))
                .andExpect(MockMvcResultMatchers.status().isConflict())
                .andExpect(MockMvcResultMatchers.jsonPath("$.error").value("Username already exists"));
    }
}
