import { ComponentFixture, TestBed } from '@angular/core/testing';
import { LeaguesPage } from './leagues.page';

describe('LeaguesPage', () => {
  let component: LeaguesPage;
  let fixture: ComponentFixture<LeaguesPage>;

  beforeEach(() => {
    fixture = TestBed.createComponent(LeaguesPage);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
