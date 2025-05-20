import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {catchError, map, Observable, of, tap} from 'rxjs';
import {TokenService} from './token.service';

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

  constructor(private http: HttpClient, private tokenService: TokenService) {
  }

  login(req: LoginRequest): Observable<string> {
    return this.http.post<string>(`${this.API_URL}/login`, req, {responseType: 'text' as 'json'}).pipe(
      tap(res => this.tokenService.set(res))
    );
  }

  register(req: RegisterRequest): Observable<void> {
    return this.http.post<void>(`${this.API_URL}/register`, req);
  }

  validateToken(): Observable<boolean> {
    return this.http.get<void>(`${this.API_URL}/validate`).pipe(
      map(() => true),
      catchError(() => of(false))
    );
  }

  removeToken(): void {
    this.tokenService.remove();
  }
}
