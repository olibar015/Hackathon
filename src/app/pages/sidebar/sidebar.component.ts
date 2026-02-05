import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { Router, RouterModule } from '@angular/router';
import Swal from 'sweetalert2';

@Component({
  selector: 'app-sidebar',
  standalone: true,
  imports: [CommonModule, RouterModule], // ✅ IMPORTANT
  templateUrl: './sidebar.component.html',
  styleUrl: './sidebar.component.scss'
})
export class SidebarComponent implements OnInit {
  role: 'admin' | 'employee' = 'employee';
  displayName = 'User';

  constructor(private router: Router) { }

  ngOnInit(): void {
    const r = localStorage.getItem('role');
    const n = localStorage.getItem('displayName');

    if (r === 'admin' || r === 'employee') this.role = r;
    if (n) this.displayName = n;
  }

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

      customClass: {
        popup: 'swal-poppins',
        title: 'swal-poppins',
        htmlContainer: 'swal-poppins',
        confirmButton: 'swal-poppins',
        cancelButton: 'swal-poppins'
      }
    }).then(res => {
      if (res.isConfirmed) {
        localStorage.removeItem('role');
        localStorage.removeItem('displayName');
        this.router.navigateByUrl('/landing');
      }
    });
  }

}
