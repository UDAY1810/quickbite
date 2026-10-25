import { Component, OnInit, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RestaurantService } from '../../services/restaurant.service';
import { AuthService } from '../../services/auth.service';
import { ToastService } from '../../services/toast.service';
import { MenuItem, Restaurant, RestaurantRequest } from '../../models/restaurant.model';

interface PresetPhoto {
  label: string;
  url: string;
}

@Component({
  selector: 'app-vendor-portal',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './vendor-portal.component.html',
  styleUrl: './vendor-portal.component.css'
})
export class VendorPortalComponent implements OnInit {
  private restaurantService = inject(RestaurantService);
  auth = inject(AuthService);
  private toast = inject(ToastService);

  myRestaurants = signal<Restaurant[]>([]);
  activeView: 'outlets' | 'add' = 'outlets';

  // Photo presets for easy 1-click submission
  photoPresets: PresetPhoto[] = [
    { label: '🍕 Artisan Pizza', url: 'https://images.unsplash.com/photo-1513104890138-7c749659a591?auto=format&fit=crop&w=900&q=80' },
    { label: '🍔 Gourmet Burgers', url: 'https://images.unsplash.com/photo-1568901346375-23c9450c58cd?auto=format&fit=crop&w=900&q=80' },
    { label: '☕ Cafe & Bakery', url: 'https://images.unsplash.com/photo-1554118811-1e0d58224f24?auto=format&fit=crop&w=900&q=80' },
    { label: '🍲 Biryani & Kebabs', url: 'https://images.unsplash.com/photo-1563379091339-03b21ab4a4f8?auto=format&fit=crop&w=900&q=80' },
    { label: '🍣 Asian & Sushi', url: 'https://images.unsplash.com/photo-1579871494447-9811cf80d66c?auto=format&fit=crop&w=900&q=80' },
    { label: '🥗 Healthy Bowls', url: 'https://images.unsplash.com/photo-1540420773420-3366772f4999?auto=format&fit=crop&w=900&q=80' }
  ];

  // New restaurant form state
  newRest: RestaurantRequest = {
    name: '',
    description: '',
    address: '',
    cuisine: 'Cafe & Continental',
    rating: 4.8,
    open: true,
    imageUrl: this.photoPresets[0].url,
    deliveryTime: '25-30 mins',
    priceForTwo: 450,
    menu: []
  };

  // Temp menu item inputs
  tempItemName = '';
  tempItemPrice = 199;
  tempItemCategory = 'Mains';
  tempItemIsVeg = true;

  // Modal to inspect outlet menu
  selectedOutletForMenu: Restaurant | null = null;

  openMenuPreview(rest: Restaurant): void {
    this.selectedOutletForMenu = rest;
  }

  closeMenuPreview(): void {
    this.selectedOutletForMenu = null;
  }

  ngOnInit(): void {
    this.loadMyRestaurants();
  }

  loadMyRestaurants(): void {
    const user = this.auth.user();
    const vendorId = user?.userId || 2; // Default to demo vendor Chef Marco if not logged in
    this.restaurantService.getVendorRestaurants(vendorId).subscribe(list => {
      this.myRestaurants.set(list);
    });
  }

  selectPreset(url: string): void {
    this.newRest.imageUrl = url;
  }

  addMenuItem(): void {
    if (!this.tempItemName.trim()) {
      this.toast.show('Please enter a dish name', 'warning');
      return;
    }

    const item: MenuItem = {
      id: Date.now() + Math.floor(Math.random() * 100),
      name: this.tempItemName.trim(),
      description: 'Prepared fresh upon order by our culinary team.',
      price: Number(this.tempItemPrice) || 199,
      category: this.tempItemCategory,
      available: true,
      isVeg: this.tempItemIsVeg,
      isBestseller: (this.newRest.menu?.length || 0) === 0
    };

    this.newRest.menu = this.newRest.menu || [];
    this.newRest.menu.push(item);

    // Reset item inputs
    this.tempItemName = '';
    this.tempItemPrice = 199;
    this.toast.show(`Added "${item.name}" to menu preview!`, 'success');
  }

  removeMenuItem(index: number): void {
    this.newRest.menu?.splice(index, 1);
  }

  submitRestaurant(): void {
    if (!this.newRest.name.trim()) {
      this.toast.show('Please enter restaurant name', 'warning');
      return;
    }
    if (!this.newRest.address.trim()) {
      this.toast.show('Please enter restaurant address', 'warning');
      return;
    }

    const currentUser = this.auth.user();
    this.newRest.ownerId = currentUser?.userId || 2;
    this.newRest.ownerName = currentUser?.fullName || 'Chef Marco Rossi (Vendor)';
    this.newRest.ownerEmail = currentUser?.email || 'vendor@quickbite.com';

    // If no menu items added, add 1 default dish
    if (!this.newRest.menu || this.newRest.menu.length === 0) {
      this.newRest.menu = [
        {
          id: Date.now(),
          name: this.newRest.name + ' Signature Special',
          description: 'House specialty prepared fresh with premium ingredients.',
          price: 299,
          category: 'Mains',
          available: true,
          isVeg: true,
          isBestseller: true
        }
      ];
    }

    this.restaurantService.submitRestaurant(this.newRest).subscribe(created => {
      this.toast.show(
        `Restaurant "${created.name}" submitted! Sent to Admin for review & approval ⏳`,
        'success'
      );
      this.loadMyRestaurants();
      this.activeView = 'outlets';
      this.resetForm();
    });
  }

  resetForm(): void {
    this.newRest = {
      name: '',
      description: '',
      address: '',
      cuisine: 'Cafe & Continental',
      rating: 4.8,
      open: true,
      imageUrl: this.photoPresets[0].url,
      deliveryTime: '25-30 mins',
      priceForTwo: 450,
      menu: []
    };
    this.tempItemName = '';
  }

  loginAsDemoVendor(): void {
    this.auth.login({ email: 'vendor@quickbite.com', password: 'vendor123' }).subscribe(() => {
      this.toast.show('Logged in as Vendor Partner (Chef Marco) 🏪', 'success');
      this.loadMyRestaurants();
    });
  }

  get approvedOutlets(): number {
    return this.myRestaurants().filter(r => r.status === 'APPROVED' || !r.status).length;
  }

  get pendingOutlets(): number {
    return this.myRestaurants().filter(r => r.status === 'PENDING_APPROVAL').length;
  }
}
