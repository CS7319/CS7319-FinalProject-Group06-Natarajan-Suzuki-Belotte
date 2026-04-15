import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { EventInfo, EventGroup } from '../../shared/data/event.data';

@Injectable({
  providedIn: 'root',
})
export class SearchService {
  private readonly httpClient = inject(HttpClient);

  getEvents(query: string): Observable<EventInfo[]> {
    return this.httpClient.get<EventInfo[]>('/api/search/events', { params: { query } });
  }

  getGroups(query: string): Observable<EventGroup[]> {
    return this.httpClient.get<EventGroup[]>('/api/search/groups', { params: { query } });
  }
}
