import { CommonModule } from '@angular/common';
import { Component, ElementRef, HostListener, ViewChild, OnInit } from '@angular/core';
import { SidebarComponent } from '../sidebar/sidebar.component';

export type TaskStatus = 'available' | 'completed' | 'bingoLine';

export interface BingoTask {
  id: number;
  name: string;
  points: number;
  status: TaskStatus;
  isFree?: boolean;
}

type OverlayType = 'bingo' | 'blackout';

@Component({
  selector: 'app-bingo-task',
  standalone: true,
  imports: [CommonModule, SidebarComponent],
  templateUrl: './bingo-task.component.html',
  styleUrls: ['./bingo-task.component.scss'],
})
export class BingoTaskComponent implements OnInit {

  // ✅ user choice
  gridSize: 3 | 5 = 5;     // always a valid number
  hasChosenSize = false;    // controls if card shows or not
  readonly bingoBalls = ['B', 'I', 'N', 'G', 'O'];


  // ✅ your original 5x5 data stays “saved”
  private readonly tasks5x5Source: BingoTask[] = [
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
    { id: 25, name: 'FREE', points: 0, status: 'completed' },
  ];

  private readonly tasks3x3Source: BingoTask[] = [
    { id: 1, name: 'React ❤️ to Milli’s greeting picture in One Exakt GC', points: 5, status: 'completed' },
    { id: 2, name: 'FREE', points: 0, status: 'completed' },
    { id: 3, name: 'Greet Milli in OneExakt GC', points: 5, status: 'available' },
    { id: 4, name: 'Wear something blue', points: 50, status: 'available' },
    { id: 5, name: 'MILLIS DAY', points: 0, status: 'completed' },

    { id: 6, name: 'FREE', points: 0, status: 'completed' },
    { id: 7, name: 'Give something blue to Milli', points: 10, status: 'available' },
    { id: 8, name: 'FREE', points: 0, status: 'completed' },
    { id: 9, name: 'Picture with Milli', points: 10, status: 'available' },
  ];


  private bingoSfx = new Audio('assets/bingo.mp3');
  private blackoutSfx = new Audio('assets/blackout.mp3');
  private sfxEnabled = true; // optional toggle
  private audioUnlocked = false;

  // ✅ this is what the UI uses (either 5x5 or 3x3)
  tasks: BingoTask[] = [];

  @ViewChild('bingoCard', { static: false }) bingoCard?: ElementRef<HTMLElement>;

  ngOnInit(): void {
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

  get completedCount(): number {
    return this.tasks.filter(t => t.status === 'completed' || t.status === 'bingoLine').length;
  }

  get totalTasks(): number {
    return this.tasks.length;
  }

  // ✅ user action
  setBoardSize(size: 3 | 5): void {
    this.gridSize = size;
    this.hasChosenSize = true;

    // reset stuff (if you already have these)
    this.awardedLines?.clear?.();
    this.hideOverlay?.();
    this.stopConfetti?.();

    this.tasks = (size === 5) ? this.build5x5() : this.build3x3();

    if (size === 5) this.initializeFreeCenter(); // FREE only for 5x5
    // 3x3: no free

    this.evaluateBoard();
  }


  private build5x5(): BingoTask[] {
    return this.tasks5x5Source.map(t => ({
      ...t,
      status: t.status ?? 'available',
      isFree: false,
    }));
  }

  private build3x3(): BingoTask[] {
    return this.tasks3x3Source.map(t => ({
      ...t,
      status: 'available', // reset each time you switch
      isFree: false,       // ✅ no FREE on 3x3
    }));
  }

  // ======================
  // cursor + overlay positioning
  // ======================

  overlayPos = { x: window.innerWidth / 2, y: window.innerHeight / 2 };
  isAdmin = true;
  private hideTimer: any = null;
  private confettiTimer: any = null;

  showOverlay = false;
  overlayType: OverlayType = 'bingo';

  @ViewChild('confettiCanvas') confettiCanvas?: ElementRef<HTMLCanvasElement>;

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
  // click handling
  // ======================

  onCellClick(task: BingoTask) {
    if (!this.isAdmin) return;

    if (!this.audioUnlocked) {
      this.audioUnlocked = true;
      this.unlockAudio();
    }

    if (task.isFree) return;
    if (task.status !== 'available') return;

    task.status = 'completed';
    this.evaluateBoard();
  }

  // ======================
  // ✅ WIN LOGIC (works for 3x3 and 5x5)
  // diagonal, horizontal, vertical
  // ======================

  private evaluateBoard(): void {
    const allMarked = this.tasks.every(t => t.status !== 'available');
    if (allMarked) {
      this.showCelebration('blackout');
      return;
    }

    const lines = this.getAllLines();
    let newLineFound = false;

    for (const line of lines) {
      const complete = line.indices.every(i => this.tasks[i].status !== 'available');
      if (!complete) continue;

      for (const i of line.indices) {
        this.tasks[i].status = 'bingoLine';
      }

      if (!this.awardedLines.has(line.key)) {
        this.awardedLines.add(line.key);
        newLineFound = true;
      }
    }

    if (newLineFound) this.showCelebration('bingo');
  }

  // board is column-major: index = col*size + row
  private idx(row: number, col: number): number {
    return col * this.gridSize + row;
  }

  private getAllLines(): { key: string; indices: number[] }[] {
    const n = this.gridSize;
    const lines: { key: string; indices: number[] }[] = [];

    // rows (horizontal)
    for (let r = 0; r < n; r++) {
      lines.push({
        key: `R${r}`,
        indices: Array.from({ length: n }, (_, c) => this.idx(r, c)),
      });
    }

    // cols (vertical)
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
  // overlay + confetti (unchanged)
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
    centerTask.status = 'completed';
    centerTask.name = 'FREE';
    centerTask.points = 0;
  }

  backToChoice(): void {
    this.hasChosenSize = false;
    this.tasks = [];
    this.awardedLines.clear();
    this.showOverlay = false;
    this.stopConfetti();
  }

  private initAudio(): void {
    // keeps them ready (preload-ish)
    this.bingoSfx.load();
    this.blackoutSfx.load();

    this.bingoSfx.volume = 0.7;
    this.blackoutSfx.volume = 0.7;
  }

  private unlockAudio(): void {
    // called on first user interaction
    const tryPlayPause = async (a: HTMLAudioElement) => {
      try {
        a.muted = true;
        await a.play(); // allowed because user gesture
        a.pause();
        a.currentTime = 0;
        a.muted = false;
      } catch {
        // ignore – some browsers are strict, but usually this works
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
