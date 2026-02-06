import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { RouterModule, Router } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { finalize } from 'rxjs/operators';
import { AuthService } from '../../services/auth.service';
// adjust path if different (ex: ../../modules/auth/auth.service)

@Component({
  selector: 'app-admin-login',
  standalone: true,
  imports: [CommonModule, RouterModule, FormsModule],
  templateUrl: './admin-login.component.html',
  styleUrl: './admin-login.component.scss'
})
export class AdminLoginComponent {
  email = '';
  password = '';
  loading = false;
  errorMsg = '';

  constructor(private router: Router, private auth: AuthService) { }

  loginAsAdmin() {
    this.errorMsg = '';
    this.loading = true;

    this.auth.login({ email: this.email.trim(), password: this.password })
      .pipe(finalize(() => (this.loading = false)))
      .subscribe({
        next: (res: any) => {
          const token = res.token ?? res.accessToken ?? res.jwt;
          const role = (res.role ?? 'admin').toString().toLowerCase();
          const displayName = res.displayName ?? res.username ?? 'Admin';

          localStorage.setItem('role', role);
          localStorage.setItem('displayName', displayName);
          if (token) localStorage.setItem('token', token);

          if (role !== 'admin') {
            this.errorMsg = 'This account is not an admin.';
            return;
          }

          this.router.navigateByUrl('/admin-dashboard');
        },
        error: (err) => {
          this.errorMsg =
            err?.error?.message || err?.message || 'Login failed. Check credentials.';
        }
      });
  }
}
