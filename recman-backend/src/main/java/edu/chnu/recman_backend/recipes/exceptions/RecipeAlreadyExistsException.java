package edu.chnu.recman_backend.recipes.exceptions;

public class RecipeAlreadyExistsException extends RuntimeException {
    public RecipeAlreadyExistsException() {
        super("Recipe already exists");
    }
}
