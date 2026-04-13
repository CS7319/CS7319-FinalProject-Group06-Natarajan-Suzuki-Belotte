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
    return this.httpClient.get<UserInfo>('/api/users/me', { params: { email: user.email } });
  }

  login(user: UserAccount): Observable<UserAccount> {
    return this.httpClient.post<UserAccount>('/api/users/login', user);
  }

  signUp(user: UserAccount): Observable<UserAccount> {
    return this.httpClient.post<UserAccount>('/api/users/register', user);
  }
}
