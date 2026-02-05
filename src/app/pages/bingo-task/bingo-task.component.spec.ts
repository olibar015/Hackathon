import { ComponentFixture, TestBed } from '@angular/core/testing';

import { BingoTaskComponent } from './bingo-task.component';

describe('BingoTaskComponent', () => {
  let component: BingoTaskComponent;
  let fixture: ComponentFixture<BingoTaskComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [BingoTaskComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(BingoTaskComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
