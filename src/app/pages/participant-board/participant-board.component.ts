import { Component } from '@angular/core';

interface Achievement {
  title: string;
  description: string;
  completed: boolean;
}

@Component({
  selector: 'app-participant-board',
  templateUrl: './participant-board.component.html',
  styleUrls: ['./participant-board.component.scss']
})
export class ParticipantBoardComponent {

  username = 'You';
  level = 9;

  totalPoints = 1850;
  currentXP = 420;
  maxXP = 900;

  currentStreak = 7;
  bestStreak = 12;

  achievements: Achievement[] = [
    {
      title: 'First BINGO!',
      description: 'Complete your first BINGO line',
      completed: true
    },
    {
      title: 'Week Warrior',
      description: 'Maintain a 7-day streak',
      completed: true
    },
    {
      title: 'Century Club',
      description: 'Earn 100 total points',
      completed: true
    },
    {
      title: 'Full Board',
      description: 'Complete all 25 tasks',
      completed: false
    }
  ];

  get xpProgress(): number {
    return Math.min((this.currentXP / this.maxXP) * 100, 100);
  }
}
