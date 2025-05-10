package edu.chnu.recman_backend.recipes.services;

import edu.chnu.recman_backend.auth.models.User;
import edu.chnu.recman_backend.auth.services.AuthService;
import edu.chnu.recman_backend.recipes.dtos.RecipeCreateRequest;
import edu.chnu.recman_backend.recipes.dtos.RecipeDetails;
import edu.chnu.recman_backend.recipes.dtos.RecipeListItem;
import edu.chnu.recman_backend.recipes.dtos.RecipeUpdateRequest;
import edu.chnu.recman_backend.recipes.exceptions.RecipeNameAlreadyExistsException;
import edu.chnu.recman_backend.recipes.exceptions.RecipeNotFoundException;
import edu.chnu.recman_backend.recipes.models.Recipe;
import edu.chnu.recman_backend.recipes.repositories.RecipeRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class RecipeService {
    private final RecipeRepository repository;
    private final AuthService authService;

    public RecipeService(RecipeRepository repository, AuthService authService) {
        this.repository = repository;
        this.authService = authService;
    }

    public RecipeDetails create(RecipeCreateRequest request) {
        User user = authService.getCurrentUser();

        validateName(request.name(), user);

        Recipe recipe = new Recipe(request.name(), request.description(), user);

        repository.save(recipe);
        return new RecipeDetails(recipe.getName(), recipe.getDescription());
    }

    public List<RecipeListItem> readAll() {
        User user = authService.getCurrentUser();

        return repository.findAllByUser(user)
                .stream()
                .map(r -> new RecipeListItem(r.getName()))
                .toList();
    }

    public RecipeDetails read(Long id) {
        User user = authService.getCurrentUser();

        return repository.findByIdAndUser(id, user)
                .map(r -> new RecipeDetails(r.getName(), r.getDescription()))
                .orElseThrow(RecipeNotFoundException::new);
    }

    public void update(Long id, RecipeUpdateRequest request) {
        User user = authService.getCurrentUser();

        Recipe recipe = repository.findByIdAndUser(id, user).orElseThrow(RecipeNotFoundException::new);

        if (request.name() != null && !request.name().equals(recipe.getName())) {
            validateName(request.name(), user);

            recipe.setName(request.name());
        }

        Optional.ofNullable(request.description()).ifPresent(recipe::setDescription);

        repository.save(recipe);
    }

    public void delete(Long id) {
        User user = authService.getCurrentUser();

        Recipe recipe = repository.findByIdAndUser(id, user).orElseThrow(RecipeNotFoundException::new);

        repository.delete(recipe);
    }

    private void validateName(String name, User user) {
        if (repository.existsByNameAndUser(name, user)) {
            throw new RecipeNameAlreadyExistsException();
        }
    }
}
