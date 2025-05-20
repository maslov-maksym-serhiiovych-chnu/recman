import {RecipesComponent} from './recipes.component';
import {RecipesRoutingModule} from './recipes-routing.module';
import {CommonModule} from '@angular/common';
import {NgModule} from '@angular/core';
import {RecipeFormDialogComponent} from './recipe-form-dialog/recipe-form-dialog.component';
import {RecipeViewDialogComponent} from './recipe-view-dialog/recipe-view-dialog.component';
import {MatButton} from '@angular/material/button';
import {ReactiveFormsModule} from '@angular/forms';
import {MatDialogContent, MatDialogTitle} from '@angular/material/dialog';

@NgModule({
  declarations: [RecipesComponent, RecipeFormDialogComponent, RecipeViewDialogComponent],
  imports: [RecipesRoutingModule, CommonModule, MatButton, ReactiveFormsModule, MatDialogContent, MatDialogTitle]
})
export class RecipesModule {
}
