package edu.chnu.recman_backend.recipes.repositories;

import edu.chnu.recman_backend.recipes.models.Recipe;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RecipeRepository extends JpaRepository<Recipe, Long> {
}
