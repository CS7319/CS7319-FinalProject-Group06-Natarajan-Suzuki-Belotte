import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { UserAccount, UserInfo } from './profile.data';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class ProfileService {
  private readonly httpClient = inject(HttpClient);

  getUserInfo(user: UserAccount): Observable<UserInfo> {
    return this.httpClient.get<UserInfo>('/api/users/me', { params: { email: user.email.value } });
  }

  login(user: UserAccount): Observable<UserInfo> {
    return this.httpClient.post<UserInfo>('/api/users/login', {
      email: user.email.value,
      password: user.password.value,
    });
  }

  signUp(user: UserAccount): Observable<UserInfo> {
    return this.httpClient.post<UserInfo>('/api/users/register', {
      email: user.email.value,
      password: user.password.value,
    });
  }
}
