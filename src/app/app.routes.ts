import { Routes } from '@angular/router';
import { LoginComponent } from './pages/login/login.component';
import { ParticipantBoardComponent } from './pages/participant-board/participant-board.component';
import { AdminDashboardComponent } from './pages/admin-dashboard/admin-dashboard.component';

export const routes: Routes = [
  { path: '', redirectTo: 'login', pathMatch: 'full' },

  { path: 'login', component: LoginComponent },
  { path: 'board', component: ParticipantBoardComponent },
  { path: 'admin', component: AdminDashboardComponent },

  { path: '**', redirectTo: 'login' }
];
