package edu.chnu.recman_backend.recipes.exceptions;

public class RecipeNotFoundException extends RuntimeException {
    public RecipeNotFoundException() {
        super("Recipe not found");
    }
}
