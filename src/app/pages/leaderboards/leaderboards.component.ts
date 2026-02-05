import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { SidebarComponent } from '../sidebar/sidebar.component';

@Component({
  selector: 'app-leaderboards',
  standalone: true,
  imports: [CommonModule, SidebarComponent],
  templateUrl: './leaderboards.component.html',
  styleUrl: './leaderboards.component.scss'
})
export class LeaderboardsComponent {
  isSidebarOpen = false;

  leaderboard = [
    {
      rank: 1,
      initials: 'SC',
      name: 'Sarah Chen',
      level: 12,
      streakDays: 15,
      points: 2450,
      trend: 'up',
      trendValue: 2
    },
    {
      rank: 2,
      initials: 'Y',
      name: 'You',
      level: 9,
      streakDays: 7,
      points: 1850,
      trend: 'up',
      trendValue: 1,
      isYou: true
    },
    {
      rank: 3,
      initials: 'MJ',
      name: 'Mike Johnson',
      level: 8,
      streakDays: 12,
      points: 1720,
      trend: 'down',
      trendValue: 1
    },
    {
      rank: 4,
      initials: 'ED',
      name: 'Emily Davis',
      level: 8,
      streakDays: 5,
      points: 1580,
      trend: 'up',
      trendValue: 3
    },
    {
      rank: 5,
      initials: 'AK',
      name: 'Alex Kim',
      level: 7,
      streakDays: 9,
      points: 1420,
      trend: 'down',
      trendValue: 2
    },
    {
      rank: 6,
      initials: 'JL',
      name: 'Jordan Lee',
      level: 7,
      streakDays: 4,
      points: 1350,
      trend: 'neutral'
    },
    {
      rank: 7,
      initials: 'TS',
      name: 'Taylor Swift',
      level: 6,
      streakDays: 8,
      points: 1240,
      trend: 'up',
      trendValue: 1
    },
    {
      rank: 8,
      initials: 'CB',
      name: 'Chris Brown',
      level: 6,
      streakDays: 6,
      points: 1180,
      trend: 'down',
      trendValue: 1
    }
  ];

  toggleSidebar(): void {
    this.isSidebarOpen = !this.isSidebarOpen;
  }

  closeSidebar(): void {
    this.isSidebarOpen = false;
  }

}
