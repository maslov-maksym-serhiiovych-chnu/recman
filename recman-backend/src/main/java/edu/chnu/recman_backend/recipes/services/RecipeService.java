package edu.chnu.recman_backend.recipes.services;

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

    public RecipeService(RecipeRepository repository) {
        this.repository = repository;
    }

    public RecipeDetails create(RecipeCreateRequest request) {
        validateName(request.name());

        Recipe recipe = new Recipe(request.name(), request.description());

        repository.save(recipe);
        return new RecipeDetails(recipe.getName(), recipe.getDescription());
    }

    public List<RecipeListItem> readAll() {
        return repository.findAll()
                .stream()
                .map(r -> new RecipeListItem(r.getName()))
                .toList();
    }

    public RecipeDetails read(Long id) {
        return repository.findById(id)
                .map(r -> new RecipeDetails(r.getName(), r.getDescription()))
                .orElseThrow(RecipeNotFoundException::new);
    }

    public void update(Long id, RecipeUpdateRequest request) {
        Recipe recipe = repository.findById(id).orElseThrow(RecipeNotFoundException::new);

        if (request.name() != null && !request.name().equals(recipe.getName())) {
            validateName(request.name());

            recipe.setName(request.name());
        }

        Optional.ofNullable(request.description()).ifPresent(recipe::setDescription);

        repository.save(recipe);
    }

    public void delete(Long id) {
        Recipe recipe = repository.findById(id).orElseThrow(RecipeNotFoundException::new);

        repository.delete(recipe);
    }

    private void validateName(String name) {
        if (repository.existsByName(name)) {
            throw new RecipeNameAlreadyExistsException();
        }
    }
}
