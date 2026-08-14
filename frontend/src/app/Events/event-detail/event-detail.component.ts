import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { EventService } from '../event.service';
import { EventItem } from '../event.model';
import { BookingService } from '../../booking/booking.service';
import { AuthService } from '../../auth/auth.service';
import { Seat } from '../Seat.model';
import { ToastService } from '../../shared/toast.service';

@Component({
  selector: 'app-event-detail',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl:'./event-detail.component.html',
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
paymentLoading =false;
  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private eventService: EventService,
    private bookingService: BookingService,
    public authService: AuthService,
    public toastService: ToastService,
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
     this.toastService.show('Seat held! Complete payment within 10 minutes.', 'success');

    },
    error: (err) => {
this.toastService.show(err.error?.message || 'Could not hold this seat.', 'error');    }
  });
}

payNow(): void {
  if (!this.currentBookingId) return;

  this.paymentLoading = true;
  this.bookingService.initiatePayment(this.currentBookingId, 500).subscribe({
    next: (payment) => {
      this.currentPaymentId = payment.id;
      this.bookingService.simulateConfirm(payment.id).subscribe({
        next: () => {
          this.paymentLoading = false;
          this.paymentConfirmed = true;
          this.refreshAvailableSeats();
          this.toastService.show('Booking confirmed!', 'success');

        },
        error: (err) => {
          this.paymentLoading = false;
          this.toastService.show(err.error?.message || 'Payment failed.', 'error');
        }
      });
    },
    error: (err) => {
      this.paymentLoading = false;
      this.toastService.show(err.error?.message || 'Could not start payment.', 'error');
    }
  });
}

refreshAvailableSeats(): void {
  if (!this.event) return;
  this.eventService.getAvailableSeats(this.event.event_id).subscribe({
    next: (count) => {
      if (this.event) {
        this.event.availableSeats = count;
      }
    }
  });
}
}