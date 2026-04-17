import { Component, inject, OnInit, signal } from '@angular/core';
import { Router, RouterModule } from '@angular/router';
import { ProfileService } from '../../features/profile/profile.service';
import { UserAccount, UserInfo } from '../../features/profile/profile.data';
import { FormControl, ReactiveFormsModule, Validators } from '@angular/forms';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-page-header',
  imports: [CommonModule, ReactiveFormsModule, RouterModule],
  templateUrl: './page-header.html',
  styleUrl: './page-header.scss',
})
export class PageHeader implements OnInit {
  private readonly profileService = inject(ProfileService);
  private readonly router = inject(Router);

  userAccount: UserAccount = {} as UserAccount;
  userInfo = signal<UserInfo>({ avatar: '', email: '', location: '', name: '' });

  ngOnInit(): void {
    this.userInfo.set(JSON.parse(localStorage.getItem('user') || '{}'));
    this.userAccount.email = new FormControl('', [Validators.email, Validators.required]);
    this.userAccount.password = new FormControl('', [Validators.required]);
  }

  OnLogOutClicked() {
    localStorage.removeItem('user');

    this.userInfo.set({ avatar: '', email: '', location: '', name: '' });
    this.router.navigate(['/']);
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
    this.profileService.login(this.userAccount).subscribe({
      error: () => {
        this.userInfo.set({ avatar: '', email: 'hello@smu.edu', location: '', name: '' });
        console.log(this.userInfo);
      },
      next: this.handleUserInfo,
    });
  }

  onSignUpClicked() {
    document.querySelector('#signupModal')?.classList.add('is-active');
  }

  onSignUpFormSubmitted() {
    if (!this.userAccount.email.valid || !this.userAccount.password.valid) return;

    this.onCloseClicked();
    this.profileService.signUp(this.userAccount).subscribe({
      error: () => {
        this.userInfo.set({ avatar: '', email: 'hello@smu.edu', location: '', name: '' });
        console.log(this.userInfo);
      },
      next: this.handleUserInfo,
    });
  }

  private handleUserInfo(user: UserInfo) {
    if (!user.email) return;

    localStorage.setItem('user', JSON.stringify(user));
    this.userInfo.set(user);
  }
}
