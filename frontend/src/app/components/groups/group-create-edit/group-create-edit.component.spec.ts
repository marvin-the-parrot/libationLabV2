import { ComponentFixture, TestBed } from '@angular/core/testing';

import { GroupCreateEditComponent } from './group-create-edit.component';

describe('GroupCreateEditComponent', () => {
  let component: GroupCreateEditComponent;
  let fixture: ComponentFixture<GroupCreateEditComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [GroupCreateEditComponent]
    });
    fixture = TestBed.createComponent(GroupCreateEditComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
