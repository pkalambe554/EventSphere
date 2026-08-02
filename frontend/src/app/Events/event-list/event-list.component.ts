import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { EventService } from '../event.service';
import { EventItem } from '../event.model';
import { RouterLink } from '@angular/router';

@Component({
  selector: 'app-event-list',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './event-list.component.html',
  styleUrl: './event-list.component.css'
})
export class EventListComponent implements OnInit {
  events :EventItem[]=[];
  loading =true;
   constructor( private eventService: EventService) {
   }
  ngOnInit(): void {
    this.eventService.getAllEvents().subscribe({
      next:(data)=>{
        this.events=data;
        this.loading=false;
      },
      error:()=>{
        this.loading=false;
      }
    })
  }

}
