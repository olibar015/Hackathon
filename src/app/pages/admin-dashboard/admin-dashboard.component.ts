import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { SidebarComponent } from '../sidebar/sidebar.component';


type ProofType = 'image' | 'file';

interface TaskProof {
  type: ProofType;
  url: string;
  label?: string;
}
interface PendingTaskVerification {
  employeeName: string;
  department: 'IT' | 'HR' | 'Finance' | 'Operations' | string;
  taskTitle: string;
  submittedAt: string; // display-ready string
  proofs: TaskProof[];
}
@Component({
  selector: 'app-admin-dashboard',
  standalone: true,
  imports: [SidebarComponent, CommonModule],
  templateUrl: './admin-dashboard.component.html',
  styleUrl: './admin-dashboard.component.scss'
})

export class AdminDashboardComponent {

  pendingTaskVerifications: PendingTaskVerification[] = [
    {
      employeeName: 'Juan Dela Cruz',
      department: 'IT',
      taskTitle: 'Setup Server Backup',
      submittedAt: 'Feb 8, 2026 • 1:13 PM',
      proofs: [
        { type: 'image', url: 'assets/bingo-bg.jpg', label: 'Screenshot' },
        { type: 'file', url: 'assets/sample.pdf', label: 'View PDF' }
      ]
    },
    {
      employeeName: 'Maria Santos',
      department: 'HR',
      taskTitle: 'Organize Training',
      submittedAt: 'Feb 8, 2026 • 11:13 AM',
      proofs: [{ type: 'image', url: 'assets/bingo-bg.png', label: 'Photo' }]
    }
  ];


  get pendingCount(): number {
    return this.pendingTaskVerifications.length;
  }

  approveTask(task: PendingTaskVerification) {
    // TODO: call API -> approve
    this.pendingTaskVerifications = this.pendingTaskVerifications.filter(t => t !== task);
  }

  rejectTask(task: PendingTaskVerification) {
    // TODO: call API -> reject
    this.pendingTaskVerifications = this.pendingTaskVerifications.filter(t => t !== task);
  }

  trackByIdx(i: number) {
    return i;
  }

}
