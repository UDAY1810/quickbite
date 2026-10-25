import { TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting, HttpTestingController } from '@angular/common/http/testing';
import { firstValueFrom } from 'rxjs';
import { AuthService } from './auth.service';
import { OrderService } from './order.service';
import { environment } from '../../environments/environment';

describe('AuthService and OrderService Account & Order Isolation', () => {
  let authService: AuthService;
  let orderService: OrderService;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    localStorage.clear();
    TestBed.configureTestingModule({
      providers: [
        AuthService,
        OrderService,
        provideHttpClient(),
        provideHttpClientTesting()
      ]
    });

    authService = TestBed.inject(AuthService);
    orderService = TestBed.inject(OrderService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
    localStorage.clear();
  });

  it('should REJECT login when typing a false or wrong email and password', async () => {
    const loginPromise = firstValueFrom(
      authService.login({ email: 'fakeuser@unknown.com', password: 'randompassword123' })
    );

    const req = httpMock.expectOne(`${environment.apiUrl}/api/auth/login`);
    req.error(new ProgressEvent('error'), { status: 0, statusText: 'Unknown Error' });

    try {
      await loginPromise;
      throw new Error('Should NOT have resolved successfully!');
    } catch (err: any) {
      expect(err.status).toBe(401);
      expect(err.error?.message).toBe('Wrong email or password');
      expect(authService.isLoggedIn()).toBe(false);
    }
  });

  it('should REJECT login for demo user with wrong password', async () => {
    const loginPromise = firstValueFrom(
      authService.login({ email: 'uday@test.com', password: 'incorrectpassword' })
    );

    const req = httpMock.expectOne(`${environment.apiUrl}/api/auth/login`);
    req.error(new ProgressEvent('error'), { status: 0, statusText: 'Unknown Error' });

    try {
      await loginPromise;
      throw new Error('Should NOT have resolved successfully!');
    } catch (err: any) {
      expect(err.status).toBe(401);
      expect(err.error?.message).toBe('Wrong email or password');
      expect(authService.isLoggedIn()).toBe(false);
    }
  });

  it('should ALLOW login for demo user with correct password', async () => {
    const loginPromise = firstValueFrom(
      authService.login({ email: 'uday@test.com', password: 'password123' })
    );

    const req = httpMock.expectOne(`${environment.apiUrl}/api/auth/login`);
    req.error(new ProgressEvent('error'), { status: 0, statusText: 'Unknown Error' });

    const res = await loginPromise;
    expect(res.userId).toBe(1);
    expect(res.fullName).toBe('Uday Dubey');
    expect(authService.isLoggedIn()).toBe(true);
  });

  it('should register a new account with a unique userId and NOT show another customer orders', async () => {
    // 1. Register new user
    const regPromise = firstValueFrom(
      authService.register({
        email: 'priya@test.com',
        password: 'password456',
        fullName: 'Priya Sharma',
        phone: '+91 99887 76655'
      })
    );

    const regReq = httpMock.expectOne(`${environment.apiUrl}/api/auth/register`);
    regReq.error(new ProgressEvent('error'), { status: 0, statusText: 'Offline' });

    const regRes = await regPromise;
    expect(regRes.userId).toBeGreaterThan(1);
    expect(regRes.fullName).toBe('Priya Sharma');
    expect(authService.user()?.userId).toBe(regRes.userId);

    // 2. Query orders for Priya
    const ordersPromise = firstValueFrom(orderService.getMyOrders());
    const ordersReq = httpMock.expectOne(`${environment.apiUrl}/api/orders/my`);
    ordersReq.error(new ProgressEvent('error'), { status: 0, statusText: 'Offline' });

    const orders = await ordersPromise;
    // Priya should have ZERO orders. Another customer's orders MUST NOT appear!
    expect(orders.length).toBe(0);
  });

  it('should prevent logging in to a registered account with WRONG password', async () => {
    // 1. Register account
    const regPromise = firstValueFrom(
      authService.register({
        email: 'rahul@test.com',
        password: 'mypassword999',
        fullName: 'Rahul Verma'
      })
    );
    const regReq = httpMock.expectOne(`${environment.apiUrl}/api/auth/register`);
    regReq.error(new ProgressEvent('error'), { status: 0, statusText: 'Offline' });
    await regPromise;

    authService.logout();

    // 2. Try logging in with WRONG password
    const loginPromise = firstValueFrom(
      authService.login({ email: 'rahul@test.com', password: 'wrongpassword' })
    );
    const loginReq = httpMock.expectOne(`${environment.apiUrl}/api/auth/login`);
    loginReq.error(new ProgressEvent('error'), { status: 0, statusText: 'Offline' });

    try {
      await loginPromise;
      throw new Error('Should NOT have logged in!');
    } catch (err: any) {
      expect(err.status).toBe(401);
      expect(err.error?.message).toBe('Wrong email or password');
      expect(authService.isLoggedIn()).toBe(false);
    }
  });

  it('should isolate orders between two different customers', async () => {
    // Customer 1 registers
    const reg1Promise = firstValueFrom(
      authService.register({
        email: 'cust1@test.com',
        password: 'pass1',
        fullName: 'Customer One'
      })
    );
    const reg1Req = httpMock.expectOne(`${environment.apiUrl}/api/auth/register`);
    reg1Req.error(new ProgressEvent('error'), { status: 0, statusText: 'Offline' });
    const user1 = await reg1Promise;

    // Customer 1 places an order
    const placePromise = firstValueFrom(
      orderService.placeOrder({
        restaurantId: 10,
        deliveryAddress: 'Flat 101, Test St',
        items: [{ menuItemId: 101, quantity: 2 }]
      })
    );
    const placeReq = httpMock.expectOne(`${environment.apiUrl}/api/orders`);
    placeReq.error(new ProgressEvent('error'), { status: 0, statusText: 'Offline' });
    const placedOrder = await placePromise;
    expect(placedOrder.customerId).toBe(user1.userId);

    // Customer 1 logs out
    authService.logout();

    // Customer 2 registers
    const reg2Promise = firstValueFrom(
      authService.register({
        email: 'cust2@test.com',
        password: 'pass2',
        fullName: 'Customer Two'
      })
    );
    const reg2Req = httpMock.expectOne(`${environment.apiUrl}/api/auth/register`);
    reg2Req.error(new ProgressEvent('error'), { status: 0, statusText: 'Offline' });
    await reg2Promise;

    // Customer 2 fetches their orders
    const cust2OrdersPromise = firstValueFrom(orderService.getMyOrders());
    const ordersReq = httpMock.expectOne(`${environment.apiUrl}/api/orders/my`);
    ordersReq.error(new ProgressEvent('error'), { status: 0, statusText: 'Offline' });
    const cust2Orders = await cust2OrdersPromise;

    // Customer 2 MUST have 0 orders and NEVER see Customer 1's order!
    expect(cust2Orders.length).toBe(0);
  });
});
