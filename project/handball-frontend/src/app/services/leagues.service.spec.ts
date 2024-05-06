import { TestBed } from '@angular/core/testing';

import { LeagueService } from './league.service';

describe('LeaguesService', () => {
  let service: LeagueService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(LeagueService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
