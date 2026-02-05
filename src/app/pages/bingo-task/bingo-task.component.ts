
import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';

export interface BingoTask {
  id: number;
  name: string;
  points: number;
  status: 'available' | 'completed' | 'bingoLine';
}

@Component({
  selector: 'app-bingo-task',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './bingo-task.component.html',
  styleUrl: './bingo-task.component.scss'
})
export class BingoTaskComponent {

  completedCount = 0;
  totalTasks = 25;

  tasks: BingoTask[] = [
    { id: 1, name: 'Complete 5 exercises', points: 50, status: 'completed' },
    { id: 2, name: 'Read for 30 min', points: 40, status: 'available' },
    { id: 3, name: 'Meditate 10 min', points: 30, status: 'available' },
    { id: 4, name: 'Drink 8 glasses', points: 20, status: 'available' },
    { id: 5, name: 'Call a friend', points: 35, status: 'available' },
    { id: 6, name: 'Cook a meal', points: 45, status: 'available' },
    { id: 7, name: 'Learn new skill', points: 60, status: 'available' },
    { id: 8, name: 'Clean workspace', points: 25, status: 'available' },
    { id: 9, name: 'Walk 10k steps', points: 50, status: 'available' },
    { id: 10, name: 'Journal 15 min', points: 25, status: 'available' },
    { id: 11, name: 'No social media', points: 30, status: 'available' },
    { id: 12, name: 'Practice gratitude', points: 35, status: 'available' },
    { id: 13, name: 'Stretch routine', points: 30, status: 'available' },
    { id: 14, name: 'Healthy breakfast', points: 20, status: 'available' },
    { id: 15, name: 'Help someone', points: 50, status: 'available' },
    { id: 16, name: 'Review goals', points: 35, status: 'available' },
    { id: 17, name: 'Organize files', points: 30, status: 'available' },
    { id: 18, name: 'Take breaks', points: 20, status: 'available' },
    { id: 19, name: 'Practice hobby', points: 45, status: 'available' },
    { id: 20, name: 'Early to bed', points: 40, status: 'available' },
    { id: 21, name: 'No caffeine PM', points: 30, status: 'available' },
    { id: 22, name: 'Compliment 3 people', points: 25, status: 'available' },
    { id: 23, name: 'Plan tomorrow', points: 25, status: 'available' },
    { id: 24, name: 'Deep work 2hrs', points: 60, status: 'available' },
    { id: 25, name: 'Family time', points: 45, status: 'available' }
  ];

  constructor() {
    this.completedCount = this.tasks.filter(t => t.status === 'completed').length;
  }
}
