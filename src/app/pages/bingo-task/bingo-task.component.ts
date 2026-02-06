import { CommonModule } from '@angular/common';
import { Component, HostListener } from '@angular/core';
import { SidebarComponent } from '../sidebar/sidebar.component';

export type TaskStatus = 'available' | 'completed' | 'bingoLine';

export interface BingoTask {
  id: number;
  name: string;
  points: number;
  status: TaskStatus;
}

@Component({
  selector: 'app-bingo-task',
  standalone: true,
  imports: [CommonModule, SidebarComponent],
  templateUrl: './bingo-task.component.html',
  styleUrls: ['./bingo-task.component.scss'],
})
export class BingoTaskComponent {
  readonly tasks: BingoTask[] = [
    { id: 1, name: 'Complete Training (Any IT Skill)', points: 50, status: 'completed' },
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
    { id: 25, name: 'Family time', points: 45, status: 'available' },
  ];
  isAdmin = true;

  get completedCount(): number {
    return this.tasks.filter(t => t.status === 'completed' || t.status === 'bingoLine').length;
  }

  get totalTasks(): number {
    return this.tasks.length;
  }

  @HostListener('document:mousemove', ['$event'])
  onMouseMove(e: MouseEvent) {
    const hand = document.querySelector('.hand-cursor') as HTMLElement;
    if (!hand) return;

    hand.style.left = e.clientX + 'px';
    hand.style.top = e.clientY + 'px';
  }

  toggleTask(task: BingoTask) {
    console.log('CLICKED TASK:', task.id, task.name, 'old:', task.status);

    task.status = task.status === 'completed' ? 'available' : 'completed';

    console.log('NEW STATUS:', task.status);
  }

  onCellClick(task: BingoTask) {
    console.log('Clicked:', task.id, task.name, 'isAdmin:', this.isAdmin);

    if (!this.isAdmin) {
      // employee view: show message only
      alert('Read-only: Admin lang pwedeng mag check.');
      return;
    }

    // admin view: allow update
    this.toggleTask(task);
  }
}
