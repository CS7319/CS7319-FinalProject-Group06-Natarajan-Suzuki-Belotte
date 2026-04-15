import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { EventInfo } from '../../shared/data/event.data';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class SocialEventService {
  private readonly httpClient = inject(HttpClient);

  delete(eventId: string) {
    return this.httpClient.delete(`/api/events/${eventId}`);
  }

  get(eventId: string): Observable<EventInfo> {
    return this.httpClient.get<EventInfo>(`/api/events/${eventId}`);
  }

  post(event: EventInfo): Observable<EventInfo> {
    return this.httpClient.post<EventInfo>(`/api/events`, event);
  }

  put(event: EventInfo): Observable<EventInfo> {
    return this.httpClient.put<EventInfo>(`/api/events/${event.id}`, event);
  }
}
