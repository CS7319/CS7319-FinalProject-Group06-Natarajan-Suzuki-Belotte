import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { User, UserProfile } from './profile.data';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class ProfileService {
  private readonly httpClient = inject(HttpClient);

  getUserInfo(user: User): Observable<UserProfile> {
    return this.httpClient.get<UserProfile>('/api/users/me', { params: { email: user.email } });
  }

  login(user: User): Observable<User> {
    return this.httpClient.post<User>('/api/users/login', user);
  }

  signUp(user: User): Observable<User> {
    return this.httpClient.post<User>('/api/users/register', user);
  }
}
