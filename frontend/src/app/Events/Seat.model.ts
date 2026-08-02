export interface Seat {
    seatId: number;
  seatNumber: string;
  seatStatus: 'AVAILABLE' | 'LOCKED' | 'BOOKED';
}