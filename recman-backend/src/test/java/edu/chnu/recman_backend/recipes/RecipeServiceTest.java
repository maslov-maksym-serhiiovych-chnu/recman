package edu.chnu.recman_backend.recipes;

import edu.chnu.recman_backend.auth.models.Role;
import edu.chnu.recman_backend.auth.models.User;
import edu.chnu.recman_backend.auth.services.AuthService;
import edu.chnu.recman_backend.recipes.dtos.RecipeCreateRequest;
import edu.chnu.recman_backend.recipes.dtos.RecipeDetails;
import edu.chnu.recman_backend.recipes.dtos.RecipeUpdateRequest;
import edu.chnu.recman_backend.recipes.exceptions.RecipeNameAlreadyExistsException;
import edu.chnu.recman_backend.recipes.exceptions.RecipeNotFoundException;
import edu.chnu.recman_backend.recipes.models.Recipe;
import edu.chnu.recman_backend.recipes.repositories.RecipeRepository;
import edu.chnu.recman_backend.recipes.services.RecipeService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;

class RecipeServiceTest {
    private static final User USER = new User("user", "password", Role.USER);

    private static final List<Recipe> RECIPES = List.of(
            new Recipe("Borscht", "Soup", USER),
            new Recipe("Varenyky", "Cheese", USER)
    );

    private final Recipe RECIPE = new Recipe("Borscht", "Soup", USER);
    
    private static final RecipeCreateRequest RECIPE_CREATE_REQUEST =
            new RecipeCreateRequest("Borscht", "Soup");

    private static final RecipeUpdateRequest RECIPE_UPDATE_REQUEST =
            new RecipeUpdateRequest("New", "New");
    
    private static final RecipeNameAlreadyExistsException RECIPE_NAME_ALREADY_EXISTS_EXCEPTION =
            new RecipeNameAlreadyExistsException();

    private static final RecipeNotFoundException RECIPE_NOT_FOUND_EXCEPTION = new RecipeNotFoundException();
    
    private static final Long RECIPE_ID = 1L;

    @Mock
    private RecipeRepository repository;
    
    @Mock
    private AuthService authService;

    @InjectMocks
    private RecipeService service;

    @BeforeEach
    void setUp() throws Exception {
        MockitoAnnotations.openMocks(this).close();
        
        Mockito.when(authService.getCurrentUser()).thenReturn(USER);
    }

    @Test
    void create_ShouldCreateRecipe_WhenNameIsUnique() {
        Mockito.when(repository.existsByNameAndUser(RECIPE_CREATE_REQUEST.name(), USER)).thenReturn(false);
        Mockito.when(repository.save(Mockito.any(Recipe.class))).thenAnswer(inv ->
                inv.getArgument(0));

        RecipeDetails result = service.create(RECIPE_CREATE_REQUEST);

        Assertions.assertEquals(RECIPE_CREATE_REQUEST.name(), result.name());
        Assertions.assertEquals(RECIPE_CREATE_REQUEST.description(), result.description());
        Mockito.verify(repository).save(Mockito.any(Recipe.class));
    }

    @Test
    void create_ShouldThrow_WhenNameAlreadyExists() {
        Mockito.when(repository.existsByNameAndUser(RECIPE_CREATE_REQUEST.name(), USER)).thenReturn(true);

        RecipeNameAlreadyExistsException ex = Assertions.assertThrows(RecipeNameAlreadyExistsException.class, () ->
                service.create(RECIPE_CREATE_REQUEST));

        Assertions.assertEquals(RECIPE_NAME_ALREADY_EXISTS_EXCEPTION.getMessage(), ex.getMessage());
    }

    @Test
    void read_ShouldReturnRecipeDetails_WhenFound() {
        Mockito.when(repository.findByIdAndUser(RECIPE_ID, USER)).thenReturn(Optional.of(RECIPE));

        RecipeDetails result = service.read(RECIPE_ID);

        Assertions.assertEquals(RECIPE.getName(), result.name());
        Assertions.assertEquals(RECIPE.getDescription(), result.description());
    }

    @Test
    void read_ShouldThrow_WhenNotFound() {
        Mockito.when(repository.findByIdAndUser(RECIPE_ID, USER)).thenReturn(Optional.empty());

        RecipeNotFoundException ex = Assertions.assertThrows(RecipeNotFoundException.class, () ->
                service.read(RECIPE_ID));

        Assertions.assertEquals(RECIPE_NOT_FOUND_EXCEPTION.getMessage(), ex.getMessage());
    }

    @Test
    void update_ShouldUpdateNameAndDescription_WhenValid() {
        Mockito.when(repository.findByIdAndUser(RECIPE_ID, USER)).thenReturn(Optional.of(RECIPE));
        Mockito.when(repository.existsByNameAndUser(RECIPE_UPDATE_REQUEST.name(), USER)).thenReturn(false);
        Mockito.when(repository.save(Mockito.any(Recipe.class))).thenAnswer(inv ->
                inv.getArgument(0));

        service.update(RECIPE_ID, RECIPE_UPDATE_REQUEST);

        Assertions.assertEquals(RECIPE_UPDATE_REQUEST.name(), RECIPE.getName());
        Assertions.assertEquals(RECIPE_UPDATE_REQUEST.description(), RECIPE.getDescription());
        Mockito.verify(repository).save(RECIPE);
    }

    @Test
    void update_ShouldThrow_WhenNewNameAlreadyExists() {
        Mockito.when(repository.findByIdAndUser(RECIPE_ID, USER)).thenReturn(Optional.of(RECIPE));
        Mockito.when(repository.existsByNameAndUser(RECIPE_UPDATE_REQUEST.name(), USER)).thenReturn(true);

        RecipeNameAlreadyExistsException ex = Assertions.assertThrows(RecipeNameAlreadyExistsException.class, () ->
                service.update(RECIPE_ID, RECIPE_UPDATE_REQUEST));

        Assertions.assertEquals(RECIPE_NAME_ALREADY_EXISTS_EXCEPTION.getMessage(), ex.getMessage());
    }

    @Test
    void update_ShouldThrow_WhenNotFound() {
        Mockito.when(repository.findByIdAndUser(RECIPE_ID, USER)).thenReturn(Optional.empty());

        RecipeNotFoundException ex = Assertions.assertThrows(RecipeNotFoundException.class, () ->
                service.update(RECIPE_ID, RECIPE_UPDATE_REQUEST));

        Assertions.assertEquals(RECIPE_NOT_FOUND_EXCEPTION.getMessage(), ex.getMessage());
    }

    @Test
    void delete_ShouldRemoveRecipe_WhenFound() {
        Mockito.when(repository.findByIdAndUser(RECIPE_ID, USER)).thenReturn(Optional.of(RECIPE));

        service.delete(RECIPE_ID);

        Mockito.verify(repository).delete(RECIPE);
    }

    @Test
    void delete_ShouldThrow_WhenNotFound() {
        Mockito.when(repository.findByIdAndUser(RECIPE_ID, USER)).thenReturn(Optional.empty());

        RecipeNotFoundException ex = Assertions.assertThrows(RecipeNotFoundException.class, () ->
                service.delete(RECIPE_ID));

        Assertions.assertEquals(RECIPE_NOT_FOUND_EXCEPTION.getMessage(), ex.getMessage());
    }

    @Test
    void readAll_ShouldReturnListOfRecipeListItems() {
        Mockito.when(repository.findAllByUser(USER)).thenReturn(RECIPES);

        var result = service.readAll();

        Assertions.assertEquals(RECIPES.size(), result.size());
        Assertions.assertEquals(RECIPES.getFirst().getName(), result.getFirst().name());
        Assertions.assertEquals(RECIPES.getLast().getName(), result.getLast().name());
    }
}
