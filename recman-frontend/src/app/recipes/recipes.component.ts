import {Component, OnInit} from '@angular/core';
import {RecipeResponse, RecipesService} from './recipes.service';
import {RecipeFormData, RecipeFormDialogComponent} from './recipe-form-dialog/recipe-form-dialog.component';
import {MatDialog} from '@angular/material/dialog';
import {take} from 'rxjs';
import {RecipeViewDialogComponent} from './recipe-view-dialog/recipe-view-dialog.component';

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
    this.recipesService.readAll().pipe(take(1)).subscribe((data) => {
      this.recipes = data;
    });
  }

  openDialog(recipe?: RecipeResponse) {
    const formData: RecipeFormData = recipe ? {id: recipe.id, name: recipe.name, description: recipe.description} : {};

    const dialogRef = this.dialog.open(RecipeFormDialogComponent, {
      width: '500px',
      panelClass: 'dark-dialog',
      data: formData
    });

    dialogRef.afterClosed().pipe(take(1)).subscribe((result) => {
      if (result) {
        this.fetchRecipes();
      }
    });
  }

  openViewDialog(recipe: RecipeResponse) {
    this.dialog.open(RecipeViewDialogComponent, {
      width: '500px',
      panelClass: 'dark-dialog',
      data: recipe
    });
  }

  deleteRecipe(recipe: RecipeResponse) {
    this.recipesService.delete(recipe.id).pipe(take(1)).subscribe(() => {
      this.fetchRecipes();
    });
  }
}
