import { Component, inject, OnInit, signal } from '@angular/core';
import { Router, RouterModule } from '@angular/router';
import { ProfileService } from '../../features/profile/profile.service';
import { UserAccount, UserProfile } from '../../features/profile/profile.data';
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

  userAccount = signal<UserAccount>({} as UserAccount);
  userProfile = signal<UserProfile>({} as UserProfile);

  ngOnInit(): void {
    this.userProfile.set(JSON.parse(localStorage.getItem('user') || '{}'));
    this.userAccount.set({
      email: new FormControl('', [Validators.email, Validators.required]),
      name: new FormControl('', [Validators.required]),
      password: new FormControl('', [Validators.required]),
      role: 'ORGANIZER',
    });
  }

  OnLogOutClicked() {
    localStorage.removeItem('user');

    this.userProfile.set({} as UserProfile);
    this.router.navigate(['/']);
  }

  onCloseClicked() {
    this.userAccount().email.reset();
    this.userAccount().password.reset();

    document.querySelector('#loginModal')?.classList.remove('is-active');
    document.querySelector('#signupModal')?.classList.remove('is-active');
  }

  onLogInClicked() {
    document.querySelector('#loginModal')?.classList.add('is-active');
  }

  onLogInFormSubmitted() {
    if (!this.userAccount().email.valid || !this.userAccount().password.valid) return;

    this.profileService.logIn(this.userAccount()).subscribe({
      error: (error) => console.error('Log In failed', error),
      next: this.handleUserInfo.bind(this),
    });
    this.onCloseClicked();
  }

  onSignUpClicked() {
    document.querySelector('#signupModal')?.classList.add('is-active');
  }

  onSignUpFormSubmitted() {
    if (!this.userAccount().email.valid || !this.userAccount().password.valid) return;

    this.userAccount().role = 'ORGANIZER';
    this.profileService.signUp(this.userAccount()).subscribe({
      error: (err) => console.error('Sign Up failed', err),
      next: this.handleUserInfo.bind(this),
    });
    this.onCloseClicked();
  }

  private handleUserInfo(user: UserProfile) {
    if (!user.email) return;

    localStorage.setItem('user', JSON.stringify(user));
    this.userProfile.set(user);
  }
}
