import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';

import {
  PendingBingoCard,
  VerifiableBingoTask,
  VerificationStoreService
} from './verification-store.service';

type CardVM = PendingBingoCard & {
  pendingTasksCount: number;
  verifiedTasksCount: number;
  rejectedTasksCount: number;
};

@Component({
  selector: 'app-verification-store',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './verification-store.component.html',
  styleUrls: ['./verification-store.component.scss'],
})
export class VerificationStoreComponent implements OnInit {

  // ✅ replace with real admin session later
  admin = { id: 'admin-001', name: 'Admin' };

  pendingCards: CardVM[] = [];
  selectedCard: CardVM | null = null;

  // reject reason per-task
  rejectReasonMap: Record<number, string> = {};

  constructor(private store: VerificationStoreService) {}

  ngOnInit(): void {
    this.refresh();
  }

  refresh(): void {
    const cards = this.store.getPendingCards();

    this.pendingCards = cards.map(c => {
      const pending = c.tasks.filter(t => !t.isFree && t.verificationStatus === 'PENDING').length;
      const verified = c.tasks.filter(t => t.isFree || t.verificationStatus === 'VERIFIED').length;
      const rejected = c.tasks.filter(t => !t.isFree && t.verificationStatus === 'REJECTED').length;

      return {
        ...c,
        pendingTasksCount: pending,
        verifiedTasksCount: verified,
        rejectedTasksCount: rejected,
      };
    });

    // keep selected card synced
    if (this.selectedCard) {
      const latest = this.pendingCards.find(c => c.cardId === this.selectedCard!.cardId) ?? null;
      this.selectedCard = latest;
    }
  }

  openCard(card: CardVM): void {
    this.selectedCard = card;
  }

  closeCard(): void {
    this.selectedCard = null;
    this.rejectReasonMap = {};
  }

  approveTask(task: VerifiableBingoTask): void {
    if (!this.selectedCard) return;
    if (task.isFree) return;
    if (task.verificationStatus !== 'PENDING') return;

    this.store.verifyTask(this.selectedCard.cardId, task.id, 'APPROVE', this.admin);
    this.refresh();
  }

  rejectTask(task: VerifiableBingoTask): void {
    if (!this.selectedCard) return;
    if (task.isFree) return;
    if (task.verificationStatus !== 'PENDING') return;

    const reason = (this.rejectReasonMap[task.id] || '').trim();
    this.store.verifyTask(this.selectedCard.cardId, task.id, 'REJECT', this.admin, reason);
    this.refresh();
  }

  // Optional: remove card from queue if admin wants
  removeCard(card: CardVM): void {
    this.store.removePendingCard(card.cardId);
    if (this.selectedCard?.cardId === card.cardId) this.closeCard();
    this.refresh();
  }
}
