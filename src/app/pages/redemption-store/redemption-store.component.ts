import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { SidebarComponent } from '../sidebar/sidebar.component';
import { EditRewardComponent } from '../mat-dialogs/edit-reward/edit-reward.component';
import { CreateRewardComponent } from '../mat-dialogs/create-reward/create-reward.component';


export interface Reward {
  id: number;
  name: string;
  description?: string;
  category: string;
  points: number;
  originalPrice?: string;
  image?: string;
  badge?: string;
}

@Component({
  selector: 'app-redemption-store',
  standalone: true,
  imports: [CommonModule, SidebarComponent, MatDialogModule],
  templateUrl: './redemption-store.component.html',
  styleUrl: './redemption-store.component.scss'
})
export class RedemptionStoreComponent implements OnInit {
  currentPoints = 20525;
  expiryDate = '06/22';
  activeTab: 'redeem' | 'earn' | 'history' = 'redeem';
  selectedCategory = 'all';
  isSidebarOpen = false;
  role: 'admin' | 'employee' = 'employee';
  showCreateModal = false;
  editingReward: Reward | null = null;

  constructor(private dialog: MatDialog) { }

  ngOnInit(): void {
    // Get role from localStorage, same way as sidebar
    const r = localStorage.getItem('role');
    if (r === 'admin' || r === 'employee') {
      this.role = r;
    }
  }

  categories = [
    { id: 'all', name: 'All' },
    { id: 'electronics', name: 'Electronics' },
    { id: 'entertainment', name: 'Entertainment' },
    { id: 'food', name: 'Food' },
    { id: 'coupons', name: 'Coupons' }
  ];

  rewards: Reward[] = [
    {
      id: 1,
      name: 'Amazon Gift Card worth 5000 INR',
      category: 'entertainment',
      points: 1800,
      originalPrice: '₹5000',
      badge: 'Popular'
    },
    {
      id: 2,
      name: 'Buy Prime Subscription for 800 points',
      category: 'entertainment',
      points: 800,
      originalPrice: 'Prime',
      badge: 'Deal'
    },
    {
      id: 3,
      name: 'Buy Coupon Code worth $25',
      category: 'coupons',
      points: 200,
      originalPrice: '$25'
    },
    {
      id: 4,
      name: 'Get 50 INR back to wallet',
      category: 'food',
      points: 300,
      originalPrice: '₹50'
    },
    {
      id: 5,
      name: 'Redeem 10000 points to get Adidas Sneakers',
      category: 'electronics',
      points: 10000,
      originalPrice: 'Adidas',
      badge: 'Hot'
    },
    {
      id: 6,
      name: 'Spotify Premium 3 Months',
      category: 'entertainment',
      points: 1200,
      originalPrice: 'Premium',
      badge: 'New'
    },
    {
      id: 7,
      name: 'Restaurant Voucher $50',
      category: 'food',
      points: 1500,
      originalPrice: '$50'
    },
    {
      id: 8,
      name: 'Apple Watch Series 5',
      category: 'electronics',
      points: 25000,
      originalPrice: '$399',
      badge: 'Exclusive'
    }
  ];

  get filteredRewards(): Reward[] {
    if (this.selectedCategory === 'all') {
      return this.rewards;
    }
    return this.rewards.filter(r => r.category === this.selectedCategory);
  }

  selectCategory(categoryId: string): void {
    this.selectedCategory = categoryId;
  }

  switchTab(tab: 'redeem' | 'earn' | 'history'): void {
    this.activeTab = tab;
  }

  toggleSidebar(): void {
    this.isSidebarOpen = !this.isSidebarOpen;
  }

  closeSidebar(): void {
    this.isSidebarOpen = false;
  }

  redeemReward(reward: Reward): void {
    if (this.currentPoints >= reward.points) {
      this.currentPoints += reward.points;
      alert(`Successfully redeemed ${reward.name}!`);
    } else {
      alert('Insufficient points');
    }
  }

  openCreateModal(): void {
    const dialogRef = this.dialog.open(CreateRewardComponent, {
      width: '520px'
    });

    dialogRef.afterClosed().subscribe(result => {
      if (!result) {
        return;
      }
      const newReward: Reward = {
        id: Math.max(...this.rewards.map(r => r.id), 0) + 1,
        name: result.name,
        description: result.description,
        category: 'entertainment',
        points: result.points
      };
      this.rewards.push(newReward);
    });
  }

  editReward(reward: Reward): void {
    const dialogRef = this.dialog.open(EditRewardComponent, {
      width: '520px',
      data: {
        name: reward.name,
        description: reward.description ?? '',
        points: reward.points
      }
    });

    dialogRef.afterClosed().subscribe(result => {
      if (!result) {
        return;
      }
      this.rewards = this.rewards.map(r =>
        r.id === reward.id
          ? { ...r, name: result.name, description: result.description, points: result.points }
          : r
      );
    });
  }

  deleteReward(id: number): void {
    if (confirm('Are you sure you want to delete this reward?')) {
      this.rewards = this.rewards.filter(r => r.id !== id);
    }
  }

  saveReward(reward: Reward): void {
    if (this.editingReward && this.editingReward.id) {
      const index = this.rewards.findIndex(r => r.id === this.editingReward!.id);
      if (index >= 0) {
        this.rewards[index] = reward;
      }
    } else {
      reward.id = Math.max(...this.rewards.map(r => r.id), 0) + 1;
      this.rewards.push(reward);
    }
    this.closeModal();
  }

  closeModal(): void {
    this.showCreateModal = false;
    this.editingReward = null;
  }
}