package edu.chnu.recipes_api;

import edu.chnu.recipes_api.recipes.RecipeDTO;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.http.*;
import org.springframework.jdbc.core.JdbcTemplate;

@Import(TestcontainersConfiguration.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class RecipesApiIntegrationTests {
    private static final RecipeDTO PANCAKES = new RecipeDTO("Pancakes", "Mix flour, eggs, and milk. Fry on pan until golden"),
            SPAGHETTI = new RecipeDTO("Spaghetti Bolognese", "Cook pasta. Prepare meat sauce. Mix and serve");

    @LocalServerPort
    private int port;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private TestRestTemplate testRestTemplate;

    private String getUrl(String path) {
        return "http://localhost:" + port + "/api/v0.1/recipes" + path;
    }

    @BeforeEach
    void setUp() {
        jdbcTemplate.execute("TRUNCATE TABLE recipes RESTART IDENTITY CASCADE;");
    }

    @Test
    void testCreateAndGet() {
        ResponseEntity<RecipeDTO> createResponse = testRestTemplate.postForEntity(getUrl(""), PANCAKES, RecipeDTO.class);

        Assertions.assertEquals(HttpStatus.CREATED, createResponse.getStatusCode());
        RecipeDTO created = createResponse.getBody();
        assertEquals(PANCAKES, created);

        ResponseEntity<RecipeDTO> getResponse = testRestTemplate.getForEntity(getUrl("/" + 1), RecipeDTO.class);

        Assertions.assertEquals(HttpStatus.OK, getResponse.getStatusCode());
        RecipeDTO fetched = getResponse.getBody();
        assertEquals(PANCAKES, fetched);
    }

    @Test
    void testUpdateAndDeleteRecipe() {
        ResponseEntity<RecipeDTO> response = testRestTemplate.postForEntity(getUrl(""), PANCAKES, RecipeDTO.class);

        Assertions.assertNotNull(response.getBody());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<RecipeDTO> requestEntity = new HttpEntity<>(SPAGHETTI, headers);
        ResponseEntity<Void> updateResponse = testRestTemplate.exchange(getUrl("/" + 1), HttpMethod.PUT, requestEntity, Void.class);

        Assertions.assertEquals(HttpStatus.NO_CONTENT, updateResponse.getStatusCode());

        RecipeDTO updatedFetched = testRestTemplate.getForObject(getUrl("/" + 1), RecipeDTO.class);

        assertEquals(SPAGHETTI, updatedFetched);

        testRestTemplate.delete(getUrl("/" + 1));

        ResponseEntity<String> notFound = testRestTemplate.getForEntity(getUrl("/" + 1), String.class);

        Assertions.assertEquals(HttpStatus.NOT_FOUND, notFound.getStatusCode());
    }

    @Test
    void testReadAllRecipes() {
        testRestTemplate.postForEntity(getUrl(""), PANCAKES, RecipeDTO.class);
        testRestTemplate.postForEntity(getUrl(""), SPAGHETTI, RecipeDTO.class);

        ResponseEntity<RecipeDTO[]> response = testRestTemplate.getForEntity(getUrl(""), RecipeDTO[].class);

        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        RecipeDTO[] recipes = response.getBody();
        Assertions.assertNotNull(recipes);
        Assertions.assertEquals(2, recipes.length);
    }

    private void assertEquals(RecipeDTO expected, RecipeDTO actual) {
        if (expected == null) return;
        Assertions.assertNotNull(actual);
        Assertions.assertEquals(expected.title(), actual.title());
        Assertions.assertEquals(expected.instructions(), actual.instructions());
    }
}
