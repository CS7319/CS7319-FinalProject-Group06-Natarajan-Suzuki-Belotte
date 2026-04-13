import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { SocialEvent, SocialGroup } from '../../shared/data/event.data';

@Injectable({
  providedIn: 'root',
})
export class SearchService {
  private readonly httpClient = inject(HttpClient);

  getEvents(query: string): Observable<SocialEvent[]> {
    return this.httpClient.get<SocialEvent[]>('/api/search/events', { params: { query } });
  }

  getGroups(query: string): Observable<SocialGroup[]> {
    return this.httpClient.get<SocialGroup[]>('/api/search/groups', { params: { query } });
  }
}
