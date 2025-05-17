import {Component} from '@angular/core';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {HttpClient} from '@angular/common/http';

interface RecipeListItem {
  name: string;
}

interface RecipeDetails {
  name: string;
  description: string;
}

@Component({
  selector: 'app-recipes',
  standalone: false,
  templateUrl: './recipes.component.html',
  styleUrl: './recipes.component.css'
})
export class RecipesComponent {
  recipes: RecipeListItem[] = [];
  selectedRecipe: RecipeDetails | null = null;
  recipeForm: FormGroup;
  showForm = false;
  editingId: number | null = null;

  constructor(private http: HttpClient, private fb: FormBuilder) {
    this.recipeForm = this.fb.group({
      name: ['', [Validators.required, Validators.minLength(3), Validators.maxLength(50)]],
      description: ['', Validators.required],
    });
  }

  ngOnInit() {
    this.fetchRecipes();
  }

  fetchRecipes() {
    this.http.get<RecipeListItem[]>('/api/recman/recipes').subscribe((data) => {
      this.recipes = data;
    });
  }

  selectRecipe(recipe: RecipeListItem) {
    this.http.get<RecipeDetails>(`/api/recman/recipes/${recipe.name}`).subscribe((data) => {
      this.selectedRecipe = data;
    });
  }

  onSubmit() {
    if (this.recipeForm.invalid) return;

    const value = this.recipeForm.value;

    if (this.editingId) {
      this.http.put(`/api/recman/recipes/${this.editingId}`, value).subscribe(() => {
        this.fetchRecipes();
        this.cancel();
      });
    } else {
      this.http.post<RecipeDetails>('/api/recman/recipes', value).subscribe(() => {
        this.fetchRecipes();
        this.cancel();
      });
    }
  }

  editRecipe(recipe: RecipeListItem) {
    this.http.get<RecipeDetails>(`/api/recman/recipes/${recipe.name}`).subscribe((data) => {
      this.recipeForm.setValue({name: data.name, description: data.description});
      this.editingId = (recipe as any).id || null;
      this.showForm = true;
    });
  }

  deleteRecipe(recipe: RecipeListItem) {
    const id = (recipe as any).id;
    this.http.delete(`/api/recman/recipes/${id}`).subscribe(() => {
      this.fetchRecipes();
      if (this.selectedRecipe?.name === recipe.name) {
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
