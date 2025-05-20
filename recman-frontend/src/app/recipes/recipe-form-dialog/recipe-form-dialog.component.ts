import {Component, Inject, OnInit} from '@angular/core';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {MAT_DIALOG_DATA, MatDialogRef} from '@angular/material/dialog';

export interface RecipeFormData {
  recipe?: { id: number; name: string; description: string };
}

@Component({
  selector: 'app-recipe-form-dialog',
  standalone: false,
  templateUrl: './recipe-form-dialog.component.html',
  styleUrl: './recipe-form-dialog.component.css'
})
export class RecipeFormDialogComponent implements OnInit {
  recipeForm!: FormGroup;

  constructor(
    private fb: FormBuilder,
    private dialogRef: MatDialogRef<RecipeFormDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: RecipeFormData
  ) {}

  ngOnInit(): void {
    this.recipeForm = this.fb.group({
      name: [this.data.recipe?.name || '', [Validators.required, Validators.minLength(3), Validators.maxLength(50)]],
      description: [this.data.recipe?.description || '', Validators.required]
    });
  }

  onSubmit(): void {
    if (this.recipeForm.valid) {
      this.dialogRef.close(this.recipeForm.value);
    }
  }

  onCancel(): void {
    this.dialogRef.close();
  }
}
