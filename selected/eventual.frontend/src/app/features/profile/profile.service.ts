import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { UserAccount, UserProfile } from './profile.data';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';

@Injectable({
  providedIn: 'root',
})
export class ProfileService {
  private readonly apiUrl = environment.apiUrl;
  private readonly httpClient = inject(HttpClient);

  getUserInfo(user: UserAccount): Observable<UserProfile> {
    return this.httpClient.get<UserProfile>(`${this.apiUrl}/users/me`, {
      params: { email: user.email.value },
    });
  }

  login(user: UserAccount): Observable<UserProfile> {
    console.log('login', user.email.value, user.password.value);
    return this.httpClient.post<UserProfile>(`${this.apiUrl}/users/login`, {
      email: user.email.value,
      password: user.password.value,
    });
  }

  signUp(user: UserAccount): Observable<UserProfile> {
    return this.httpClient.post<UserProfile>(`${this.apiUrl}/users/register`, {
      email: user.email.value,
      password: user.password.value,
    });
  }
}
