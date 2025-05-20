import {Component, Inject} from '@angular/core';
import {MAT_DIALOG_DATA, MatDialogRef} from '@angular/material/dialog';
import {RecipeResponse} from '../recipes.service';

@Component({
  selector: 'app-recipe-view-dialog',
  standalone: false,
  templateUrl: './recipe-view-dialog.component.html',
  styleUrl: './recipe-view-dialog.component.css'
})
export class RecipeViewDialogComponent {
  constructor(
    @Inject(MAT_DIALOG_DATA) public data: RecipeResponse,
    private dialogRef: MatDialogRef<RecipeViewDialogComponent>
  ) {}

  close() {
    this.dialogRef.close();
  }
}
