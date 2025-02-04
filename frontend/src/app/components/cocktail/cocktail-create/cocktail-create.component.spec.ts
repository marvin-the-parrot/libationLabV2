import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CocktailCreateComponent } from './cocktail-create.component';

describe('CocktailCreateComponent', () => {
  let component: CocktailCreateComponent;
  let fixture: ComponentFixture<CocktailCreateComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [CocktailCreateComponent]
    });
    fixture = TestBed.createComponent(CocktailCreateComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
