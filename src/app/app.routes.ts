import { Routes } from '@angular/router';
import { LandingComponent } from './pages/landing/landing.component';
import { AdminLoginComponent } from './pages/admin-login/admin-login.component';
import { ParticipantLoginComponent } from './pages/participant-login/participant-login.component';
import { RegistrationComponent } from './pages/registration/registration.component';
import { ParticipantBoardComponent } from './pages/participant-board/participant-board.component';
import { AdminDashboardComponent } from './pages/admin-dashboard/admin-dashboard.component';


export const routes: Routes = [
  { path: '', redirectTo: 'landing', pathMatch: 'full' },

  { path: 'landing', component: LandingComponent },
  { path: 'participant-login', component: ParticipantLoginComponent },
  { path: 'admin-login', component: AdminLoginComponent},
  { path: 'registration', component: RegistrationComponent},
  { path: 'paticipant-board', component: ParticipantBoardComponent },
  { path: 'admin-board', component: AdminDashboardComponent },
  

  { path: '**', redirectTo: 'login' }
];
