import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
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
createOrder(bookingId: number, amount: number): Observable<any> {
  return this.http.post<any>(`/api/payments/create-order?bookingId=${bookingId}&amount=${amount}`, {});
}
getBooking(bookingId: number): Observable<any> {
  return this.http.get<any>(`/api/bookings/${bookingId}`);
}
}