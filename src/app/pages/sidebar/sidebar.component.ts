import { CommonModule } from '@angular/common';
import { Component, Input } from '@angular/core';
import { RouterModule, Router } from '@angular/router';
import Swal from 'sweetalert2'


@Component({
  selector: 'app-sidebar',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './sidebar.component.html',
  styleUrl: './sidebar.component.scss'
})

export class SidebarComponent {
  constructor(private router: Router) { }
  @Input() role: 'admin' | 'employee' = 'employee';
  @Input() displayName = 'User';

  get roleLabel(): string {
    return this.role === 'admin' ? 'Admin' : 'Employee';
  }

  logout(): void {
    Swal.fire({
      title: 'Logout',
      text: 'Are you sure you want to logout?',
      icon: 'question',
      showCancelButton: true,
      confirmButtonText: 'Yes, logout',
      cancelButtonText: 'Cancel',
      confirmButtonColor: '#0a67b6',
      cancelButtonColor: '#d33'
    }).then((result) => {
      if (result.isConfirmed) {
        // Optional: clear auth data if you have it
        // localStorage.removeItem('token');
        // sessionStorage.clear();

        this.router.navigateByUrl('/landing'); // or '/participant-login'
      }
    });
  }
}

