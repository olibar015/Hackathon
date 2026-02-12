import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { SidebarComponent } from '../sidebar/sidebar.component';

import {
  PendingBingoCard,
  VerificationStoreService,
  VerifiableBingoTask
} from '../verification-store/verification-store.service';

type PendingCardVM = PendingBingoCard & {
  totalTasks: number;
  completedCount: number; // FREE + VERIFIED
  status: 'PENDING' | 'REVIEWED';
};

@Component({
  selector: 'app-admin-dashboard',
  standalone: true,
  imports: [CommonModule, SidebarComponent],
  templateUrl: './admin-dashboard.component.html',
  styleUrl: './admin-dashboard.component.scss'
})
export class AdminDashboardComponent implements OnInit {

  isAdmin = true;

  pendingCards: PendingCardVM[] = [];
  selectedCard: PendingCardVM | null = null;

  // replace later with real login user
  admin = { id: 'admin-001', name: 'Admin' };

  constructor(private store: VerificationStoreService) {}

  ngOnInit(): void {
    this.loadPending();
  }

  loadPending(): void {
    const cards = this.store.getPendingCards();

    this.pendingCards = cards.map(c => {
      const total = c.tasks?.length ?? 0;
      const done = (c.tasks || []).filter(t => t.isFree || t.verificationStatus === 'VERIFIED').length;
      const hasPending = (c.tasks || []).some(t => !t.isFree && t.verificationStatus === 'PENDING');

      return {
        ...c,
        totalTasks: total,
        completedCount: done,
        status: hasPending ? 'PENDING' : 'REVIEWED',
      };
    });

    // keep modal in sync after refresh/approve/reject
    if (this.selectedCard) {
      this.selectedCard = this.pendingCards.find(c => c.cardId === this.selectedCard!.cardId) || null;
    }
  }

  openCard(card: PendingCardVM): void {
    this.selectedCard = card;
  }

  closeCard(): void {
    this.selectedCard = null;
  }

  // ✅ Approve all PENDING tasks in the selected card
  approve(): void {
    if (!this.selectedCard) return;

    const cardId = this.selectedCard.cardId;

    const pendingTasks = (this.selectedCard.tasks || []).filter(
      t => !t.isFree && t.verificationStatus === 'PENDING'
    );

    for (const t of pendingTasks) {
      this.store.verifyTask(cardId, t.id, 'APPROVE', this.admin);
    }

    this.syncBackToEmployee(cardId);
    this.loadPending();
  }

  // ✅ Reject all PENDING tasks in the selected card
  reject(reason: string): void {
    if (!this.selectedCard) return;

    const cardId = this.selectedCard.cardId;
    const cleanReason = (reason || '').trim();

    const pendingTasks = (this.selectedCard.tasks || []).filter(
      t => !t.isFree && t.verificationStatus === 'PENDING'
    );

    for (const t of pendingTasks) {
      this.store.verifyTask(cardId, t.id, 'REJECT', this.admin, cleanReason);
    }

    this.syncBackToEmployee(cardId);
    this.loadPending();
  }

  // helper for badge text (VerifiableBingoTask has no "status")
  badgeText(t: VerifiableBingoTask): string {
    if (t.isFree) return 'FREE';
    return t.verificationStatus;
  }

  private syncBackToEmployee(cardId: string): void {
    const card = this.store.getPendingCards().find(c => c.cardId === cardId);
    if (!card) return;

    const [employeeId, sizeStr] = card.cardId.split('_');
    const gridSize = Number(sizeStr) as 3 | 5;

    const employeeTasks = this.store.loadEmployeeCard(employeeId, gridSize);
    if (!employeeTasks) return;

    for (const et of employeeTasks) {
      const adminTask = card.tasks.find(t => t.id === et.id);
      if (!adminTask) continue;

      et.verificationStatus = adminTask.verificationStatus;
      et.proof = adminTask.proof;
      et.logs = adminTask.logs;
    }

    this.store.saveEmployeeCard(employeeId, gridSize, employeeTasks);

    // remove card if no more pending tasks
    const stillPending = card.tasks.some(t => !t.isFree && t.verificationStatus === 'PENDING');
    if (!stillPending) {
      this.store.removePendingCard(cardId);
    }
  }
}
