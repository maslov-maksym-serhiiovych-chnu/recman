import {Component} from '@angular/core';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {RecipeResponse, RecipesService} from './recipes.service';

@Component({
  selector: 'app-recipes',
  standalone: false,
  templateUrl: './recipes.component.html',
  styleUrl: './recipes.component.css'
})
export class RecipesComponent {
  recipes: RecipeResponse[] = [];
  selectedRecipe: RecipeResponse | null = null;
  recipeForm: FormGroup;
  showForm = false;
  editingId: number | null = null;

  constructor(private recipesService: RecipesService, private fb: FormBuilder) {
    this.recipeForm = this.fb.group({
      name: ['', [Validators.required, Validators.minLength(3), Validators.maxLength(50)]],
      description: ['', Validators.required],
    });
  }

  ngOnInit() {
    this.fetchRecipes();
  }

  fetchRecipes() {
    this.recipesService.readAll().subscribe((data) => {
      this.recipes = data;
    });
  }

  selectRecipe(recipe: RecipeResponse) {
    this.recipesService.read(recipe.id).subscribe((data) => {
      this.selectedRecipe = data;
    });
  }

  onSubmit() {
    if (this.recipeForm.invalid) return;

    const value = this.recipeForm.value;

    if (this.editingId !== null) {
      this.recipesService.update(this.editingId, value).subscribe(() => {
        this.fetchRecipes();
        this.cancel();
      });
    } else {
      this.recipesService.create(value).subscribe(() => {
        this.fetchRecipes();
        this.cancel();
      });
    }
  }

  editRecipe(recipe: RecipeResponse) {
    this.recipesService.read(recipe.id).subscribe((data) => {
      this.recipeForm.setValue({
        name: data.name,
        description: data.description
      });
      this.editingId = data.id;
      this.showForm = true;
    });
  }

  deleteRecipe(recipe: RecipeResponse) {
    this.recipesService.delete(recipe.id).subscribe(() => {
      this.fetchRecipes();
      if (this.selectedRecipe?.id === recipe.id) {
        this.selectedRecipe = null;
      }
    });
  }

  cancel() {
    this.recipeForm.reset();
    this.editingId = null;
    this.showForm = false;
  }
}
