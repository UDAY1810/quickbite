import { Component, OnInit, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { RestaurantService } from '../../services/restaurant.service';
import { Restaurant } from '../../models/restaurant.model';

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [CommonModule, RouterLink, FormsModule],
  templateUrl: './home.component.html',
  styleUrl: './home.component.css'
})
export class HomeComponent implements OnInit {
  private restaurantService = inject(RestaurantService);

  allRestaurants: Restaurant[] = [];
  filteredRestaurants: Restaurant[] = [];
  loading = signal<boolean>(true);

  searchQuery = '';
  selectedCuisine = 'All';
  vegOnly = false;
  fastDeliveryOnly = false;
  highRatedOnly = false;
  sortBy = 'featured';

  popularTags = ['Double Cheeseburger', 'Neapolitan Pizza', 'Dum Biryani', 'Sushi Roll', 'Truffle Fries', 'Acai Bowl'];

  cuisines = [
    { name: 'All', emoji: '🍽️' },
    { name: 'Burgers', emoji: '🍔' },
    { name: 'Italian', emoji: '🍕' },
    { name: 'Biryani', emoji: '🍚' },
    { name: 'Japanese', emoji: '🍣' },
    { name: 'Healthy', emoji: '🥗' },
    { name: 'Bakery', emoji: '🥐' },
    { name: 'Coffee', emoji: '☕' }
  ];

  ngOnInit(): void {
    this.loadRestaurants();
  }

  loadRestaurants(): void {
    this.loading.set(true);
    this.restaurantService.getRestaurants().subscribe({
      next: (list) => {
        this.allRestaurants = list;
        this.applyFilters();
        this.loading.set(false);
      },
      error: () => {
        this.loading.set(false);
      }
    });
  }

  onSearchChange(): void {
    this.applyFilters();
  }

  clearSearch(): void {
    this.searchQuery = '';
    this.applyFilters();
  }

  selectTag(tag: string): void {
    this.searchQuery = tag;
    this.applyFilters();
  }

  filterByCuisine(cuisine: string): void {
    this.selectedCuisine = cuisine;
    this.applyFilters();
  }

  toggleVegOnly(): void {
    this.vegOnly = !this.vegOnly;
    this.applyFilters();
  }

  toggleFastDelivery(): void {
    this.fastDeliveryOnly = !this.fastDeliveryOnly;
    this.applyFilters();
  }

  toggleHighRated(): void {
    this.highRatedOnly = !this.highRatedOnly;
    this.applyFilters();
  }

  resetFilters(): void {
    this.searchQuery = '';
    this.selectedCuisine = 'All';
    this.vegOnly = false;
    this.fastDeliveryOnly = false;
    this.highRatedOnly = false;
    this.sortBy = 'featured';
    this.applyFilters();
  }

  applyFilters(): void {
    let list = [...this.allRestaurants];

    // Search query filter
    if (this.searchQuery.trim()) {
      const q = this.searchQuery.toLowerCase();
      list = list.filter(r =>
        r.name.toLowerCase().includes(q) ||
        r.cuisine.toLowerCase().includes(q) ||
        r.description.toLowerCase().includes(q) ||
        (r.menu && r.menu.some(m => m.name.toLowerCase().includes(q) || m.category.toLowerCase().includes(q)))
      );
    }

    // Cuisine filter
    if (this.selectedCuisine !== 'All') {
      const c = this.selectedCuisine.toLowerCase();
      list = list.filter(r => r.cuisine.toLowerCase().includes(c));
    }

    // Veg filter
    if (this.vegOnly) {
      list = list.filter(r => r.menu && r.menu.some(m => m.isVeg));
    }

    // Fast delivery filter (< 25 min)
    if (this.fastDeliveryOnly) {
      list = list.filter(r => {
        if (!r.deliveryTime) return false;
        const mins = parseInt(r.deliveryTime.split('-')[0], 10);
        return mins <= 25;
      });
    }

    // High rating filter
    if (this.highRatedOnly) {
      list = list.filter(r => r.rating >= 4.8);
    }

    // Sorting
    if (this.sortBy === 'rating') {
      list.sort((a, b) => b.rating - a.rating);
    } else if (this.sortBy === 'deliveryTime') {
      list.sort((a, b) => {
        const aMins = parseInt(a.deliveryTime?.split('-')[0] || '99', 10);
        const bMins = parseInt(b.deliveryTime?.split('-')[0] || '99', 10);
        return aMins - bMins;
      });
    } else if (this.sortBy === 'priceLow') {
      list.sort((a, b) => (a.priceForTwo || 0) - (b.priceForTwo || 0));
    } else if (this.sortBy === 'priceHigh') {
      list.sort((a, b) => (b.priceForTwo || 0) - (a.priceForTwo || 0));
    }

    this.filteredRestaurants = list;
  }
}