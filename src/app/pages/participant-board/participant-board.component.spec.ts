import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ParticipantBoardComponent } from './participant-board.component';

describe('ParticipantBoardComponent', () => {
  let component: ParticipantBoardComponent;
  let fixture: ComponentFixture<ParticipantBoardComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ParticipantBoardComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(ParticipantBoardComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
