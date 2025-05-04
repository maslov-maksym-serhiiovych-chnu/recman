package edu.chnu.recman_backend.recipes.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RecipeCreateRequest(
        @NotBlank(message = "Name is required")
        @Size(min = 3, max = 50, message = "Name must be 3-50 characters")
        String name,

        @NotBlank(message = "Description is required")
        String description) {
}
