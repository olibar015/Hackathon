import { CommonModule } from '@angular/common';
import { Component, ElementRef, HostListener, OnInit, ViewChild } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { SidebarComponent } from '../sidebar/sidebar.component';

import {
  PendingBingoCard,
  VerifiableBingoTask,
  VerificationStoreService,
} from '../verification-store/verification-store.service';

type OverlayType = 'bingo' | 'blackout';

@Component({
  selector: 'app-bingo-task',
  standalone: true,
  imports: [CommonModule, FormsModule, SidebarComponent],
  templateUrl: './bingo-task.component.html',
  styleUrls: ['./bingo-task.component.scss'],
})
export class BingoTaskComponent implements OnInit {

  // ✅ Replace later with real login
  employee = { id: 'emp-001', name: 'Employee' };

  // ✅ user choice
  gridSize: 3 | 5 = 5;
  hasChosenSize = false;
  readonly bingoBalls = ['B', 'I', 'N', 'G', 'O'];

  // ✅ UI tasks
  tasks: VerifiableBingoTask[] = [];

  // ✅ modal state
  selectedTask: VerifiableBingoTask | null = null;
  selectedProofPreview: string | null = null;
  selectedProofNote = '';

  // ✅ overlay (kept minimal)
  showOverlay = false;
  overlayType: OverlayType = 'bingo';
  overlayPos = { x: window.innerWidth / 2, y: window.innerHeight / 2 };

  @ViewChild('bingoCard', { static: false }) bingoCard?: ElementRef<HTMLElement>;

  constructor(private store: VerificationStoreService) {}

  ngOnInit(): void {
    // optional: nothing on init
  }

  // ======================
  // UI helpers
  // ======================
  get letters(): string[] {
    return this.gridSize === 5 ? ['B', 'I', 'N', 'G', 'O'] : ['B', 'I', 'N'];
  }

  get columns(): number[] {
    return Array.from({ length: this.gridSize }, (_, i) => i);
  }

  // ✅ completed = VERIFIED + FREE
  get completedCount(): number {
    return this.tasks.filter(t => t.isFree || t.verificationStatus === 'VERIFIED').length;
  }

  get totalTasks(): number {
    return this.tasks.length;
  }

  // ======================
  // data sources
  // ======================
  private readonly tasks5x5Source: VerifiableBingoTask[] = [
    { id: 1, name: 'Complete Training (Any IT Skill)', points: 50, verificationStatus: 'AVAILABLE' },
    { id: 2, name: 'Acquire Training Certificate', points: 100, verificationStatus: 'AVAILABLE' },
    { id: 3, name: 'No Absences for the Whole Month', points: 200, verificationStatus: 'AVAILABLE' },
    { id: 4, name: 'Team Learning Activity', points: 30, verificationStatus: 'AVAILABLE' },
    { id: 5, name: 'Mentor Colleague', points: 25, verificationStatus: 'AVAILABLE' },

    { id: 6, name: 'Attend Team Knowledge Session', points: 30, verificationStatus: 'AVAILABLE' },
    { id: 7, name: 'Participate ISO Audit', points: 50, verificationStatus: 'AVAILABLE' },
    { id: 8, name: 'Have 1-on-1 Session with Your Department / Immediate Head', points: 50, verificationStatus: 'AVAILABLE' },
    { id: 9, name: 'Share Knowledge in Community Chat', points: 20, verificationStatus: 'AVAILABLE' },
    { id: 10, name: 'Share Webinar / Tech Recap', points: 20, verificationStatus: 'AVAILABLE' },

    { id: 11, name: 'Help Teammate', points: 15, verificationStatus: 'AVAILABLE' },
    { id: 12, name: 'Cross-Team Collaboration Task', points: 30, verificationStatus: 'AVAILABLE' },
    { id: 13, name: 'Document Process / Tool Setup', points: 30, verificationStatus: 'AVAILABLE' },
    { id: 14, name: 'Present Tool / Project Demo', points: 25, verificationStatus: 'AVAILABLE' },
    { id: 15, name: 'Suggest Automation / Improvement Idea', points: 25, verificationStatus: 'AVAILABLE' },

    { id: 16, name: 'Re-echo the Training to Colleague You Attended', points: 50, verificationStatus: 'AVAILABLE' },
    { id: 17, name: 'On-time Join Weekly Updates', points: 15, verificationStatus: 'AVAILABLE' },
    { id: 18, name: 'No Late for the Whole Month', points: 100, verificationStatus: 'AVAILABLE' },
    { id: 19, name: 'Follow Proper Dress Code', points: 20, verificationStatus: 'AVAILABLE' },
    { id: 20, name: 'Update Knowledge Base / Docs', points: 30, verificationStatus: 'AVAILABLE' },

    { id: 21, name: 'Attend Webinar', points: 30, verificationStatus: 'AVAILABLE' },
    { id: 22, name: 'Resolved Issues / Concerns from Client', points: 100, verificationStatus: 'AVAILABLE' },
    { id: 23, name: 'Passed on ISO Internal Audit', points: 50, verificationStatus: 'AVAILABLE' },
    { id: 24, name: 'Solve Risk / Tech Scenario', points: 30, verificationStatus: 'AVAILABLE' },
    { id: 25, name: 'FREE', points: 0, verificationStatus: 'VERIFIED' },
  ];

  private readonly tasks3x3Source: VerifiableBingoTask[] = [
    { id: 1, name: 'React ❤️ to Milli’s greeting picture in One Exakt GC', points: 5, verificationStatus: 'AVAILABLE' },
    { id: 2, name: 'FREE', points: 0, verificationStatus: 'VERIFIED' },
    { id: 3, name: 'Greet Milli in OneExakt GC', points: 5, verificationStatus: 'AVAILABLE' },

    { id: 4, name: 'Wear something blue', points: 50, verificationStatus: 'AVAILABLE' },
    { id: 5, name: 'MILLIS DAY', points: 0, verificationStatus: 'VERIFIED' },
    { id: 6, name: 'FREE', points: 0, verificationStatus: 'VERIFIED' },

    { id: 7, name: 'Give something blue to Milli', points: 10, verificationStatus: 'AVAILABLE' },
    { id: 8, name: 'FREE', points: 0, verificationStatus: 'VERIFIED' },
    { id: 9, name: 'Picture with Milli', points: 10, verificationStatus: 'AVAILABLE' },
  ];

  // ======================
  // board selection
  // ======================
  setBoardSize(size: 3 | 5): void {
    this.gridSize = size;
    this.hasChosenSize = true;

    const saved = this.store.loadEmployeeCard(this.employee.id, size);
    if (saved) {
      this.tasks = saved.map(t => ({ ...t }));
      return;
    }

    this.tasks = size === 5 ? this.build5x5() : this.build3x3();

    if (size === 5) this.initializeFreeCenter();

    this.saveBoard();
  }

  private build5x5(): VerifiableBingoTask[] {
    return this.tasks5x5Source.map(t => ({
      ...t,
      isFree: false,
      logs: t.logs || [],
    }));
  }

  private build3x3(): VerifiableBingoTask[] {
    return this.tasks3x3Source.map(t => ({
      ...t,
      isFree: false,
      logs: t.logs || [],
    }));
  }

  private initializeFreeCenter(): void {
    if (this.gridSize !== 5) return;

    const centerIndex = 12;
    const centerTask = this.tasks[centerIndex];
    if (!centerTask) return;

    centerTask.isFree = true;
    centerTask.name = 'FREE';
    centerTask.points = 0;
    centerTask.verificationStatus = 'VERIFIED';
  }

  backToChoice(): void {
    this.hasChosenSize = false;
    this.tasks = [];
    this.showOverlay = false;
  }

  // ======================
  // click => open modal
  // ======================
  onCellClick(task: VerifiableBingoTask): void {
    if (!task) return;

    // FREE should not open modal
    if (task.isFree) return;

    // allow resubmit if rejected, allow view if pending, block if verified
    this.selectedTask = task;
    this.selectedProofPreview = task.proof?.imageBase64 || null;
    this.selectedProofNote = task.proof?.note || '';
  }

  // ✅ THIS FIXES YOUR (change)="onProofFileSelected($event)" ERROR
  onProofFileSelected(event: Event): void {
    const input = event.target as HTMLInputElement;
    if (!input.files || input.files.length === 0) return;

    const file = input.files[0];
    if (!file.type.startsWith('image/')) return;

    const reader = new FileReader();
    reader.onload = () => {
      this.selectedProofPreview = String(reader.result || null);
    };
    reader.readAsDataURL(file);
  }

  submitProof(): void {
    if (!this.selectedTask) return;
    if (!this.selectedProofPreview) return;

    // attach proof + pending
    this.selectedTask.proof = {
      imageBase64: this.selectedProofPreview,
      note: (this.selectedProofNote || '').trim(),
      submittedAt: new Date().toISOString(),
    };
    this.selectedTask.verificationStatus = 'PENDING';

    // persist employee board
    this.saveBoard();

    // submit whole card to admin queue
    const cardId = `${this.employee.id}_${this.gridSize}`;

    const payload: PendingBingoCard = {
      cardId,
      employeeId: this.employee.id,
      employeeName: this.employee.name,
      gridSize: this.gridSize,
      submittedAt: new Date().toISOString(),
      tasks: this.tasks.map(t => ({ ...t })),
    };

    this.store.submitForVerification(payload);

    this.closeTaskModal();
  }

  closeTaskModal(): void {
    this.selectedTask = null;
    this.selectedProofPreview = null;
    this.selectedProofNote = '';
  }

  private saveBoard(): void {
    this.store.saveEmployeeCard(this.employee.id, this.gridSize, this.tasks.map(t => ({ ...t })));
  }

  // ======================
  // overlay position helpers (optional)
  // ======================
  private updateOverlayPosition(): void {
    const card = this.bingoCard?.nativeElement;
    if (!card) {
      this.overlayPos = { x: window.innerWidth / 2, y: window.innerHeight / 2 };
      return;
    }
    const r = card.getBoundingClientRect();
    this.overlayPos = { x: r.left + r.width / 2, y: r.top + r.height / 2 };
  }

  @HostListener('window:resize')
  onResize(): void {
    if (this.showOverlay) this.updateOverlayPosition();
  }
}
