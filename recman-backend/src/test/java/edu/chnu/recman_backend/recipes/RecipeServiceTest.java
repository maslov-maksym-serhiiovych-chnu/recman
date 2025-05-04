package edu.chnu.recman_backend.recipes;

import edu.chnu.recman_backend.recipes.dtos.RecipeCreateRequest;
import edu.chnu.recman_backend.recipes.dtos.RecipeDetails;
import edu.chnu.recman_backend.recipes.dtos.RecipeUpdateRequest;
import edu.chnu.recman_backend.recipes.exceptions.RecipeAlreadyExistsException;
import edu.chnu.recman_backend.recipes.exceptions.RecipeNotFoundException;
import edu.chnu.recman_backend.recipes.models.Recipe;
import edu.chnu.recman_backend.recipes.repositories.RecipeRepository;
import edu.chnu.recman_backend.recipes.services.RecipeService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;
import java.util.Optional;

class RecipeServiceTest {
    private RecipeRepository repository;
    private RecipeService service;

    @BeforeEach
    void setUp() {
        repository = Mockito.mock(RecipeRepository.class);
        service = new RecipeService(repository);
    }

    @Test
    void create_ShouldCreateRecipe_WhenNameIsUnique() {
        RecipeCreateRequest request = new RecipeCreateRequest("Borscht", "Traditional beet soup");

        Mockito.when(repository.existsByName("Borscht")).thenReturn(false);
        Mockito.when(repository.save(Mockito.any(Recipe.class))).thenAnswer(inv -> inv.getArgument(0));

        RecipeDetails result = service.create(request);

        Assertions.assertEquals("Borscht", result.name());
        Assertions.assertEquals("Traditional beet soup", result.description());
        Mockito.verify(repository).save(Mockito.any(Recipe.class));
    }

    @Test
    void create_ShouldThrow_WhenNameAlreadyExists() {
        RecipeCreateRequest request = new RecipeCreateRequest("Borscht", "Soup");

        Mockito.when(repository.existsByName("Borscht")).thenReturn(true);

        Assertions.assertThrows(RecipeAlreadyExistsException.class, () -> service.create(request));
    }

    @Test
    void read_ShouldReturnRecipeDetails_WhenFound() {
        Recipe recipe = new Recipe("Borscht", "Soup");

        Mockito.when(repository.findById(1L)).thenReturn(Optional.of(recipe));

        RecipeDetails result = service.read(1L);

        Assertions.assertEquals("Borscht", result.name());
        Assertions.assertEquals("Soup", result.description());
    }

    @Test
    void read_ShouldThrow_WhenNotFound() {
        Mockito.when(repository.findById(1L)).thenReturn(Optional.empty());

        Assertions.assertThrows(RecipeNotFoundException.class, () -> service.read(1L));
    }

    @Test
    void update_ShouldUpdateNameAndDescription_WhenValid() {
        Recipe recipe = new Recipe("Old", "Old desc");

        RecipeUpdateRequest request = new RecipeUpdateRequest("New", "New desc");

        Mockito.when(repository.findById(1L)).thenReturn(Optional.of(recipe));
        Mockito.when(repository.existsByName("New")).thenReturn(false);
        Mockito.when(repository.save(Mockito.any(Recipe.class))).thenAnswer(inv -> inv.getArgument(0));

        service.update(1L, request);

        Assertions.assertEquals("New", recipe.getName());
        Assertions.assertEquals("New desc", recipe.getDescription());
        Mockito.verify(repository).save(recipe);
    }

    @Test
    void update_ShouldThrow_WhenNewNameAlreadyExists() {
        Recipe recipe = new Recipe("Old", "Desc");

        RecipeUpdateRequest request = new RecipeUpdateRequest("New", null);

        Mockito.when(repository.findById(1L)).thenReturn(Optional.of(recipe));
        Mockito.when(repository.existsByName("New")).thenReturn(true);

        Assertions.assertThrows(RecipeAlreadyExistsException.class, () -> service.update(1L, request));
    }

    @Test
    void update_ShouldThrow_WhenNotFound() {
        RecipeUpdateRequest request = new RecipeUpdateRequest("New", "Desc");

        Mockito.when(repository.findById(1L)).thenReturn(Optional.empty());

        Assertions.assertThrows(RecipeNotFoundException.class, () -> service.update(1L, request));
    }

    @Test
    void delete_ShouldRemoveRecipe_WhenFound() {
        Recipe recipe = new Recipe("ToDelete", "Desc");

        Mockito.when(repository.findById(1L)).thenReturn(Optional.of(recipe));

        service.delete(1L);

        Mockito.verify(repository).delete(recipe);
    }

    @Test
    void delete_ShouldThrow_WhenNotFound() {
        Mockito.when(repository.findById(1L)).thenReturn(Optional.empty());

        Assertions.assertThrows(RecipeNotFoundException.class, () -> service.delete(1L));
    }

    @Test
    void readAll_ShouldReturnListOfRecipeListItems() {
        Recipe firstRecipe = new Recipe("Borscht", "Desc"),
                secondRecipe = new Recipe("Varenyky", "Desc");

        Mockito.when(repository.findAll()).thenReturn(List.of(firstRecipe, secondRecipe));

        var result = service.readAll();

        Assertions.assertEquals(2, result.size());
        Assertions.assertEquals("Borscht", result.get(0).name());
        Assertions.assertEquals("Varenyky", result.get(1).name());
    }
}
