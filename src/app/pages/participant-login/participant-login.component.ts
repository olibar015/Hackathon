import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { RouterModule, Router } from '@angular/router';

@Component({
  selector: 'app-participant-login',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './participant-login.component.html',
  styleUrl: './participant-login.component.scss'
})
export class ParticipantLoginComponent {
  constructor(private router: Router) { }

  loginAsEmployee() {
    localStorage.setItem('role', 'employee');
    localStorage.setItem('displayName', 'Employee'); // palitan mo from input field if meron
    this.router.navigateByUrl('/participant-board');
  }
}
