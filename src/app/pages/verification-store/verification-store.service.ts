import { Injectable } from '@angular/core';

export type VerificationStatus = 'AVAILABLE' | 'PENDING' | 'VERIFIED' | 'REJECTED';

export interface VerificationProof {
  imageBase64: string; // screenshot/photo (dataURL)
  note?: string;
  submittedAt: string; // ISO
}

export interface VerificationLog {
  action: 'APPROVED' | 'REJECTED';
  adminId: string;
  adminName: string;
  reason?: string;
  verifiedAt: string; // ISO
}

export interface VerifiableBingoTask {
  id: number;
  name: string;
  points: number;
  isFree?: boolean;

  verificationStatus: VerificationStatus;
  proof?: VerificationProof;
  logs?: VerificationLog[];
}

export interface PendingBingoCard {
  cardId: string; // employeeId_gridSize
  employeeId: string;
  employeeName: string;
  gridSize: 3 | 5;
  submittedAt: string; // ISO
  tasks: VerifiableBingoTask[];
}

const LS_PENDING = 'bingo_pending_cards';
const LS_EMPLOYEE = 'bingo_employee_cards';

@Injectable({ providedIn: 'root' })
export class VerificationStoreService {
  /* =========================
     EMPLOYEE: SAVE BOARD STATE
     ========================= */
  saveEmployeeCard(employeeId: string, gridSize: 3 | 5, tasks: VerifiableBingoTask[]) {
    const all = this.readObject<Record<string, VerifiableBingoTask[]>>(LS_EMPLOYEE, {});
    all[`${employeeId}_${gridSize}`] = tasks;
    localStorage.setItem(LS_EMPLOYEE, JSON.stringify(all));
  }

  loadEmployeeCard(employeeId: string, gridSize: 3 | 5): VerifiableBingoTask[] | null {
    const all = this.readObject<Record<string, VerifiableBingoTask[]>>(LS_EMPLOYEE, {});
    return all[`${employeeId}_${gridSize}`] ?? null;
  }

  /* =========================
     EMPLOYEE: SUBMIT TO ADMIN
     ========================= */
  submitForVerification(card: PendingBingoCard) {
    const list = this.getPendingCards().filter(c => c.cardId !== card.cardId);
    list.unshift(card);
    localStorage.setItem(LS_PENDING, JSON.stringify(list));
  }

  /* =========================
     ADMIN: READ PENDING
     ========================= */
  getPendingCards(): PendingBingoCard[] {
    return this.readArray<PendingBingoCard>(LS_PENDING);
  }

  getPendingCard(cardId: string): PendingBingoCard | null {
    const cards = this.getPendingCards();
    return cards.find(c => c.cardId === cardId) ?? null;
  }

  removePendingCard(cardId: string) {
    const filtered = this.getPendingCards().filter(c => c.cardId !== cardId);
    localStorage.setItem(LS_PENDING, JSON.stringify(filtered));
  }

  /* =========================
     ADMIN: VERIFY TASK
     ========================= */
  verifyTask(
    cardId: string,
    taskId: number,
    decision: 'APPROVE' | 'REJECT',
    admin: { id: string; name: string },
    reason?: string
  ) {
    const cards = this.getPendingCards();
    const card = cards.find(c => c.cardId === cardId);
    if (!card) return;

    const task = card.tasks.find(t => t.id === taskId);
    if (!task) return;

    task.logs = task.logs || [];
    task.logs.push({
      action: decision === 'APPROVE' ? 'APPROVED' : 'REJECTED',
      adminId: admin.id,
      adminName: admin.name,
      reason: decision === 'REJECT' ? (reason?.trim() || 'No reason provided') : undefined,
      verifiedAt: new Date().toISOString(),
    });

    task.verificationStatus = decision === 'APPROVE' ? 'VERIFIED' : 'REJECTED';

    localStorage.setItem(LS_PENDING, JSON.stringify(cards));
  }

  /* ========================= */

  private readArray<T>(key: string): T[] {
    const raw = localStorage.getItem(key);
    return raw ? (JSON.parse(raw) as T[]) : [];
  }

  private readObject<T>(key: string, fallback: T): T {
    const raw = localStorage.getItem(key);
    return raw ? (JSON.parse(raw) as T) : fallback;
  }
}
