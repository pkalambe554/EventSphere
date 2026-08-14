import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { ToastService } from '../../shared/toast.service';
import { EventItem } from '../../Events/event.model';
import { EventService } from '../../Events/event.service';

@Component({
  selector: 'app-manage-events',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './manage-events.component.html',
  styleUrl: './manage-events.component.css'
})
export class ManageEventsComponent implements OnInit {
  events: EventItem[] = [];

  constructor(private eventService: EventService, private toastService: ToastService) {}

  ngOnInit(): void {
    this.loadEvents();
  }

  loadEvents(): void {
    this.eventService.getAllEvents().subscribe({
      next: (data) => (this.events = data),
      error: () => this.toastService.show('Could not load events.', 'error')
    });
  }

  deleteEvent(event: EventItem): void {
    if (!confirm(`Delete "${event.title}"? This cannot be undone.`)) return;

    this.eventService.delete(event.event_id).subscribe({
      next: () => {
        this.toastService.show('Event deleted.', 'success');
        this.events = this.events.filter(e => e.event_id !== event.event_id);
      },
      error: (err) => this.toastService.show(err.error?.message || 'Delete failed.', 'error')
    });
  }
}