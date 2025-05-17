import { Injectable } from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Observable, tap} from 'rxjs';

export interface LoginRequest {
  username: string;
  password: string;
}

export interface RegisterRequest {
  username: string;
  password: string;
}

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private readonly API_URL = 'http://localhost:8080/api/recman/auth';

  constructor(private http: HttpClient) {}

  login(req: LoginRequest): Observable<string> {
    return this.http.post<string>(`${this.API_URL}/login`, req, {responseType: 'text' as 'json'}).pipe(
      tap(res => localStorage.setItem('token', res))
    );
  }

  register(req: RegisterRequest): Observable<void> {
    return this.http.post<void>(`${this.API_URL}/register`, req);
  }

  logout(): void {
    localStorage.removeItem('token');
  }

  isAuthenticated(): boolean {
    return !!localStorage.getItem('token');
  }
}
