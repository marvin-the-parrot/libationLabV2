import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CocktailComponent } from './cocktail.component';

describe('CocktailComponent', () => {
  let component: CocktailComponent;
  let fixture: ComponentFixture<CocktailComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [CocktailComponent]
    });
    fixture = TestBed.createComponent(CocktailComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
