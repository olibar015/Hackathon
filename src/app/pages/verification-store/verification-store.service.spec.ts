import { TestBed } from '@angular/core/testing';

import { VerificationStoreService } from './verification-store.service';

describe('VerificationStoreService', () => {
  let service: VerificationStoreService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(VerificationStoreService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
