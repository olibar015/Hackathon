import { Component } from '@angular/core';
import { RouterModule } from '@angular/router';
import { AuthService } from '../../services/auth.service';
import { FormsModule } from '@angular/forms';
import Swal from 'sweetalert2';

@Component({
  selector: 'app-registration',
  standalone: true,
  imports: [RouterModule, FormsModule],
  templateUrl: './registration.component.html',
  styleUrl: './registration.component.scss'
})
export class RegistrationComponent {

  department: 'Technical' | 'Sales' | 'Admin/HR' | '' = '';
  username = '';
  email = '';
  password = '';
  confirmPassword = '';

  constructor(private authService: AuthService) { }

  registerUser() {
    if (this.password !== this.confirmPassword) {
      Swal.fire({
        icon: 'error',
        title: 'Passwords do not match',
        text: 'Please make sure your passwords are the same.'
      });
      return;
    }

    const payload = {
      department: this.department,
      username: this.username,
      email: this.email,
      password: this.password,
      role: 'USER' // ✅ force role
    };

    console.log('Submitting register payload:', payload);

    this.authService.register(payload).subscribe({
      next: (res: any) => {
        console.log('Success', res);
        Swal.fire({
          icon: 'success',
          title: 'Registered!',
          text: 'Account created successfully.'
        });
      },
      error: (err: any) => {
        console.error('Error', err);
        Swal.fire({
          icon: 'error',
          title: 'Registration failed',
          text: err?.error?.message ?? 'Something went wrong.'
        });
      }
    });
  }
}
