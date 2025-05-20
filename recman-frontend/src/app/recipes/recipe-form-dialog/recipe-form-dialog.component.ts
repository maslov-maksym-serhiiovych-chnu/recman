import {Component, Inject, OnInit} from '@angular/core';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {MAT_DIALOG_DATA, MatDialogRef} from '@angular/material/dialog';
import {RecipesService} from '../recipes.service';
import {Observable} from 'rxjs';

export interface RecipeFormData {
  id?: number;
  name?: string;
  description?: string;
}

@Component({
  selector: 'app-recipe-form-dialog',
  standalone: false,
  templateUrl: './recipe-form-dialog.component.html',
  styleUrl: './recipe-form-dialog.component.css'
})
export class RecipeFormDialogComponent implements OnInit {
  recipeForm!: FormGroup;
  errorMessage: string | null = null;

  constructor(
    private fb: FormBuilder,
    private recipesService: RecipesService,
    private dialogRef: MatDialogRef<RecipeFormDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: RecipeFormData
  ) {
  }

  ngOnInit(): void {
    this.recipeForm = this.fb.group({
      name: [this.data.name || '', [Validators.required, Validators.minLength(3),
        Validators.maxLength(50)]],
      description: [this.data.description || '', Validators.required]
    });
  }

  onSubmit(): void {
    if (this.recipeForm.invalid) return;

    const formData = this.recipeForm.value;

    let request$: Observable<unknown>;
    request$ = this.data.id != null ? this.recipesService.update(this.data.id, formData) :
      this.recipesService.create(formData);

    request$.subscribe({
      next: () => this.dialogRef.close(true),
      error: (err: any) => this.handleError(err)
    });
  }

  onCancel(): void {
    this.dialogRef.close();
  }

  private handleError(err: any): void {
    if (err.status === 409) {
      this.errorMessage = 'A recipe with this name already exists';
    } else {
      this.errorMessage = err.error?.message || 'Something went wrong';
    }
  }
}
