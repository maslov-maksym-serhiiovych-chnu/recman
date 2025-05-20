import { ComponentFixture, TestBed } from '@angular/core/testing';

import { RecipeFormDialogComponent } from './recipe-form-dialog.component';

describe('RecipeFormDialogComponent', () => {
  let component: RecipeFormDialogComponent;
  let fixture: ComponentFixture<RecipeFormDialogComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [RecipeFormDialogComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(RecipeFormDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
