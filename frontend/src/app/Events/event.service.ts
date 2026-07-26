import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { EventItem } from './event.model';

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
}
