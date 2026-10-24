import { Injectable, inject } from '@angular/core';
import { Client } from '@stomp/stompjs';
import SockJS from 'sockjs-client';
import { Observable, Subject } from 'rxjs';
import { environment } from '../../environments/environment';
import { LiveTrackingUpdate, OrderStatus } from '../models/order.model';
import { ToastService } from './toast.service';

@Injectable({ providedIn: 'root' })
export class TrackingService {
  private toast = inject(ToastService);
  private stompClient: Client | null = null;
  private updateSubject = new Subject<LiveTrackingUpdate>();
  private simulationTimer: any = null;

  updates$: Observable<LiveTrackingUpdate> = this.updateSubject.asObservable();

  connectToOrder(orderId: number): void {
    this.disconnect();

    try {
      this.stompClient = new Client({
        webSocketFactory: () => new SockJS(`${environment.wsUrl}/ws`),
        reconnectDelay: 5000,
        heartbeatIncoming: 4000,
        heartbeatOutgoing: 4000,
        onConnect: () => {
          console.log(`[WebSocket] Connected. Subscribing to /topic/orders/${orderId}`);
          this.toast.show('Live order radar connected 🛰️', 'info');

          this.stompClient?.subscribe(`/topic/orders/${orderId}`, message => {
            try {
              const data = JSON.parse(message.body);
              console.log('[WebSocket] Live Update Received:', data);
              this.updateSubject.next(data);
            } catch (e) {
              console.error('[WebSocket] Failed to parse message body', e);
            }
          });
        },
        onStompError: frame => {
          console.warn('[WebSocket] Stomp error:', frame.headers['message']);
        },
        onWebSocketClose: () => {
          console.log('[WebSocket] Connection closed');
        }
      });

      this.stompClient.activate();
    } catch (err) {
      console.warn('[WebSocket] Direct connection failed, simulation ready:', err);
    }
  }

  disconnect(): void {
    if (this.simulationTimer) {
      clearInterval(this.simulationTimer);
      this.simulationTimer = null;
    }
    if (this.stompClient) {
      try {
        this.stompClient.deactivate();
      } catch (e) {
        // ignore
      }
      this.stompClient = null;
    }
  }

  // Interactive local simulation for testing delivery trip anytime
  startSimulation(orderId: number, startingStep = 1): void {
    if (this.simulationTimer) {
      clearInterval(this.simulationTimer);
    }

    let step = startingStep;
    let lat = 12.9716;
    let lng = 77.5946;

    const driverNames = ['Vikram Singh', 'Rajesh Kumar', 'Arjun Mehta', 'Aditya Sharma'];
    const driver = driverNames[orderId % driverNames.length];

    this.toast.show('Live trip simulation started! 🛵', 'info');

    this.simulationTimer = setInterval(() => {
      lat += 0.0009;
      lng += 0.0007;
      step++;

      let status: OrderStatus = 'OUT_FOR_DELIVERY';
      if (step <= 2) {
        status = 'CONFIRMED';
      } else if (step <= 4) {
        status = 'PREPARING';
      } else if (step < 10) {
        status = 'OUT_FOR_DELIVERY';
      } else {
        status = 'DELIVERED';
      }

      const update: LiveTrackingUpdate = {
        orderId,
        status,
        driver,
        lat,
        lng,
        step,
        timestamp: new Date().toLocaleTimeString()
      };

      this.updateSubject.next(update);

      if (step >= 10) {
        clearInterval(this.simulationTimer);
        this.simulationTimer = null;
        this.toast.show('Order delivered! Enjoy your meal 🍕🎉', 'success');
      }
    }, 2800);
  }
}
