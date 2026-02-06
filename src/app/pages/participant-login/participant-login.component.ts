import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { RouterModule, Router } from '@angular/router';
import { FormsModule } from '@angular/forms';
import Swal from 'sweetalert2';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-participant-login',
  standalone: true,
  imports: [CommonModule, RouterModule, FormsModule],
  templateUrl: './participant-login.component.html',
  styleUrl: './participant-login.component.scss'
})
export class ParticipantLoginComponent {
  email = '';
  password = '';
  loading = false;

  constructor(private authService: AuthService, private router: Router) { }

  login() {
    const payload = { email: this.email, password: this.password };
    console.log('Submitting login payload:', payload);

    this.loading = true;

    this.authService.login(payload).subscribe({
      next: (res: any) => {
        console.log('Login success:', res);

        // ✅ adjust these depending on what your backend returns
        // Example possibilities: res.token, res.accessToken, res.user, res.username, res.role
        const token = res?.token ?? res?.accessToken ?? null;
        const role = res?.role ?? res?.user?.role ?? 'USER';
        const displayName = res?.username ?? res?.user?.username ?? 'Employee';

        if (token) localStorage.setItem('token', token);
        localStorage.setItem('role', role); // you can store 'USER'
        localStorage.setItem('displayName', displayName);

        Swal.fire({ icon: 'success', title: 'Welcome!', timer: 1200, showConfirmButton: false });

        // ✅ go to your board
        this.router.navigateByUrl('/participant-board');
      },
      error: (err: any) => {
        console.error('Login error:', err);
        Swal.fire({
          icon: 'error',
          title: 'Login failed',
          text: err?.error?.message ?? 'Invalid email or password.'
        });
        this.loading = false;
      },
      complete: () => {
        this.loading = false;
      }
    });
  }
}
