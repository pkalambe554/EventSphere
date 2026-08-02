import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { EventService } from '../event.service';
import { EventItem } from '../event.model';
import { BookingService } from '../../booking/booking.service';
import { AuthService } from '../../auth/auth.service';
import { Seat } from '../Seat.model';

@Component({
  selector: 'app-event-detail',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './event-detail.component.html',
  styleUrl: './event-detail.component.css'
})
export class EventDetailComponent implements OnInit {
  event: EventItem | null = null;
  seats: Seat[] = [];
  bookingError = '';
  bookingSuccessSeatId: number | null = null;
 currentBookingId: number | null = null;
currentPaymentId: number | null = null;
paymentConfirmed = false;
  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private eventService: EventService,
    private bookingService: BookingService,
    public authService: AuthService
  ) {}

  ngOnInit(): void {
    const id = this.route.snapshot.paramMap.get('event_id');
    if (!id) {
      return;
    }

    this.eventService.getEventById(+id).subscribe({
      next: (data) => (this.event = data),
      error: () => (this.event = null)
    });

    this.eventService.getSeats(+id).subscribe({
      next: (data) => (this.seats = data),
      error: () => (this.seats = [])
    });
  }

 

holdSeat(seat: Seat): void {
  if (seat.seatStatus !== 'AVAILABLE') return;

  if (!this.authService.isLoggedIn()) {
    this.router.navigate(['/login'], { queryParams: { returnUrl: this.router.url } });
    return;
  }

  this.bookingError = '';
  this.bookingService.holdSeat(seat.seatId).subscribe({
    next: (booking) => {
      seat.seatStatus = 'LOCKED';
      this.bookingSuccessSeatId = seat.seatId;
      this.currentBookingId = booking.bookId;
    },
    error: (err) => {
      this.bookingError = err.error?.message || 'Could not hold this seat.';
    }
  });
}

payNow(): void {
  console.log("Current Booking ID: %s", this.currentBookingId);
  if (!this.currentBookingId) return;

  this.bookingService.initiatePayment(this.currentBookingId, 500).subscribe({
    next: (payment) => {
      this.currentPaymentId = payment.id;
      this.bookingService.simulateConfirm(payment.id).subscribe({
        next: () => (this.paymentConfirmed = true),
        error: (err) => (this.bookingError = err.error?.message || 'Payment failed.')
      });
    },
    error: (err) => (this.bookingError = err.error?.message || 'Could not start payment.')
  });
}
}