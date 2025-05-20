import { ComponentFixture, TestBed } from '@angular/core/testing';

import { RecipeViewDialogComponent } from './recipe-view-dialog.component';

describe('RecipeViewDialogComponent', () => {
  let component: RecipeViewDialogComponent;
  let fixture: ComponentFixture<RecipeViewDialogComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [RecipeViewDialogComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(RecipeViewDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
