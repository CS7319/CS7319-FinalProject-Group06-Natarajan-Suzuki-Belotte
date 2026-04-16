import { Component, ElementRef, inject, OnInit, ViewChild } from '@angular/core';
import { RouterModule } from '@angular/router';
import { ProfileService } from '../../features/profile/profile.service';
import { UserAccount, UserInfo } from '../../features/profile/profile.data';

@Component({
  selector: 'app-page-header',
  imports: [RouterModule],
  templateUrl: './page-header.html',
  styleUrl: './page-header.scss',
})
export class PageHeader implements OnInit {
  private readonly profileService = inject(ProfileService);

  // @ViewChild('#loginModal') loginModal: HTMLElement;
  // @ViewChild('#signupModal') signupModal: HTMLElement = {} as HTMLElement;

  userAccount: UserAccount = {} as UserAccount;
  userInfo: UserInfo = {} as UserInfo;

  ngOnInit(): void {
    this.userInfo = JSON.parse(localStorage.getItem('user') || '{}');
  }

  logIn() {
    this.profileService.login(this.userAccount).subscribe(this.handleUserInfo);
  }

  logOut() {
    localStorage.removeItem('user');
  }

  onCloseClicked() {
    document.querySelector('#loginModal')?.classList.remove('is-active');
    document.querySelector('#signupModal')?.classList.remove('is-active');
  }

  onLogInClicked() {
    document.querySelector('#loginModal')?.classList.add('is-active');
  }

  onSignUpClicked() {
    document.querySelector('#signupModal')?.classList.add('is-active');
  }

  signUp() {
    this.profileService.signUp(this.userAccount).subscribe(this.handleUserInfo);
  }

  private handleUserInfo(user: UserInfo) {
    if (!user.email) return;

    this.userInfo = user;
    localStorage.setItem('user', JSON.stringify(user));
  }
}
