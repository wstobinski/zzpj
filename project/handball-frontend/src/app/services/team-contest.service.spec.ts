import { TestBed } from '@angular/core/testing';

import { TeamContestService } from './team-contest.service';

describe('TeamContestService', () => {
  let service: TeamContestService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(TeamContestService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
