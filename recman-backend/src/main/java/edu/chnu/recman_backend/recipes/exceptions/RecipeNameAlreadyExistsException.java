package edu.chnu.recman_backend.recipes.exceptions;

public class RecipeNameAlreadyExistsException extends RuntimeException {
    public RecipeNameAlreadyExistsException() {
        super("Recipe name already exists");
    }
}
