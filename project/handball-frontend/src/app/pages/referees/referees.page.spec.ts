import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RefereesPage } from './referees.page';

describe('RefereesPage', () => {
  let component: RefereesPage;
  let fixture: ComponentFixture<RefereesPage>;

  beforeEach(() => {
    fixture = TestBed.createComponent(RefereesPage);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
