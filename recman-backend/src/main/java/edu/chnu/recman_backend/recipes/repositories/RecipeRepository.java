package edu.chnu.recman_backend.recipes.repositories;

import edu.chnu.recman_backend.auth.models.User;
import edu.chnu.recman_backend.recipes.models.Recipe;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RecipeRepository extends JpaRepository<Recipe, Long> {
    boolean existsByNameAndUser(String name, User user);
    
    List<Recipe> findAllByUser(User user);
    
    Optional<Recipe> findByIdAndUser(Long id, User user);
}
