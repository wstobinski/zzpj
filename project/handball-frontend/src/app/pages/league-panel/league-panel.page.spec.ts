import { ComponentFixture, TestBed } from '@angular/core/testing';
import { LeaguePanelPage } from './league-panel.page';

describe('LeaguePanelPage', () => {
  let component: LeaguePanelPage;
  let fixture: ComponentFixture<LeaguePanelPage>;

  beforeEach(() => {
    fixture = TestBed.createComponent(LeaguePanelPage);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
