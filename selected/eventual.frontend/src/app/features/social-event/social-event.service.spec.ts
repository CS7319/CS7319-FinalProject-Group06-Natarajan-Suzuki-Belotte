import { TestBed } from '@angular/core/testing';

import { SocialEventService } from './social-event.service';

describe('SocialEventService', () => {
  let service: SocialEventService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(SocialEventService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
