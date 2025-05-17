package edu.chnu.recman_backend.recipes;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.chnu.recman_backend.TestcontainersConfiguration;
import edu.chnu.recman_backend.auth.dtos.LoginRequest;
import edu.chnu.recman_backend.auth.dtos.RegisterRequest;
import edu.chnu.recman_backend.auth.models.Role;
import edu.chnu.recman_backend.auth.models.User;
import edu.chnu.recman_backend.recipes.dtos.RecipeCreateRequest;
import edu.chnu.recman_backend.recipes.dtos.RecipeUpdateRequest;
import edu.chnu.recman_backend.recipes.models.Recipe;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@Import(TestcontainersConfiguration.class)
@SpringBootTest
@AutoConfigureMockMvc
class RecipeControllerIntegrationTest {
    private static final User USER = new User("user", "password", Role.USER);
    private static final String REGISTER_URL = "/api/recman/auth/register";
    private static final RegisterRequest REGISTER_REQUEST = new RegisterRequest("user", "password");
    private static final String LOGIN_URL = "/api/recman/auth/login";
    private static final LoginRequest LOGIN_REQUEST = new LoginRequest("user", "password");
    private static final String RECIPES_URL = "/api/recman/recipes";

    private static final RecipeCreateRequest RECIPE_CREATE_REQUEST =
            new RecipeCreateRequest("Borscht", "Soup");

    private static final Long RECIPE_ID = 1L, SECOND_RECIPE_ID = 2L;

    private static final RecipeUpdateRequest RECIPE_UPDATE_REQUEST =
            new RecipeUpdateRequest("Borscht", "New");

    private final Recipe FIRST_RECIPE = new Recipe("Borscht", "Soup", USER),
            SECOND_RECIPE = new Recipe("Varenyky", "Cheese", USER);

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private JdbcTemplate template;

    private String accessToken;

    @BeforeEach
    void setUp() throws Exception {
        template.execute("TRUNCATE TABLE recipes RESTART IDENTITY CASCADE");
        template.execute("TRUNCATE TABLE users RESTART IDENTITY CASCADE");

        mockMvc.perform(MockMvcRequestBuilders.post(REGISTER_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(REGISTER_REQUEST)))
                .andExpect(MockMvcResultMatchers.status().isOk());

        MvcResult loginResult = mockMvc.perform(MockMvcRequestBuilders.post(LOGIN_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(LOGIN_REQUEST)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        String responseBody = loginResult.getResponse().getContentAsString();
        JsonNode json = mapper.readTree(responseBody);
        accessToken = json.get("token").asText();
    }

    @Test
    void createRecipe_returnsCreatedRecipe() throws Exception {
        mockMvc.perform(authorized(MockMvcRequestBuilders.post(RECIPES_URL))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(RECIPE_CREATE_REQUEST)))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value(RECIPE_CREATE_REQUEST.name()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.description")
                        .value(RECIPE_CREATE_REQUEST.description()));
    }

    @Test
    void readAllRecipes_returnsEmptyListInitially() throws Exception {
        mockMvc.perform(authorized(MockMvcRequestBuilders.get(RECIPES_URL)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json("[]"));
    }

    @Test
    void readRecipeById_returnsCorrectRecipe() throws Exception {
        mockMvc.perform(authorized(MockMvcRequestBuilders.post(RECIPES_URL))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(RECIPE_CREATE_REQUEST)))
                .andExpect(MockMvcResultMatchers.status().isCreated());

        mockMvc.perform(authorized(MockMvcRequestBuilders.get(RECIPES_URL + "/" + RECIPE_ID)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value(RECIPE_CREATE_REQUEST.name()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.description")
                        .value(RECIPE_CREATE_REQUEST.description()));
    }

    @Test
    void updateRecipe_updatesFields() throws Exception {
        mockMvc.perform(authorized(MockMvcRequestBuilders.post(RECIPES_URL))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(RECIPE_CREATE_REQUEST)))
                .andExpect(MockMvcResultMatchers.status().isCreated());


        mockMvc.perform(authorized(MockMvcRequestBuilders.put(RECIPES_URL + "/" + RECIPE_ID))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(RECIPE_UPDATE_REQUEST)))
                .andExpect(MockMvcResultMatchers.status().isNoContent());

        mockMvc.perform(authorized(MockMvcRequestBuilders.get(RECIPES_URL + "/" + RECIPE_ID)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value(RECIPE_UPDATE_REQUEST.name()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.description")
                        .value(RECIPE_UPDATE_REQUEST.description()));
    }

    @Test
    void deleteRecipe_removesIt() throws Exception {
        mockMvc.perform(authorized(MockMvcRequestBuilders.post(RECIPES_URL)
                ).contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(RECIPE_CREATE_REQUEST)))
                .andExpect(MockMvcResultMatchers.status().isCreated());

        mockMvc.perform(authorized(MockMvcRequestBuilders.delete(RECIPES_URL + "/" + RECIPE_ID)))
                .andExpect(MockMvcResultMatchers.status().isNoContent());

        mockMvc.perform(authorized(MockMvcRequestBuilders.get(RECIPES_URL + "/" + RECIPE_ID)))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    void creatingDuplicateName_returnsConflict() throws Exception {

        mockMvc.perform(authorized(MockMvcRequestBuilders.post(RECIPES_URL))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(RECIPE_CREATE_REQUEST)))
                .andExpect(MockMvcResultMatchers.status().isCreated());

        mockMvc.perform(authorized(MockMvcRequestBuilders.post(RECIPES_URL))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(RECIPE_CREATE_REQUEST)))
                .andExpect(MockMvcResultMatchers.status().isConflict())
                .andExpect(MockMvcResultMatchers.jsonPath("$.error").exists());
    }

    @Test
    void updateRecipe_withDuplicateName_returnsConflict() throws Exception {
        mockMvc.perform(authorized(MockMvcRequestBuilders.post(RECIPES_URL))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(FIRST_RECIPE)))
                .andExpect(MockMvcResultMatchers.status().isCreated());

        mockMvc.perform(authorized(MockMvcRequestBuilders.post(RECIPES_URL))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(SECOND_RECIPE)))
                .andExpect(MockMvcResultMatchers.status().isCreated());

        mockMvc.perform(authorized(MockMvcRequestBuilders.put(RECIPES_URL + "/" + SECOND_RECIPE_ID))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(RECIPE_UPDATE_REQUEST)))
                .andExpect(MockMvcResultMatchers.status().isConflict())
                .andExpect(MockMvcResultMatchers.jsonPath("$.error").exists());
    }

    private MockHttpServletRequestBuilder authorized(MockHttpServletRequestBuilder builder) {
        return builder.header("Authorization", "Bearer " + accessToken);
    }
}
