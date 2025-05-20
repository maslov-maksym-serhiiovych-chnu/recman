import {Component, OnInit} from '@angular/core';
import {RecipeResponse, RecipesService} from './recipes.service';
import {MatDialog} from '@angular/material/dialog';
import {RecipeFormData, RecipeFormDialogComponent} from './recipe-form-dialog/recipe-form-dialog.component';

@Component({
  selector: 'app-recipes',
  standalone: false,
  templateUrl: './recipes.component.html',
  styleUrl: './recipes.component.css'
})
export class RecipesComponent implements OnInit {
  recipes: RecipeResponse[] = [];

  constructor(private recipesService: RecipesService, private dialog: MatDialog) {
  }

  ngOnInit() {
    this.fetchRecipes();
  }

  fetchRecipes() {
    this.recipesService.readAll().subscribe((data) => {
      this.recipes = data;
    });
  }

  openCreateDialog() {
    const dialogRef = this.dialog.open(RecipeFormDialogComponent, {
      width: '500px',
      panelClass: 'dark-dialog',
      data: {} as RecipeFormData
    });

    dialogRef.afterClosed().subscribe((result) => {
      if (result) {
        this.recipesService.create(result).subscribe({
          next: () => this.fetchRecipes(),
          error: (err) => {
            if (err.status === 409) {
              alert('A recipe with this name already exists');
            } else {
              alert(err.error.message || 'Something went wrong');
            }
          }
        });
      }
    });
  }

  openEditDialog(recipe: RecipeResponse) {
    const dialogRef = this.dialog.open(RecipeFormDialogComponent, {
      width: '500px',
      panelClass: 'dark-dialog',
      data: {recipe}
    });

    dialogRef.afterClosed().subscribe((result) => {
      if (result) {
        this.recipesService.update(recipe.id, result).subscribe({
          next: () => this.fetchRecipes(),
          error: (err) => {
            if (err.status === 409) {
              alert('A recipe with this name already exists');
            } else {
              alert(err.error.message || 'Something went wrong');
            }
          }
        });
      }
    });
  }

  deleteRecipe(recipe: RecipeResponse) {
    this.recipesService.delete(recipe.id).subscribe(() => {
      this.fetchRecipes();
    });
  }
}
