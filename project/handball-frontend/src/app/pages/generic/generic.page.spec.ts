import { ComponentFixture, TestBed } from '@angular/core/testing';
import { GenericPage } from './generic.page';

describe('GenericPage', () => {
  let component: GenericPage;
  let fixture: ComponentFixture<GenericPage>;

  beforeEach(() => {
    fixture = TestBed.createComponent(GenericPage);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
