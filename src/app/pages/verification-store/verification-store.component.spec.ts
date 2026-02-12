import { ComponentFixture, TestBed } from '@angular/core/testing';

import { VerificationStoreComponent } from './verification-store.component';

describe('VerificationStoreComponent', () => {
  let component: VerificationStoreComponent;
  let fixture: ComponentFixture<VerificationStoreComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [VerificationStoreComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(VerificationStoreComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
