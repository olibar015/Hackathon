import { ComponentFixture, TestBed } from '@angular/core/testing';

import { RedemptionStoreComponent } from './redemption-store.component';

describe('RedemptionStoreComponent', () => {
  let component: RedemptionStoreComponent;
  let fixture: ComponentFixture<RedemptionStoreComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [RedemptionStoreComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(RedemptionStoreComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
