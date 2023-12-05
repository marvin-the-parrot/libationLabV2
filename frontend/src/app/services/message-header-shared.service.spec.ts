import { TestBed } from '@angular/core/testing';

import { MessageHeaderSharedService } from './message-header-shared.service';

describe('MessageHeaderSharedService', () => {
  let service: MessageHeaderSharedService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(MessageHeaderSharedService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
