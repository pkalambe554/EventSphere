import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { AuthResponse, LoginRequest, RegisterRequest } from './auth.models';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  
  constructor(private http: HttpClient) { }

  register(request:RegisterRequest):Observable<AuthResponse> {
    return this.http.post<AuthResponse>('/api/auth/register',request);   
  }

  login(request:LoginRequest):Observable<AuthResponse> {
    return this.http.post<AuthResponse>('/api/auth/login',request);   
  }

  saveToken(token:string){
    localStorage.setItem('accessToken',token);
  }

  saveRole(role:string){
    localStorage.setItem('userRole',role);
  }
  getRole(){
    return localStorage.getItem('userRole');
  }
  getToken(){
   return localStorage.getItem('accessToken');
  }

isLoggedIn(): boolean {
  return this.getToken() !== null;
}

isAdmin(): boolean {
  return this.getRole() === 'ADMIN';
}

logout(): void {
  localStorage.removeItem('accessToken');
    localStorage.removeItem('userRole');

}

}
