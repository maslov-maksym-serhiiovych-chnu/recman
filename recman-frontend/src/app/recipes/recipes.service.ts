import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Observable} from 'rxjs';

export interface RecipeCreateRequest {
  name: string;
  description: string;
}

export interface RecipeUpdateRequest {
  name?: string;
  description?: string;
}

export interface RecipeResponse {
  id: number;
  name: string;
  description: string;
}

@Injectable({
  providedIn: 'root'
})
export class RecipesService {
  private readonly API_URL = 'http://localhost:8080/api/recman/recipes';

  constructor(private http: HttpClient) {
  }

  readAll(): Observable<RecipeResponse[]> {
    return this.http.get<RecipeResponse[]>(this.API_URL, {withCredentials: true});
  }

  read(id: number): Observable<RecipeResponse> {
    return this.http.get<RecipeResponse>(`${this.API_URL}/${id}`, {withCredentials: true});
  }

  create(recipe: RecipeCreateRequest): Observable<RecipeResponse> {
    return this.http.post<RecipeResponse>(this.API_URL, recipe, {withCredentials: true});
  }

  update(id: number, recipe: RecipeUpdateRequest): Observable<void> {
    return this.http.put<void>(`${this.API_URL}/${id}`, recipe, {withCredentials: true});
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.API_URL}/${id}`, {withCredentials: true});
  }
}
