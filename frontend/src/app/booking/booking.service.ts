import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({ providedIn: 'root' })
export class BookingService {
  constructor(private http: HttpClient) {}

  holdSeat(seatId: number): Observable<any> {
    return this.http.post<any>(`/api/bookings/hold/${seatId}`, {});
  }

  initiatePayment(bookingId: number, amount: number): Observable<any> {
  return this.http.post<any>(`/api/payments/initiate?bookingId=${bookingId}&amount=${amount}`, {});
}

simulateConfirm(paymentId: number): Observable<any> {
  return this.http.post<any>(`/api/payments/simulate-confirm/${paymentId}`, {});
}
}