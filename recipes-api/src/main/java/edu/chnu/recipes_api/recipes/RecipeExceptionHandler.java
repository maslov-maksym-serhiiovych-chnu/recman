package edu.chnu.recipes_api.recipes;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class RecipeExceptionHandler {
    @ExceptionHandler(RecipeNotFoundException.class)
    public ResponseEntity<Void> handleRecipeNotFoundException() {
        return ResponseEntity.notFound().build();
    }
}
