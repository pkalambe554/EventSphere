import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { ToastService } from '../../shared/toast.service';
import { EventService } from '../../Events/event.service';

@Component({
  selector: 'app-create-event',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './create-event.component.html',
  styleUrl: './create-event.component.css'
})
export class CreateEventComponent {
  form: FormGroup;
  loading = false;
  editingId:number | null=null;

  constructor(
    private fb: FormBuilder,
    private eventService: EventService,
    private toastService: ToastService,
    private router: Router,
    private route: ActivatedRoute

  ) {
    this.form = this.fb.group({
      title: ['', Validators.required],
      description: ['', Validators.required],
      venue: ['', Validators.required],
      category: ['', Validators.required],
      eventDateTime: ['', Validators.required],
      totalSeats: [1, [Validators.required, Validators.min(1)]]
    });
  }
  ngOnInit(): void {
    const id=this.route.snapshot.paramMap.get('id');
    if(id){
       this.editingId = +id;
      this.eventService.getEventById(+id).subscribe(event =>{
        this.form.patchValue(event);
        this.form.get('totalSeats')?.disable();
      })
    }
  }

    onSubmit(): void {
    if (this.form.invalid) return;
    this.loading = true;

    const request = this.editingId
      ? this.eventService.update(this.editingId, this.form.getRawValue())
      : this.eventService.create(this.form.value);

    request.subscribe({
      next: (event) => {
        this.loading = false;
        this.toastService.show(this.editingId ? 'Event updated!' : 'Event created!', 'success');
        this.router.navigate(['/admin/manage-events']);
      },
      error: (err) => {
        this.loading = false;
        this.toastService.show(err.error?.message || 'Something went wrong.', 'error');
      }
    });
  }
}