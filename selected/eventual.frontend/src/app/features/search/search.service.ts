import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { EventInfo, EventGroup, EventResult } from '../../shared/data/event.data';
import { ProfileService } from '../profile/profile.service';
import { environment } from '../../../environments/environment';

@Injectable({
  providedIn: 'root',
})
export class SearchService {
  private readonly apiUrl = environment.apiUrl;
  private readonly httpClient = inject(HttpClient);
  private readonly profileService = inject(ProfileService);

  getEvents(query: string): Observable<EventResult> {
    const token = this.profileService.getUserInfo().token;
    return this.httpClient.get<EventResult>(`${this.apiUrl}/search/events`, {
      headers: { Authorization: `Bearer ${token}` },
      // params: { query: 'tech' },
    });
  }

  getGroups(): Observable<EventGroup[]> {
    const token = this.profileService.getUserInfo().token;
    return this.httpClient.get<EventGroup[]>(`${this.apiUrl}/api/search/groups`, {
      headers: { Authorization: `Bearer ${token}` },
    });
  }
}
