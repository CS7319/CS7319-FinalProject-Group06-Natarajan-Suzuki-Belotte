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

  getUserInfo(): UserProfile {
    return JSON.parse(localStorage.getItem('user') || '{}');
  }

  logIn(user: UserAccount): Observable<UserProfile> {
    return this.httpClient.post<UserProfile>(`${this.apiUrl}/users/login`, {
      email: user.email.value,
      password: user.password.value,
    });
  }

  signUp(user: UserAccount): Observable<UserProfile> {
    const formData = new FormData();

    formData.append('email', user.email.value);
    formData.append('name', user.name.value);
    formData.append('password', user.password.value);
    formData.append('role', user.role);

    return this.httpClient.post<UserProfile>(`${this.apiUrl}/users/register`, formData);
  }
}
