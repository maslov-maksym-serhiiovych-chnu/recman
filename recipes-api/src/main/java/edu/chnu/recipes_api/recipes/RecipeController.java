package edu.chnu.recipes_api.recipes;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v0.1/recipes")
public class RecipeController {
    private final RecipeService recipeService;

    public RecipeController(RecipeService recipeService) {
        this.recipeService = recipeService;
    }

    @PostMapping
    public ResponseEntity<RecipeDTO> create(@RequestBody RecipeDTO recipeDTO) {
        Recipe recipe = toEntity(recipeDTO);
        recipeService.create(recipe);
        return ResponseEntity.status(HttpStatus.CREATED).body(recipeDTO);
    }

    @GetMapping
    public ResponseEntity<List<RecipeDTO>> readAll() {
        var recipes = recipeService.readAll().stream()
                .map(this::toDTO)
                .toList();
        return ResponseEntity.ok(recipes);
    }

    @GetMapping("{id}")
    public ResponseEntity<RecipeDTO> read(@PathVariable Long id) {
        RecipeDTO recipeDTO = toDTO(recipeService.read(id));
        return ResponseEntity.ok(recipeDTO);
    }

    @PutMapping("{id}")
    public ResponseEntity<Void> update(@PathVariable Long id, @RequestBody RecipeDTO recipeDTO) {
        Recipe recipe = toEntity(recipeDTO);
        recipeService.update(id, recipe);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        recipeService.delete(id);
        return ResponseEntity.noContent().build();
    }

    private RecipeDTO toDTO(Recipe recipe) {
        if (recipe == null) return null;
        return new RecipeDTO(recipe.getTitle(), recipe.getInstructions());
    }

    private Recipe toEntity(RecipeDTO recipeDTO) {
        if (recipeDTO == null) return null;
        return new Recipe(recipeDTO.title(), recipeDTO.instructions());
    }
}
