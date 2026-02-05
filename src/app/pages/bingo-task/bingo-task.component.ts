import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';

interface BingoCell {
  text: string;
  points: number;
  flipped: boolean;
}

@Component({
  selector: 'app-bingo-task',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './bingo-task.component.html',
  styleUrls: ['./bingo-task.component.scss']
})
export class BingoTaskComponent {

  gridSize = 5;
  totalPoints = 0;
  bingoBonus = 50;

  bingoGrid: BingoCell[][] = [];

  constructor() {
    this.generateGrid();
  }

  generateGrid(): void {
    const tasks: BingoCell[] = Array.from({ length: 25 }, (_, i) => ({
      text: `Task ${i + 1}`,
      points: [5, 10, 15, 20][Math.floor(Math.random() * 4)],
      flipped: false
    }));

    for (let row = 0; row < this.gridSize; row++) {
      this.bingoGrid[row] = [];
      for (let col = 0; col < this.gridSize; col++) {
        const index = row * this.gridSize + col;
        this.bingoGrid[row][col] = tasks[index];
      }
    }

    // Center FREE cell
    this.bingoGrid[2][2] = {
      text: 'FREE',
      points: 0,
      flipped: true
    };
  }

  flipCell(cell: BingoCell): void {
    if (cell.flipped) return;

    cell.flipped = true;
    this.totalPoints += cell.points;

    if (this.checkBingo()) {
      this.totalPoints += this.bingoBonus;
    }
  }

  checkBingo(): boolean {
    const size = this.gridSize;

    // Rows & columns
    for (let i = 0; i < size; i++) {
      if (
        this.bingoGrid[i].every(c => c.flipped) ||
        this.bingoGrid.every(row => row[i].flipped)
      ) {
        return true;
      }
    }

    // Diagonals
    return (
      this.bingoGrid.every((row, i) => row[i].flipped) ||
      this.bingoGrid.every((row, i) => row[size - 1 - i].flipped)
    );
  }

  // trackBy helpers
  trackByRow(index: number): number {
    return index;
  }

  trackByCell(index: number): number {
    return index;
  }
}
