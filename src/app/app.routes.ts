import { Routes } from '@angular/router';
import { LandingComponent } from './pages/landing/landing.component';
import { AdminLoginComponent } from './pages/admin-login/admin-login.component';
import { ParticipantLoginComponent } from './pages/participant-login/participant-login.component';
import { RegistrationComponent } from './pages/registration/registration.component';
import { ParticipantBoardComponent } from './pages/participant-board/participant-board.component';
import { AdminDashboardComponent } from './pages/admin-dashboard/admin-dashboard.component';
import { SidebarComponent } from './pages/sidebar/sidebar.component';
import { TaskManagementComponent } from './pages/task-management/task-management.component';
import { LeaderboardsComponent } from './pages/leaderboards/leaderboards.component';
import { BingoTaskComponent } from './pages/bingo-task/bingo-task.component';


export const routes: Routes = [
  { path: '', redirectTo: 'landing', pathMatch: 'full' },

  { path: 'landing', component: LandingComponent },
  { path: 'participant-login', component: ParticipantLoginComponent },
  { path: 'admin-login', component: AdminLoginComponent },
  { path: 'registration', component: RegistrationComponent },
  { path: 'participant-board', component: ParticipantBoardComponent },
  { path: 'admin-board', component: AdminDashboardComponent },
  { path: 'sidebar', component: SidebarComponent },
  { path: 'task-management', component: TaskManagementComponent },
  { path: 'bingo-task', component: BingoTaskComponent },
  { path: 'leaderboards', component: LeaderboardsComponent },


  { path: '**', redirectTo: 'landing' }
];

