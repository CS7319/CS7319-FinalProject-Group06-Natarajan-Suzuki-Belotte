import { Component, ElementRef, inject, OnInit, ViewChild } from '@angular/core';
import { RouterModule } from '@angular/router';
import { ProfileService } from '../../features/profile/profile.service';
import { UserAccount, UserInfo } from '../../features/profile/profile.data';
import { FormControl, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';

@Component({
  selector: 'app-page-header',
  imports: [ReactiveFormsModule, RouterModule],
  templateUrl: './page-header.html',
  styleUrl: './page-header.scss',
})
export class PageHeader implements OnInit {
  private readonly profileService = inject(ProfileService);

  userAccount: UserAccount = {} as UserAccount;
  userInfo: UserInfo = {} as UserInfo;

  ngOnInit(): void {
    this.userInfo = JSON.parse(localStorage.getItem('user') || '{}');
    this.userAccount.email = new FormControl('', [Validators.email, Validators.required]);
    this.userAccount.password = new FormControl('', [Validators.required]);
  }

  OnLogOutClicked() {
    this.userAccount = {} as UserAccount;
    this.userInfo = {} as UserInfo;

    localStorage.removeItem('user');
  }

  onCloseClicked() {
    this.userAccount.email.reset();
    this.userAccount.password.reset();

    document.querySelector('#loginModal')?.classList.remove('is-active');
    document.querySelector('#signupModal')?.classList.remove('is-active');
  }

  onLogInClicked() {
    document.querySelector('#loginModal')?.classList.add('is-active');
  }

  onLogInFormSubmitted() {
    if (!this.userAccount.email.valid || !this.userAccount.password.valid) return;

    this.onCloseClicked();
    this.profileService.login(this.userAccount).subscribe(this.handleUserInfo);
  }

  onSignUpClicked() {
    document.querySelector('#signupModal')?.classList.add('is-active');
  }

  onSignUpFormSubmitted() {
    if (!this.userAccount.email.valid || !this.userAccount.password.valid) return;

    this.onCloseClicked();
    this.profileService.signUp(this.userAccount).subscribe(this.handleUserInfo);
  }

  private handleUserInfo(user: UserInfo) {
    if (!user.email) return;

    this.userInfo = user;
    localStorage.setItem('user', JSON.stringify(user));
  }
}
