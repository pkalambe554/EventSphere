import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { EventItem } from './event.model';
import { Seat } from './Seat.model';

@Injectable({
  providedIn: 'root'
})
export class EventService { 
  constructor(private http: HttpClient) { }

  getAllEvents(): Observable<EventItem[]> {
    return this.http.get<EventItem[]>('/api/events');
  }

  getEventById(event_id: number): Observable<EventItem> {
    return this.http.get<EventItem>(`/api/events/${event_id}`);
  }

  getAvailableSeats(eventId: number): Observable<number> {
    return this.http.get<number>(`/api/events/${eventId}/available-seats`);
  }

  getSeats(eventId: number): Observable<Seat[]> {
  return this.http.get<Seat[]>(`/api/events/${eventId}/seats`);
}
}
