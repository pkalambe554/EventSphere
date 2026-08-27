import { CommonModule } from "@angular/common";
import { Component, OnInit } from "@angular/core";
import { ActivatedRoute, Router, RouterLink } from "@angular/router";
import { EventService } from "../event.service";
import { EventItem } from "../event.model";
import { BookingService } from "../../booking/booking.service";
import { AuthService } from "../../auth/auth.service";
import { Seat } from "../Seat.model";
import { ToastService } from "../../shared/toast.service";
declare var Razorpay: any;

@Component({
  selector: "app-event-detail",
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: "./event-detail.component.html",
  styleUrl: "./event-detail.component.css",
})export class EventDetailComponent implements OnInit {
  event: EventItem | null = null;
  seats: Seat[] = [];
  bookingSuccessSeatId: number | null = null;
  currentBookingId: number | null = null;
  paymentConfirmed = false;
  paymentLoading = false;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private eventService: EventService,
    private bookingService: BookingService,
    public authService: AuthService,
    public toastService: ToastService,
  ) {}

  ngOnInit(): void {
    const id = this.route.snapshot.paramMap.get("event_id");
    if (!id) return;

    this.eventService.getEventById(+id).subscribe({
      next: (data) => {
        this.event = data;
        this.refreshAvailableSeats();
      },
      error: () => (this.event = null),
    });

    this.eventService.getSeats(+id).subscribe({
      next: (data) => (this.seats = data),
      error: () => (this.seats = []),
    });
  }

  holdSeat(seat: Seat): void {
    if (seat.seatStatus !== "AVAILABLE") return;

    if (!this.authService.isLoggedIn()) {
      this.router.navigate(["/login"], { queryParams: { returnUrl: this.router.url } });
      return;
    }

    this.bookingService.holdSeat(seat.seatId).subscribe({
      next: (booking) => {
        seat.seatStatus = "LOCKED";
        this.bookingSuccessSeatId = seat.seatId;
        this.currentBookingId = booking.bookId;
        this.toastService.show("Seat held! Complete payment within 10 minutes.", "success");
      },
      error: (err) => {
        this.toastService.show(err.error?.message || "Could not hold this seat.", "error");
      },
    });
  }

  payNow(): void {
    if (!this.currentBookingId) return;

    this.paymentLoading = true;
    this.bookingService.createOrder(this.currentBookingId, 500).subscribe({
      next: (res) => {
        const options = {
          key: res.razorpayKeyId,
          amount: res.amount * 100,
          currency: "INR",
          order_id: res.razorpayOrderId,
          name: "EventSphere",
          description: "Booking Payment",
          handler: (response: any) => {
            this.toastService.show("Payment submitted. Confirming...", "info");
            this.pollBookingStatus(this.currentBookingId!);
          },
          modal: {
            ondismiss: () => {
              this.paymentLoading = false;
            },
          },
          theme: { color: "#c8841f" },
        };
        const rzp = new Razorpay(options);
        rzp.open();
      },
      error: () => {
        this.paymentLoading = false;
        this.toastService.show("Could not start payment.", "error");
      },
    });
  }

  pollBookingStatus(bookingId: number, attempts: number = 0): void {
    if (attempts >= 10) {
      this.paymentLoading = false;
      this.toastService.show("Confirmation delayed. Refresh in a moment.", "error");
      return;
    }

    setTimeout(() => {
      this.bookingService.getBooking(bookingId).subscribe({
        next: (res) => {
          if (res.status === "CONFIRMED") {
            this.paymentLoading = false;
            this.paymentConfirmed = true;
            this.toastService.show("Booking confirmed!", "success");
            this.router.navigate(["/"]);
          } else if (res.status === "FAILED") {
            this.paymentLoading = false;
            this.toastService.show("Payment failed.", "error");
          } else {
            this.pollBookingStatus(bookingId, attempts + 1);
          }
        },
        error: () => this.pollBookingStatus(bookingId, attempts + 1),
      });
    }, 2000);
  }

  refreshSeats(): void {
    if (!this.event) return;
    this.eventService.getSeats(+this.event.event_id).subscribe({
      next: (data) => (this.seats = data),
    });
  }

  refreshAvailableSeats(): void {
    if (!this.event) return;
    this.eventService.getAvailableSeats(+this.event.event_id).subscribe({
      next: (count) => {
        if (this.event) this.event.availableSeats = count;
      },
    });
  }
}