import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { SidebarComponent } from '../sidebar/sidebar.component';
import { CreateTaskComponent } from '../mat-dialogs/create-task/create-task.component';

@Component({
  selector: 'app-task-management',
  standalone: true,
  imports: [CommonModule, SidebarComponent, MatDialogModule],
  templateUrl: './task-management.component.html',
  styleUrls: ['./task-management.component.scss']
})
export class TaskManagementComponent {

  constructor(private dialog: MatDialog) { }

  openCreateTaskDialog(): void {
    const dialogRef = this.dialog.open(CreateTaskComponent, {
      width: '500px',
      disableClose: true
    });

    dialogRef.afterClosed().subscribe((result: any) => {
      if (result) {
        console.log('Task created:', result);
        // TODO: call API + refresh task list
      }
    });
  }
}
