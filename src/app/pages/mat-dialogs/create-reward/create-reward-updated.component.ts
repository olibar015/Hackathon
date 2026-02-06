import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { MatDialogRef, MatDialogModule } from '@angular/material/dialog';
import { MatButtonModule } from '@angular/material/button';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';

@Component({
  selector: 'app-create-reward',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    MatDialogModule,
    MatButtonModule,
    MatFormFieldModule,
    MatInputModule
  ],
  templateUrl: './create-reward.component.html',
  styleUrl: './create-reward.component.scss'
})
export class CreateRewardComponent {
  reward = {
    name: '',
    description: '',
    points: 0
  };

  constructor(public dialogRef: MatDialogRef<CreateRewardComponent>) {}

  onCancel(): void {
    this.dialogRef.close();
  }

  onCreate(): void {
    if (this.isFormValid()) {
      this.dialogRef.close(this.reward);
    }
  }

  private isFormValid(): boolean {
    return !!(this.reward.name && this.reward.description && this.reward.points > 0);
  }
}
