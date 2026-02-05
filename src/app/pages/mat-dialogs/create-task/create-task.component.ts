import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, Validators, ReactiveFormsModule } from '@angular/forms';
import { MatDialogRef, MatDialogModule } from '@angular/material/dialog';

@Component({
  selector: 'app-create-task',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, MatDialogModule],
  templateUrl: './create-task.component.html',
  styleUrls: ['./create-task.component.scss']
})
export class CreateTaskComponent {

  departments = ['IT', 'HR', 'Finance', 'Operations'];

  taskForm = this.fb.group({
    name: ['', Validators.required],
    description: ['', Validators.required],
    department: ['', Validators.required],
    points: [null, [Validators.required, Validators.min(1)]]
  });

  constructor(
    private fb: FormBuilder,
    private dialogRef: MatDialogRef<CreateTaskComponent>
  ) { }

  close(): void {
    this.dialogRef.close();
  }

  create(): void {
    if (this.taskForm.invalid) return;
    this.dialogRef.close(this.taskForm.value);
  }
}
