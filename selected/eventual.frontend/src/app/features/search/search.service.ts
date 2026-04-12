import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { SocialEvent } from '../../shared/data/social-event.data';

@Injectable({
  providedIn: 'root',
})
export class SearchService {
  private readonly httpClient = inject(HttpClient);

  get(query: string): Observable<SocialEvent[]> {
    return this.httpClient.get<SocialEvent[]>('/api/events', { params: { query } });
  }
}
