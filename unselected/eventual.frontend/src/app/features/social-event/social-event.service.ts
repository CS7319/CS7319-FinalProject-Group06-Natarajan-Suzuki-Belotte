import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { EventInfo, RSVPInfo } from '../../shared/data/event.data';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { ProfileService } from '../profile/profile.service';

@Injectable({
  providedIn: 'root',
})
export class SocialEventService {
  private readonly apiUrl = environment.apiUrl;
  private readonly httpClient = inject(HttpClient);
  private readonly profileService = inject(ProfileService);

  delete(eventId: string) {
    return this.httpClient.delete(`${this.apiUrl}/events/${eventId}`);
  }

  get(eventId: string): Observable<EventInfo> {
    const token = this.profileService.getUserInfo().token;
    return this.httpClient.get<EventInfo>(`${this.apiUrl}/events/${eventId}`, {
      headers: { Authorization: `Bearer ${token}` },
    });
  }

  getRSVPs(eventId: number): Observable<RSVPInfo[]> {
    const token = this.profileService.getUserInfo().token;
    return this.httpClient.get<RSVPInfo[]>(`${this.apiUrl}/events/${eventId}/rsvp`, {
      headers: { Authorization: `Bearer ${token}` },
    });
  }

  post(event: EventInfo): Observable<EventInfo> {
    return this.httpClient.post<EventInfo>(`${this.apiUrl}/events`, event);
  }

  put(event: EventInfo): Observable<EventInfo> {
    return this.httpClient.put<EventInfo>(`${this.apiUrl}/events/${event.event_id}`, event);
  }
}
