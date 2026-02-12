import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SubmitTaskProofComponent } from './submit-task-proof.component';

describe('SubmitTaskProofComponent', () => {
  let component: SubmitTaskProofComponent;
  let fixture: ComponentFixture<SubmitTaskProofComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SubmitTaskProofComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(SubmitTaskProofComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
