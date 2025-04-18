package edu.chnu.recipes_api.recipes;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RecipeService {
    private final RecipeRepository recipeRepository;

    public RecipeService(RecipeRepository recipeRepository) {
        this.recipeRepository = recipeRepository;
    }

    public Recipe create(Recipe recipe) {
        return recipeRepository.save(recipe);
    }

    public List<Recipe> readAll() {
        return recipeRepository.findAll();
    }

    public Recipe read(Long id) {
        return recipeRepository.findById(id).orElseThrow(() -> new RecipeNotFoundException("Recipe not found"));
    }

    public void update(Long id, Recipe recipe) {
        Recipe ignored = read(id);
        recipe.setId(id);
        recipeRepository.save(recipe);
    }

    public void delete(Long id) {
        Recipe ignored = read(id);
        recipeRepository.deleteById(id);
    }
}
