import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';

/**
 * TODO: add methods here per module as the backend endpoints come online,
 * e.g. getEvents(), login(), holdSeat(). Base path is environment.apiUrl.
 */
@Injectable({ providedIn: 'root' })
export class ApiService {
  constructor(private http: HttpClient) {}
}
