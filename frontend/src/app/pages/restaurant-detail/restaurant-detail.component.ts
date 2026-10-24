import { Component, OnInit, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { RestaurantService } from '../../services/restaurant.service';
import { CartService } from '../../services/cart.service';
import { MenuItem, Restaurant } from '../../models/restaurant.model';

@Component({
  selector: 'app-restaurant-detail',
  standalone: true,
  imports: [CommonModule, RouterLink, FormsModule],
  templateUrl: './restaurant-detail.component.html',
  styleUrl: './restaurant-detail.component.css'
})
export class RestaurantDetailComponent implements OnInit {
  private route = inject(ActivatedRoute);
  private restaurantService = inject(RestaurantService);
  cart = inject(CartService);

  restaurant: Restaurant | null = null;
  loading = signal<boolean>(true);

  menuSearchQuery = '';
  vegFilter = false;
  activeCategory = 'All';
  categories: string[] = [];
  filteredMenuItems: MenuItem[] = [];

  ngOnInit(): void {
    this.route.paramMap.subscribe(params => {
      const idParam = params.get('id');
      const id = idParam ? parseInt(idParam, 10) : 1;
      this.loadRestaurant(id);
    });
  }

  loadRestaurant(id: number): void {
    this.loading.set(true);
    this.restaurantService.getRestaurant(id).subscribe({
      next: (res) => {
        this.restaurant = res;
        this.extractCategories();
        this.filterMenu();
        this.loading.set(false);
      },
      error: () => {
        this.loading.set(false);
      }
    });
  }

  extractCategories(): void {
    if (!this.restaurant?.menu) return;
    const cats = new Set<string>();
    this.restaurant.menu.forEach(item => {
      if (item.category) cats.add(item.category);
    });
    this.categories = Array.from(cats);
  }

  selectCategory(cat: string): void {
    this.activeCategory = cat;
    this.filterMenu();
  }

  toggleVegFilter(): void {
    this.vegFilter = !this.vegFilter;
    this.filterMenu();
  }

  filterMenu(): void {
    if (!this.restaurant?.menu) {
      this.filteredMenuItems = [];
      return;
    }

    let items = [...this.restaurant.menu];

    // Category filter
    if (this.activeCategory !== 'All') {
      items = items.filter(i => i.category === this.activeCategory);
    }

    // Veg filter
    if (this.vegFilter) {
      items = items.filter(i => i.isVeg);
    }

    // Search query filter
    if (this.menuSearchQuery.trim()) {
      const q = this.menuSearchQuery.toLowerCase();
      items = items.filter(i =>
        i.name.toLowerCase().includes(q) ||
        i.description.toLowerCase().includes(q) ||
        i.category.toLowerCase().includes(q)
      );
    }

    this.filteredMenuItems = items;
  }

  addToCart(item: MenuItem): void {
    if (!this.restaurant) return;
    this.cart.addItem(item, { id: this.restaurant.id, name: this.restaurant.name });
  }
}
