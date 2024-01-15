import { ComponentFixture, TestBed } from '@angular/core/testing';

import { GenerateMenuDialogComponent } from './generate-menu-dialog.component';

describe('GenerateMenuDialogComponent', () => {
  let component: GenerateMenuDialogComponent;
  let fixture: ComponentFixture<GenerateMenuDialogComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [GenerateMenuDialogComponent]
    });
    fixture = TestBed.createComponent(GenerateMenuDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
