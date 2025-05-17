import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Observable} from 'rxjs';

export interface RecipeListItem {
  id: number;
  name: string;
}

export interface RecipeDetails {
  id?: number;
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

  readAll(): Observable<RecipeListItem[]> {
    return this.http.get<RecipeListItem[]>(this.API_URL);
  }

  readByName(name: string): Observable<RecipeDetails> {
    return this.http.get<RecipeDetails>(`${this.API_URL}/${name}`);
  }

  create(recipe: RecipeDetails): Observable<RecipeDetails> {
    return this.http.post<RecipeDetails>(this.API_URL, recipe);
  }

  update(id: number, recipe: RecipeDetails): Observable<void> {
    return this.http.put<void>(`${this.API_URL}/${id}`, recipe);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.API_URL}/${id}`);
  }
}
