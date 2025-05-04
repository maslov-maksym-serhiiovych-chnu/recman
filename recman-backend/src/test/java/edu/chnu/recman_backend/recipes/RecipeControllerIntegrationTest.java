package edu.chnu.recman_backend.recipes;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.chnu.recman_backend.TestcontainersConfiguration;
import edu.chnu.recman_backend.recipes.dtos.RecipeCreateRequest;
import edu.chnu.recman_backend.recipes.dtos.RecipeUpdateRequest;
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

        mockMvc.perform(MockMvcRequestBuilders.post("/api/recman/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                    {
                                        "username": "user",
                                        "password": "password"
                                    }
                                """))
                .andExpect(MockMvcResultMatchers.status().isOk());

        MvcResult loginResult = mockMvc.perform(MockMvcRequestBuilders.post("/api/recman/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                    {
                                        "username": "user",
                                        "password": "password"
                                    }
                                """))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        String responseBody = loginResult.getResponse().getContentAsString();
        JsonNode json = mapper.readTree(responseBody);
        accessToken = json.get("token").asText();
    }

    @Test
    void createRecipe_returnsCreatedRecipe() throws Exception {
        RecipeCreateRequest request = new RecipeCreateRequest("Borscht", "Ukrainian soup");

        mockMvc.perform(authorized(MockMvcRequestBuilders.post("/api/recman/recipes"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request)))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value("Borscht"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.description").value("Ukrainian soup"));
    }

    @Test
    void readAllRecipes_returnsEmptyListInitially() throws Exception {
        mockMvc.perform(authorized(MockMvcRequestBuilders.get("/api/recman/recipes")))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json("[]"));
    }

    @Test
    void readRecipeById_returnsCorrectRecipe() throws Exception {
        RecipeCreateRequest request = new RecipeCreateRequest("Borscht", "Ukrainian soup");
        mockMvc.perform(authorized(MockMvcRequestBuilders.post("/api/recman/recipes"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request)))
                .andExpect(MockMvcResultMatchers.status().isCreated());

        mockMvc.perform(authorized(MockMvcRequestBuilders.get("/api/recman/recipes/1")))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value("Borscht"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.description").value("Ukrainian soup"));
    }

    @Test
    void updateRecipe_updatesFields() throws Exception {
        RecipeCreateRequest request = new RecipeCreateRequest("Borscht", "Ukrainian soup");
        mockMvc.perform(authorized(MockMvcRequestBuilders.post("/api/recman/recipes"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request)))
                .andExpect(MockMvcResultMatchers.status().isCreated());

        RecipeUpdateRequest updateRequest = new RecipeUpdateRequest("Green Borscht", "With sorrel");

        mockMvc.perform(authorized(MockMvcRequestBuilders.put("/api/recman/recipes/1"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(updateRequest)))
                .andExpect(MockMvcResultMatchers.status().isNoContent());

        mockMvc.perform(authorized(MockMvcRequestBuilders.get("/api/recman/recipes/1")))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value("Green Borscht"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.description").value("With sorrel"));
    }

    @Test
    void deleteRecipe_removesIt() throws Exception {
        RecipeCreateRequest request = new RecipeCreateRequest("Borscht", "Ukrainian soup");
        mockMvc.perform(authorized(MockMvcRequestBuilders.post("/api/recman/recipes")
                ).contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request)))
                .andExpect(MockMvcResultMatchers.status().isCreated());

        mockMvc.perform(authorized(MockMvcRequestBuilders.delete("/api/recman/recipes/1")))
                .andExpect(MockMvcResultMatchers.status().isNoContent());

        mockMvc.perform(authorized(MockMvcRequestBuilders.get("/api/recman/recipes/1")))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    void creatingDuplicateName_returnsConflict() throws Exception {
        RecipeCreateRequest request = new RecipeCreateRequest("Borscht", "Ukrainian soup");

        mockMvc.perform(authorized(MockMvcRequestBuilders.post("/api/recman/recipes"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request)))
                .andExpect(MockMvcResultMatchers.status().isCreated());

        mockMvc.perform(authorized(MockMvcRequestBuilders.post("/api/recman/recipes"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request)))
                .andExpect(MockMvcResultMatchers.status().isConflict())
                .andExpect(MockMvcResultMatchers.jsonPath("$.error").exists());
    }

    @Test
    void updateRecipe_withDuplicateName_returnsConflict() throws Exception {
        RecipeCreateRequest first = new RecipeCreateRequest("Borscht", "Ukrainian soup");
        mockMvc.perform(authorized(MockMvcRequestBuilders.post("/api/recman/recipes"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(first)))
                .andExpect(MockMvcResultMatchers.status().isCreated());

        RecipeCreateRequest second = new RecipeCreateRequest("Varenyky", "Ukrainian dumplings");
        mockMvc.perform(authorized(MockMvcRequestBuilders.post("/api/recman/recipes"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(second)))
                .andExpect(MockMvcResultMatchers.status().isCreated());

        RecipeUpdateRequest updateRequest = new RecipeUpdateRequest("Borscht", "Now it's soup too");
        mockMvc.perform(authorized(MockMvcRequestBuilders.put("/api/recman/recipes/2"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(updateRequest)))
                .andExpect(MockMvcResultMatchers.status().isConflict())
                .andExpect(MockMvcResultMatchers.jsonPath("$.error").exists());
    }

    private MockHttpServletRequestBuilder authorized(MockHttpServletRequestBuilder builder) {
        return builder.header("Authorization", "Bearer " + accessToken);
    }
}
