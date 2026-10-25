import { Component, OnInit, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RestaurantService } from '../../services/restaurant.service';
import { AuthService, StoredUser } from '../../services/auth.service';
import { ToastService } from '../../services/toast.service';
import { Restaurant } from '../../models/restaurant.model';

@Component({
  selector: 'app-admin-dashboard',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './admin-dashboard.component.html',
  styleUrl: './admin-dashboard.component.css'
})
export class AdminDashboardComponent implements OnInit {
  private restaurantService = inject(RestaurantService);
  auth = inject(AuthService);
  private toast = inject(ToastService);

  activeTab: 'approvals' | 'restaurants' | 'vendors' | 'customers' = 'approvals';

  restaurants = signal<Restaurant[]>([]);
  vendors = signal<StoredUser[]>([]);
  customers = signal<StoredUser[]>([]);

  searchQuery = '';
  statusFilter: 'ALL' | 'APPROVED' | 'PENDING_APPROVAL' | 'REJECTED' = 'ALL';

  // Modal / Rejection state
  selectedRestaurant: Restaurant | null = null;
  rejectModalOpen = false;
  rejectionReason = '';

  ngOnInit(): void {
    this.refreshData();
  }

  refreshData(): void {
    this.restaurantService.getAllRestaurantsAdmin().subscribe(list => {
      this.restaurants.set(list);
    });
    this.vendors.set(this.auth.getVendors());
    this.customers.set(this.auth.getCustomers());
  }

  // Counts
  get pendingCount(): number {
    return this.restaurants().filter(r => r.status === 'PENDING_APPROVAL').length;
  }

  get approvedCount(): number {
    return this.restaurants().filter(r => r.status === 'APPROVED' || !r.status).length;
  }

  get rejectedCount(): number {
    return this.restaurants().filter(r => r.status === 'REJECTED').length;
  }

  get pendingRestaurants(): Restaurant[] {
    return this.restaurants().filter(r => r.status === 'PENDING_APPROVAL');
  }

  get filteredRestaurants(): Restaurant[] {
    return this.restaurants().filter(r => {
      const matchSearch = !this.searchQuery.trim() ||
        r.name.toLowerCase().includes(this.searchQuery.toLowerCase()) ||
        r.cuisine.toLowerCase().includes(this.searchQuery.toLowerCase()) ||
        (r.ownerName && r.ownerName.toLowerCase().includes(this.searchQuery.toLowerCase()));

      const matchStatus = this.statusFilter === 'ALL' ||
        (this.statusFilter === 'APPROVED' && (!r.status || r.status === 'APPROVED')) ||
        r.status === this.statusFilter;

      return matchSearch && matchStatus;
    });
  }

  // Actions
  approve(restaurant: Restaurant): void {
    this.restaurantService.approveRestaurant(restaurant.id).subscribe(() => {
      this.toast.show(`Approved "${restaurant.name}"! It is now live for all customers 🚀`, 'success');
      this.refreshData();
    });
  }

  openRejectModal(restaurant: Restaurant): void {
    this.selectedRestaurant = restaurant;
    this.rejectionReason = '';
    this.rejectModalOpen = true;
  }

  closeRejectModal(): void {
    this.selectedRestaurant = null;
    this.rejectModalOpen = false;
  }

  confirmReject(): void {
    if (!this.selectedRestaurant) return;
    const name = this.selectedRestaurant.name;
    const reason = this.rejectionReason || 'Did not meet QuickBite partner hygiene and verification criteria.';
    this.restaurantService.rejectRestaurant(this.selectedRestaurant.id, reason).subscribe(() => {
      this.toast.show(`Rejected submission for "${name}". Feedback logged.`, 'warning');
      this.closeRejectModal();
      this.refreshData();
    });
  }

  deleteRestaurant(restaurant: Restaurant): void {
    if (confirm(`Are you sure you want to delete "${restaurant.name}" from QuickBite catalog?`)) {
      this.restaurantService.deleteRestaurant(restaurant.id).subscribe(() => {
        this.toast.show(`Removed "${restaurant.name}" from catalog.`, 'info');
        this.refreshData();
      });
    }
  }

  toggleOpen(restaurant: Restaurant): void {
    restaurant.open = !restaurant.open;
    this.toast.show(`"${restaurant.name}" is now marked as ${restaurant.open ? 'OPEN' : 'CLOSED'}.`, 'info');
  }

  formatDate(iso?: string): string {
    if (!iso) return 'Recent';
    try {
      const d = new Date(iso);
      return d.toLocaleDateString('en-IN', {
        day: 'numeric',
        month: 'short',
        year: 'numeric'
      });
    } catch {
      return 'Recent';
    }
  }

  getVendorOutletsCount(vendorId: number): number {
    return this.restaurants().filter(r => r.ownerId === vendorId).length;
  }
}
