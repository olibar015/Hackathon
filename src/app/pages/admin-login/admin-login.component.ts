import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { RouterModule, Router } from '@angular/router';

@Component({
  selector: 'app-admin-login',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './admin-login.component.html',
  styleUrl: './admin-login.component.scss'
})
export class AdminLoginComponent {
  constructor(private router: Router) { }

  loginAsAdmin() {
    localStorage.setItem('role', 'admin');
    localStorage.setItem('displayName', 'Admin'); // palitan mo from input field if meron
    this.router.navigateByUrl('/admin-board');
  }
}
