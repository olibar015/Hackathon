import { CommonModule } from '@angular/common';
import { Component, Input } from '@angular/core';
import { RouterModule } from '@angular/router';

@Component({
  selector: 'app-sidebar',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './sidebar.component.html',
  styleUrl: './sidebar.component.scss'
})
export class SidebarComponent {
  @Input() role: 'admin' | 'employee' = 'employee';
  @Input() displayName = 'User';

  get roleLabel(): string {
    return this.role === 'admin' ? 'Admin' : 'Employee';
  }
}
