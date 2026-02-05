import { Component } from '@angular/core';

interface Achievement {
  title: string;
  description: string;
  completed: boolean;
}

@Component({
  selector: 'app-admin-dashboard',
  templateUrl: './admin-dashboard.component.html',
  styleUrls: ['./admin-dashboard.component.scss']
})
export class AdminDashboardComponent {
  user = {
    name: 'You',
    level: 9,
    totalPoints: 2130,
    streak: 7,
    bestStreak: 12,
    xp: 700,
    xpMax: 900
  };

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
    return (this.user.xp / this.user.xpMax) * 100;
  }
}
