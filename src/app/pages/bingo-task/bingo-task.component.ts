import { CommonModule } from '@angular/common';
import { Component, ElementRef, HostListener, ViewChild, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { SidebarComponent } from '../sidebar/sidebar.component';

export type TaskStatus = 'available' | 'pending' | 'verified' | 'bingoLine';

type OverlayType = 'bingo' | 'blackout';

type ProofType = 'image' | 'file';
export interface TaskProof {
  type: ProofType;
  name: string;
  url: string;     // blob URL for preview OR remote URL
  file?: File;     // used during upload
}

export interface BingoTask {
  id: number;
  name: string;
  points: number;
  status: TaskStatus;
  isFree?: boolean;

  // NEW (for submit/verification flow)
  completedAt?: string; // display string (e.g. "Feb 8, 2026")
  proofs?: TaskProof[]; // multiple files/images
}

interface BingoCardTemplate {
  id: string;
  title: string;
  subtitle?: string;
  tasks: BingoTask[]; // should be 9 tasks for 3x3
}

@Component({
  selector: 'app-bingo-task',
  standalone: true,
  imports: [CommonModule, SidebarComponent, FormsModule],
  templateUrl: './bingo-task.component.html',
  styleUrls: ['./bingo-task.component.scss'],
})
export class BingoTaskComponent implements OnInit {

  // ✅ user choice
  gridSize: 3 | 5 = 5;
  hasChosenSize = false;

  readonly bingoBalls = ['B', 'I', 'N', 'G', 'O'];

  // ✅ ROLE (use localStorage like your other components)
  role: 'admin' | 'employee' = 'employee';

  // ✅ Employees can play
  get canPlay(): boolean { return this.role === 'employee'; }

  get isEmployee(): boolean { return this.role === 'employee'; }
  get isAdmin(): boolean { return this.role === 'admin'; }

  // ======================
  // DATA SOURCES
  // ======================

  // NOTE: These sources used to have status 'completed'.
  // We keep them but normalize to 'verified' when building the board.
  private readonly tasks5x5Source: Array<Omit<BingoTask, 'status'> & { status: 'completed' | 'available' }> = [
    { id: 1, name: 'Complete Training (Any IT Skill)', points: 50, status: 'completed' },
    { id: 2, name: 'Acquire Training Certificate', points: 100, status: 'available' },
    { id: 3, name: 'No Absences for the Whole Month', points: 200, status: 'available' },
    { id: 4, name: 'Team Learning Activity', points: 30, status: 'available' },
    { id: 5, name: 'Mentor Colleague', points: 25, status: 'available' },

    { id: 6, name: 'Attend Team Knowledge Session', points: 30, status: 'available' },
    { id: 7, name: 'Participate ISO Audit', points: 50, status: 'available' },
    { id: 8, name: 'Have 1-on-1 Session with Your Department / Immediate Head', points: 50, status: 'available' },
    { id: 9, name: 'Share Knowledge in Community Chat', points: 20, status: 'available' },
    { id: 10, name: 'Share Webinar / Tech Recap', points: 20, status: 'available' },

    { id: 11, name: 'Help Teammate', points: 15, status: 'available' },
    { id: 12, name: 'Cross-Team Collaboration Task', points: 30, status: 'available' },
    { id: 13, name: 'Document Process / Tool Setup', points: 30, status: 'available' },
    { id: 14, name: 'Present Tool / Project Demo', points: 25, status: 'available' },
    { id: 15, name: 'Suggest Automation / Improvement Idea', points: 25, status: 'available' },

    { id: 16, name: 'Re-echo the Training to Colleague You Attended', points: 50, status: 'available' },
    { id: 17, name: 'On-time Join Weekly Updates', points: 15, status: 'available' },
    { id: 18, name: 'No Late for the Whole Month', points: 100, status: 'available' },
    { id: 19, name: 'Follow Proper Dress Code', points: 20, status: 'available' },
    { id: 20, name: 'Update Knowledge Base / Docs', points: 30, status: 'available' },

    { id: 21, name: 'Attend Webinar', points: 30, status: 'available' },
    { id: 22, name: 'Resolved Issues / Concerns from Client', points: 100, status: 'available' },
    { id: 23, name: 'Passed on ISO Internal Audit', points: 50, status: 'available' },
    { id: 24, name: 'Solve Risk / Tech Scenario', points: 30, status: 'available' },
    { id: 25, name: 'FREE', points: 0, status: 'available' },
  ];

  private readonly tasks3x3Source: Array<Omit<BingoTask, 'status'> & { status: 'completed' | 'available' }> = [
    { id: 1, name: 'React ❤️ to Milli’s greeting picture in One Exakt GC', points: 5, status: 'available' },
    { id: 2, name: 'FREE', points: 0, status: 'available' },
    { id: 3, name: 'Greet Milli in OneExakt GC', points: 5, status: 'available' },
    { id: 4, name: 'Wear something blue', points: 50, status: 'available' },
    { id: 5, name: 'MILLIS DAY', points: 0, status: 'available' },

    { id: 6, name: 'FREE', points: 0, status: 'available' },
    { id: 7, name: 'Give something blue to Milli', points: 10, status: 'available' },
    { id: 8, name: 'FREE', points: 0, status: 'available' },
    { id: 9, name: 'Picture with Milli', points: 10, status: 'available' },
  ];

  readonly dailyCards3x3: BingoCardTemplate[] = [
    {
      id: 'daily-1',
      title: 'Daily Fun Card #1',
      subtitle: 'Milli’s Day Set',
      tasks: this.tasks3x3Source.map(t => this.normalizeIncomingTask(t)),
    },
    {
      id: 'daily-2',
      title: 'Daily Fun Card #2',
      subtitle: 'Valentines Day Set',
      tasks: [
        { id: 1, name: 'Wear something red ❤️', points: 5, status: 'available' },
        { id: 2, name: 'Give chocolate/flowers to Jhe 🍫', points: 15, status: 'available' },
        { id: 3, name: 'Greet someone Happy Valentines Day 💌', points: 5, status: 'available' },

        { id: 4, name: 'FREE', points: 0, status: 'available' },
        { id: 5, name: 'VALENTINES DAY', points: 0, status: 'available' },
        { id: 6, name: 'FREE', points: 0, status: 'available' },

        { id: 7, name: 'Give something red or pink to a teammate', points: 10, status: 'available' },
        { id: 8, name: 'FREE', points: 0, status: 'available' },
        { id: 9, name: 'Picture with a Valentine prop (heart, rose, chocolate)', points: 10, status: 'available' },
      ],
    },
    {
      id: 'daily-3',
      title: 'Daily Fun Card #3',
      subtitle: 'Quick Challenges Set',
      tasks: [
        { id: 1, name: 'Clean your workspace', points: 10, status: 'available' },
        { id: 2, name: 'Inbox zero (5 mins)', points: 10, status: 'available' },
        { id: 3, name: 'Write 1 goal for today', points: 10, status: 'available' },

        { id: 4, name: 'Stand up (1 min)', points: 5, status: 'available' },
        { id: 5, name: 'Deep breath (30 sec)', points: 5, status: 'available' },
        { id: 6, name: 'Play 1 song break', points: 5, status: 'available' },

        { id: 7, name: 'Thank a teammate', points: 10, status: 'available' },
        { id: 8, name: 'Share a tip', points: 10, status: 'available' },
        { id: 9, name: 'End-of-day reflection', points: 10, status: 'available' },
      ],
    },
  ];

  // ✅ Picker state
  showCardPicker = false;
  selectedCardId: string | null = null;

  // ✅ Saves progress per 3x3 card while switching (front-end only)
  private cardState = new Map<string, BingoTask[]>();

  // ======================
  // AUDIO
  // ======================

  private bingoSfx = new Audio('assets/bingo.mp3');
  private blackoutSfx = new Audio('assets/blackout.mp3');
  private sfxEnabled = true;
  private audioUnlocked = false;

  // ✅ UI board
  tasks: BingoTask[] = [];

  @ViewChild('bingoCard', { static: false }) bingoCard?: ElementRef<HTMLElement>;
  @ViewChild('confettiCanvas') confettiCanvas?: ElementRef<HTMLCanvasElement>;

  // ======================
  // TASK SUBMIT DIALOG (NEW)
  // ======================

  showTaskDialog = false;
  activeTask: BingoTask | null = null;

  completionDateISO = new Date().toISOString().slice(0, 10);

  selectedFiles: File[] = [];
  filePreviews: TaskProof[] = [];

  // ======================
  // WIN TOAST (NEW)
  // ======================

  showWinToast = false;
  winToastText = '';

  private showWin(points: number) {
    this.winToastText = `You win +${points} pts!`;
    this.showWinToast = true;
    setTimeout(() => (this.showWinToast = false), 2200);
  }

  ngOnInit(): void {
    const r = (localStorage.getItem('role') || 'employee').toLowerCase();
    if (r === 'admin' || r === 'employee') this.role = r;

    this.initAudio();
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

  // ✅ ONLY verified/bingoLine count as done (pending does NOT count)
  get completedCount(): number {
    return this.tasks.filter(t => t.isFree || t.status === 'verified' || t.status === 'bingoLine').length;
  }

  get totalTasks(): number {
    return this.tasks.length;
  }

  // ======================
  // BOARD SETUP
  // ======================

  start3x3(): void {
    this.gridSize = 3;
    this.showCardPicker = true;
  }

  choose3x3Card(cardId: string): void {
    this.selectedCardId = cardId;
    this.showCardPicker = false;

    this.hasChosenSize = true;
    this.gridSize = 3;

    this.resetBoardEffects();
    this.loadSelected3x3Card();
  }

  setBoardSize(size: 3 | 5): void {
    if (size === 3 && this.isEmployee) {
      this.start3x3();
      return;
    }

    this.gridSize = size;
    this.hasChosenSize = true;

    this.resetBoardEffects();

    this.tasks = (size === 5) ? this.build5x5() : this.build3x3Static();
    if (size === 5) this.initializeFreeCenter();

    this.evaluateBoard();
  }

  private resetBoardEffects(): void {
    this.awardedLines.clear();
    this.hideOverlay();
    this.stopConfetti();
  }

  private normalizeIncomingTask(t: any): BingoTask {
    // convert legacy 'completed' into new 'verified'
    const status: TaskStatus =
      t.status === 'completed' ? 'verified'
        : (t.status as TaskStatus) ?? 'available';

    return {
      id: t.id,
      name: t.name,
      points: t.points,
      status,
      isFree: false,
      completedAt: t.completedAt,
      proofs: t.proofs,
    };
  }

  private build5x5(): BingoTask[] {
    return this.tasks5x5Source.map(t => ({
      ...this.normalizeIncomingTask(t),
      isFree: false,
    }));
  }

  private build3x3Static(): BingoTask[] {
    return this.tasks3x3Source.map(t => ({
      ...this.normalizeIncomingTask(t),
      isFree: false,
    }));
  }

  private loadSelected3x3Card(): void {
    if (!this.selectedCardId) return;

    const saved = this.cardState.get(this.selectedCardId);
    if (saved) {
      this.tasks = this.cloneTasks(saved);
    } else {
      const tpl = this.dailyCards3x3.find(c => c.id === this.selectedCardId);
      this.tasks = this.cloneTasks((tpl?.tasks ?? []).map(t => this.normalizeIncomingTask(t)));

      // for 3x3, keep your "FREE" text but treat as normal unless you want it special
      this.tasks.forEach(t => (t.isFree = false));

      this.cardState.set(this.selectedCardId, this.cloneTasks(this.tasks));
    }

    this.evaluateBoard();
  }

  private saveCurrentCardState(): void {
    if (this.gridSize !== 3 || !this.selectedCardId) return;
    this.cardState.set(this.selectedCardId, this.cloneTasks(this.tasks));
  }

  private cloneTasks(list: BingoTask[]): BingoTask[] {
    return list.map(t => ({
      ...t,
      proofs: t.proofs ? t.proofs.map(p => ({ ...p })) : undefined,
    }));
  }

  // ======================
  // cursor + overlay positioning
  // ======================

  overlayPos = { x: window.innerWidth / 2, y: window.innerHeight / 2 };

  private hideTimer: any = null;
  private confettiTimer: any = null;

  showOverlay = false;
  overlayType: OverlayType = 'bingo';

  private awardedLines = new Set<string>();

  private raf = 0;
  private confettiParts: {
    x: number; y: number;
    vx: number; vy: number;
    rot: number; vr: number;
    w: number; h: number;
    life: number; max: number;
    kind: number;
  }[] = [];

  private updateOverlayPosition() {
    const card = this.bingoCard?.nativeElement;
    if (!card) {
      this.overlayPos = { x: window.innerWidth / 2, y: window.innerHeight / 2 };
      return;
    }

    const r = card.getBoundingClientRect();
    this.overlayPos = { x: r.left + r.width / 2, y: r.top + r.height / 2 };
  }

  @HostListener('document:mousemove', ['$event'])
  onMouseMove(e: MouseEvent) {
    const hand = document.querySelector('.hand-cursor') as HTMLElement;
    if (!hand) return;
    hand.style.left = e.clientX + 'px';
    hand.style.top = e.clientY + 'px';
  }

  @HostListener('window:resize')
  onResize() {
    if (this.showOverlay) this.updateOverlayPosition();
  }

  @HostListener('window:scroll')
  onScroll() {
    if (this.showOverlay) this.updateOverlayPosition();
  }

  // ======================
  // TASK DIALOG (NEW)
  // ======================

  openTaskDialog(task: BingoTask) {
    this.activeTask = task;
    this.completionDateISO = new Date().toISOString().slice(0, 10);
    this.selectedFiles = [];
    this.filePreviews = [];
    this.showTaskDialog = true;
  }

  closeTaskDialog() {
    // cleanup blob URLs
    for (const p of this.filePreviews) {
      if (p.url?.startsWith('blob:')) URL.revokeObjectURL(p.url);
    }
    this.showTaskDialog = false;
    this.activeTask = null;
    this.selectedFiles = [];
    this.filePreviews = [];
  }

  onProofFilesSelected(ev: Event) {
    const input = ev.target as HTMLInputElement;
    const files = Array.from(input.files ?? []);
    this.selectedFiles = files;

    // cleanup old previews
    for (const p of this.filePreviews) {
      if (p.url?.startsWith('blob:')) URL.revokeObjectURL(p.url);
    }

    this.filePreviews = files.map(f => {
      const isImg = f.type?.startsWith('image/');
      return {
        type: isImg ? 'image' : 'file',
        name: f.name,
        url: URL.createObjectURL(f),
        file: f,
      };
    });
  }

  submitTaskProof() {
    if (!this.activeTask) return;

    // ✅ REQUIRE AT LEAST 1 FILE
    if (this.filePreviews.length === 0) {
      alert('Please upload at least one proof before submitting.');
      return;
    }

    const t = this.activeTask;

    const niceDate = new Date(this.completionDateISO).toLocaleDateString();
    t.completedAt = niceDate;
    t.proofs = [...this.filePreviews];

    t.status = 'pending';

    this.saveCurrentCardState();
    this.closeTaskDialog();
  }


  // ======================
  // click handling (UPDATED)
  // ======================

  onCellClick(task: BingoTask) {
    if (!this.canPlay) return;

    if (!this.audioUnlocked) {
      this.audioUnlocked = true;
      this.unlockAudio();
    }

    if (task.isFree) return;
    if (task.status !== 'available') return;

    // NEW: open dialog instead of instant win
    this.openTaskDialog(task);
  }

  // ======================
  // VERIFY/REJECT (CALL THESE FROM API UPDATES)
  // ======================

  // Call when backend confirms this task is VERIFIED
  markTaskVerified(taskId: number) {
    const t = this.tasks.find(x => x.id === taskId);
    if (!t) return;

    if (t.status === 'pending') {
      t.status = 'verified';

      // toast message: You win +X pts
      this.showWin(t.points);

      // now it can count for bingo lines/blackout
      this.evaluateBoard();
      this.saveCurrentCardState();
    }
  }

  // Optional: when admin rejects, return to available
  markTaskRejected(taskId: number) {
    const t = this.tasks.find(x => x.id === taskId);
    if (!t) return;

    if (t.status === 'pending') {
      t.status = 'available';
      t.completedAt = undefined;
      t.proofs = undefined;
      this.saveCurrentCardState();
    }
  }

  // ======================
  // ✅ WIN LOGIC (UPDATED)
  // Only VERIFIED/BINGOLINE/FREE count (PENDING does NOT)
  // ======================

  private isMarked(t: BingoTask | undefined): boolean {
    if (!t) return false;
    return !!t.isFree || t.status === 'verified' || t.status === 'bingoLine';
  }

  private evaluateBoard(): void {
    if (this.tasks.length === 0) return;

    const allMarked = this.tasks.every(t => this.isMarked(t));
    if (allMarked) {
      this.showCelebration('blackout');
      return;
    }

    const lines = this.getAllLines();
    let newLineFound = false;

    for (const line of lines) {
      const complete = line.indices.every(i => this.isMarked(this.tasks[i]));
      if (!complete) continue;

      // upgrade all in the line to bingoLine (except FREE)
      for (const i of line.indices) {
        const cell = this.tasks[i];
        if (!cell) continue;
        if (cell.isFree) continue;
        cell.status = 'bingoLine';
      }

      if (!this.awardedLines.has(line.key)) {
        this.awardedLines.add(line.key);
        newLineFound = true;
      }
    }

    if (newLineFound) this.showCelebration('bingo');

    // save state after evaluation (bingoLine may be applied)
    this.saveCurrentCardState();
  }

  // board is column-major: index = col*size + row
  private idx(row: number, col: number): number {
    return col * this.gridSize + row;
  }

  private getAllLines(): { key: string; indices: number[] }[] {
    const n = this.gridSize;
    const lines: { key: string; indices: number[] }[] = [];

    // rows
    for (let r = 0; r < n; r++) {
      lines.push({
        key: `R${r}`,
        indices: Array.from({ length: n }, (_, c) => this.idx(r, c)),
      });
    }

    // cols
    for (let c = 0; c < n; c++) {
      lines.push({
        key: `C${c}`,
        indices: Array.from({ length: n }, (_, r) => this.idx(r, c)),
      });
    }

    // diagonals
    lines.push({
      key: 'D0',
      indices: Array.from({ length: n }, (_, i) => this.idx(i, i)),
    });
    lines.push({
      key: 'D1',
      indices: Array.from({ length: n }, (_, i) => this.idx(i, n - 1 - i)),
    });

    return lines;
  }

  // ======================
  // overlay + confetti (same behavior)
  // ======================

  private showCelebration(type: 'bingo' | 'blackout'): void {
    if (this.showOverlay && this.overlayType === 'blackout') return;

    if (this.hideTimer) clearTimeout(this.hideTimer);
    if (this.confettiTimer) clearTimeout(this.confettiTimer);

    this.updateOverlayPosition();
    this.overlayType = type;
    this.showOverlay = true;
    this.playSfx(type);

    this.stopConfetti();

    if (type === 'bingo') {
      setTimeout(() => this.startConfetti('bingo'), 0);
      setTimeout(() => this.hideOverlay(), 2200);
    } else {
      setTimeout(() => this.startConfetti('blackout'), 0);
      setTimeout(() => this.hideOverlay(), 2600);
    }
  }

  private hideOverlay(): void {
    this.showOverlay = false;
    this.stopConfetti();

    if (this.hideTimer) clearTimeout(this.hideTimer);
    if (this.confettiTimer) clearTimeout(this.confettiTimer);

    this.hideTimer = null;
    this.confettiTimer = null;
  }

  private startConfetti(mode: 'bingo' | 'blackout'): void {
    const canvas = this.confettiCanvas?.nativeElement;
    if (!canvas) return;

    const ctx = canvas.getContext('2d');
    if (!ctx) return;

    const resize = () => {
      const dpr = Math.max(1, window.devicePixelRatio || 1);
      canvas.width = Math.floor(canvas.clientWidth * dpr);
      canvas.height = Math.floor(canvas.clientHeight * dpr);
      ctx.setTransform(dpr, 0, 0, dpr, 0, 0);
    };

    resize();

    const w = canvas.clientWidth;
    const h = canvas.clientHeight;
    const cx = this.overlayPos.x;
    const cy = this.overlayPos.y - 60;

    this.confettiParts = [];
    const count = mode === 'blackout' ? 420 : 180;

    for (let i = 0; i < count; i++) {
      const a = Math.random() * Math.PI * 2;
      const sp = mode === 'blackout' ? 6 + Math.random() * 12 : 3 + Math.random() * 8;

      this.confettiParts.push({
        x: cx + (Math.random() * 18 - 9),
        y: cy + (Math.random() * 12 - 6),
        vx: Math.cos(a) * sp,
        vy: Math.sin(a) * sp - (2 + Math.random() * 2.5),
        rot: Math.random() * Math.PI,
        vr: (Math.random() - 0.5) * 0.35,
        w: 5 + Math.random() * 6,
        h: 3 + Math.random() * 5,
        life: 0,
        max: mode === 'blackout' ? 90 + Math.random() * 60 : 55 + Math.random() * 40,
        kind: (Math.random() * 4) | 0,
      });
    }

    const tick = () => {
      ctx.clearRect(0, 0, w, h);

      const g = 0.22;
      const drag = 0.992;

      for (const p of this.confettiParts) {
        p.vx *= drag;
        p.vy = p.vy * drag + g;
        p.x += p.vx;
        p.y += p.vy;
        p.rot += p.vr;
        p.life += 1;
      }

      for (const p of this.confettiParts) {
        if (p.life >= p.max) continue;

        const alpha = 1 - p.life / p.max;
        const colors = mode === 'blackout'
          ? [
            `rgba(239,68,68,${alpha})`,
            `rgba(220,38,38,${alpha})`,
            `rgba(185,28,28,${alpha})`,
            `rgba(248,113,113,${alpha})`,
          ]
          : [
            `rgba(255,216,107,${alpha})`,
            `rgba(240,181,60,${alpha})`,
            `rgba(215,146,25,${alpha})`,
            `rgba(255,243,196,${alpha})`,
          ];

        ctx.fillStyle = colors[p.kind];

        ctx.save();
        ctx.translate(p.x, p.y);
        ctx.rotate(p.rot);
        ctx.fillRect(-p.w / 2, -p.h / 2, p.w, p.h);
        ctx.restore();
      }

      this.confettiParts = this.confettiParts.filter(p => p.life < p.max && p.y < h + 80);

      if (this.confettiParts.length > 0 && this.showOverlay) {
        this.raf = requestAnimationFrame(tick);
      }
    };

    window.addEventListener('resize', resize);
    this.raf = requestAnimationFrame(tick);

    const oldStop = this.stopConfetti.bind(this);
    this.stopConfetti = () => {
      window.removeEventListener('resize', resize);
      oldStop();
    };
  }

  private stopConfetti(): void {
    cancelAnimationFrame(this.raf);
    this.raf = 0;
    this.confettiParts = [];
  }

  private initializeFreeCenter(): void {
    if (this.gridSize !== 5) return;

    const centerIndex = 12; // 5x5 center (0-based)
    const centerTask = this.tasks[centerIndex];
    if (!centerTask) return;

    centerTask.isFree = true;
    centerTask.status = 'verified'; // counts for lines
    centerTask.name = 'FREE';
    centerTask.points = 0;
  }

  backToChoice(): void {
    this.hasChosenSize = false;
    this.tasks = [];
    this.awardedLines.clear();
    this.showOverlay = false;
    this.stopConfetti();

    this.showCardPicker = false;
    this.selectedCardId = null;
  }

  // ======================
  // AUDIO
  // ======================

  private initAudio(): void {
    this.bingoSfx.load();
    this.blackoutSfx.load();

    this.bingoSfx.volume = 0.7;
    this.blackoutSfx.volume = 0.7;
  }

  private unlockAudio(): void {
    const tryPlayPause = async (a: HTMLAudioElement) => {
      try {
        a.muted = true;
        await a.play();
        a.pause();
        a.currentTime = 0;
        a.muted = false;
      } catch {
        // ignore
      }
    };

    void tryPlayPause(this.bingoSfx);
    void tryPlayPause(this.blackoutSfx);
  }

  private playSfx(type: 'bingo' | 'blackout'): void {
    if (!this.sfxEnabled) return;

    const a = type === 'bingo' ? this.bingoSfx : this.blackoutSfx;

    try {
      a.pause();
      a.currentTime = 0;
      void a.play();
    } catch {
      // ignore
    }
  }
}
