import { CommonModule } from '@angular/common';
import { Component, Inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { MAT_DIALOG_DATA, MatDialogModule, MatDialogRef } from '@angular/material/dialog';
import { MatButtonModule } from '@angular/material/button';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';

@Component({
  selector: 'app-edit-reward',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    MatDialogModule,
    MatButtonModule,
    MatFormFieldModule,
    MatInputModule
  ],
  templateUrl: './edit-reward.component.html',
  styleUrl: './edit-reward.component.scss'
})
export class EditRewardComponent {
  reward: { name: string; description: string; points: number };

  constructor(
    public dialogRef: MatDialogRef<EditRewardComponent>,
    @Inject(MAT_DIALOG_DATA)
    data: { name: string; description: string; points: number }
  ) {
    this.reward = {
      name: data?.name ?? '',
      description: data?.description ?? '',
      points: data?.points ?? 0
    };
  }

  onCancel(): void {
    this.dialogRef.close();
  }

  onSave(): void {
    if (this.isFormValid()) {
      this.dialogRef.close(this.reward);
    }
  }

  private isFormValid(): boolean {
    return !!(this.reward.name && this.reward.description && this.reward.points > 0);
  }
}
