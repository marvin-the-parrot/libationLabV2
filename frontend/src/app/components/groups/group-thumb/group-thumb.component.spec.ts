import { ComponentFixture, TestBed } from '@angular/core/testing';

import { GroupThumbComponent } from './group-thumb.component';

describe('GroupThumbComponent', () => {
  let component: GroupThumbComponent;
  let fixture: ComponentFixture<GroupThumbComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [GroupThumbComponent]
    });
    fixture = TestBed.createComponent(GroupThumbComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
