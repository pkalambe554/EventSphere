import { Routes } from '@angular/router';
import { HomeComponent } from './pages/home/home.component';
import { LoginComponent } from './auth/login/login.component';
import { RegisterComponent } from './auth/register/register.component';
import { EventListComponent } from './Events/event-list/event-list.component';
import { EventDetailComponent } from './Events/event-detail/event-detail.component';
import { adminGuard } from './admin/admin.guard';
import { CreateEventComponent } from './admin/create-event/create-event.component';
import { ManageEventsComponent } from './admin/manage-events/manage-events.component';

export const routes: Routes = [
  { path: '', component: EventListComponent },
  {path : 'login',component: LoginComponent},
  {path:'register',component:RegisterComponent},
  {path:'events',component:EventListComponent},
  {path:'events/:event_id',component:EventDetailComponent},
  // app.routes.ts
{ path: 'admin/create-event', component: CreateEventComponent, canActivate: [adminGuard] },
{ path: 'admin/edit-event/:id', component: CreateEventComponent, canActivate: [adminGuard] },
{ path: 'admin/manage-events', component: ManageEventsComponent, canActivate: [adminGuard] },

];
