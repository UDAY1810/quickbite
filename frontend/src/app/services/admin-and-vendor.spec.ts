import { TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting, HttpTestingController } from '@angular/common/http/testing';
import { firstValueFrom } from 'rxjs';
import { AuthService } from './auth.service';
import { RestaurantService } from './restaurant.service';
import { RestaurantRequest } from '../models/restaurant.model';

describe('Admin Approval & Vendor Onboarding Workflow', () => {
  let authService: AuthService;
  let restaurantService: RestaurantService;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    localStorage.clear();
    TestBed.configureTestingModule({
      providers: [
        AuthService,
        RestaurantService,
        provideHttpClient(),
        provideHttpClientTesting()
      ]
    });

    authService = TestBed.inject(AuthService);
    restaurantService = TestBed.inject(RestaurantService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
    localStorage.clear();
  });

  it('should allow vendor to submit restaurant in PENDING_APPROVAL state, hidden from customer marketplace', async () => {
    const submission: RestaurantRequest = {
      name: 'Bella Napoli Cafe',
      description: 'Authentic Neapolitan sourdough pizzas and espresso.',
      address: 'Indiranagar 100ft Road, Bengaluru',
      cuisine: 'Italian · Pizza · Cafe',
      rating: 4.8,
      open: true,
      imageUrl: 'https://images.unsplash.com/photo-1513104890138-7c749659a591',
      ownerId: 2,
      ownerName: 'Chef Marco Rossi (Vendor)',
      ownerEmail: 'vendor@quickbite.com',
      menu: [
        { id: 1, name: 'Margherita D.O.P', description: 'San Marzano tomatoes, buffalo mozzarella', price: 399, category: 'Pizzas', available: true, isVeg: true }
      ]
    };

    // Vendor submits restaurant
    const submitted = await firstValueFrom(restaurantService.submitRestaurant(submission));
    expect(submitted.status).toBe('PENDING_APPROVAL');
    expect(submitted.name).toBe('Bella Napoli Cafe');

    // Customer marketplace should NOT include Bella Napoli Cafe while it is pending!
    const customerMarketplacePromise = firstValueFrom(restaurantService.getRestaurants());
    const req = httpMock.expectOne(req => req.url.includes('/api/restaurants'));
    req.error(new ProgressEvent('error'), { status: 0, statusText: 'Offline' });

    const liveRestaurants = await customerMarketplacePromise;
    const found = liveRestaurants.find(r => r.name === 'Bella Napoli Cafe');
    expect(found).toBeUndefined(); // Must NOT be visible to customers!
  });

  it('should allow Admin to approve pending restaurant and make it immediately live for customers', async () => {
    // 1. Submit restaurant
    const submission: RestaurantRequest = {
      name: 'Tokyo Ramen Bar',
      description: 'Handcrafted ramen bowls with slow simmered broth.',
      address: 'Koramangala 5th Block, Bengaluru',
      cuisine: 'Japanese · Ramen · Asian',
      rating: 4.9,
      open: true,
      imageUrl: 'https://images.unsplash.com/photo-1569718212165-3a8278d5f624',
      ownerId: 2
    };

    const submitted = await firstValueFrom(restaurantService.submitRestaurant(submission));
    expect(submitted.status).toBe('PENDING_APPROVAL');

    // 2. Admin approves the restaurant
    const approved = await firstValueFrom(restaurantService.approveRestaurant(submitted.id));
    expect(approved.status).toBe('APPROVED');

    // 3. Customer marketplace now includes Tokyo Ramen Bar!
    const customerMarketplacePromise = firstValueFrom(restaurantService.getRestaurants());
    const req = httpMock.expectOne(req => req.url.includes('/api/restaurants'));
    req.error(new ProgressEvent('error'), { status: 0, statusText: 'Offline' });

    const liveRestaurants = await customerMarketplacePromise;
    const found = liveRestaurants.find(r => r.id === submitted.id);
    expect(found).toBeDefined();
    expect(found?.name).toBe('Tokyo Ramen Bar');
    expect(found?.status).toBe('APPROVED');
  });

  it('should allow Admin to reject restaurant with a feedback reason', async () => {
    const submission: RestaurantRequest = {
      name: 'Unverified Quick Foods',
      description: 'Quick fast food stall',
      address: 'Unknown Street',
      cuisine: 'Fast Food',
      rating: 3.5,
      open: true,
      imageUrl: ''
    };

    const submitted = await firstValueFrom(restaurantService.submitRestaurant(submission));
    const rejected = await firstValueFrom(
      restaurantService.rejectRestaurant(submitted.id, 'FSSAI License missing and incomplete menu')
    );

    expect(rejected.status).toBe('REJECTED');
    expect(rejected.rejectionReason).toBe('FSSAI License missing and incomplete menu');

    // Should NOT appear in customer marketplace
    const customerMarketplacePromise = firstValueFrom(restaurantService.getRestaurants());
    const req = httpMock.expectOne(req => req.url.includes('/api/restaurants'));
    req.error(new ProgressEvent('error'), { status: 0, statusText: 'Offline' });

    const liveRestaurants = await customerMarketplacePromise;
    expect(liveRestaurants.some(r => r.id === submitted.id)).toBe(false);
  });

  it('should list all registered vendors and customers for Admin directory', () => {
    const vendors = authService.getVendors();
    const customers = authService.getCustomers();

    expect(vendors.length).toBeGreaterThanOrEqual(1);
    expect(vendors.some(v => v.role === 'RESTAURANT_OWNER' && v.email === 'vendor@quickbite.com')).toBe(true);

    expect(customers.length).toBeGreaterThanOrEqual(1);
    expect(customers.some(c => c.role === 'CUSTOMER' && c.email === 'uday@test.com')).toBe(true);
  });
});
