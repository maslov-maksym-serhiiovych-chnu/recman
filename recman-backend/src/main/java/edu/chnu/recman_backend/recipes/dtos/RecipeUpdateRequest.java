package edu.chnu.recman_backend.recipes.dtos;

import jakarta.validation.constraints.Size;

public record RecipeUpdateRequest(
        @Size(min = 3, max = 50, message = "Name must be 3-50 characters")
        String name,

        String description) {
}
