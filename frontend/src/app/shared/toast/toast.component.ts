import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ToastService } from '../toast.service';

@Component({
  selector: 'app-toast-container',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="toast-stack">
      <div
        *ngFor="let t of toastService.toasts()"
        class="toast"
        [class.toast--success]="t.type === 'success'"
        [class.toast--error]="t.type === 'error'"
        [class.toast--info]="t.type === 'info'"
      >
        <span>{{ t.message }}</span>
        <button class="toast__close" (click)="toastService.dismiss(t.id)">✕</button>
      </div>
    </div>
  `,
styleUrl: './toast.component.css',})
export class ToastContainerComponent {
  constructor(public toastService: ToastService) {}
}