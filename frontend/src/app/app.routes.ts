import { Routes } from '@angular/router';
import { HomeComponent } from './pages/home/home.component';
import { LoginComponent } from './auth/login/login.component';
import { RegisterComponent } from './auth/register/register.component';
import { EventListComponent } from './Events/event-list/event-list.component';

export const routes: Routes = [
  { path: '', component: HomeComponent },
  {path : 'login',component: LoginComponent},
  {path:'register',component:RegisterComponent},
  {path:'events',component:EventListComponent},
  


];
