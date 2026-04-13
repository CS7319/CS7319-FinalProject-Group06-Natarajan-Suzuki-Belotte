import { Component, inject } from '@angular/core';
import { RouterModule } from '@angular/router';
import { ProfileService } from '../../features/profile/profile.service';
import { User } from '../../features/profile/profile.data';

@Component({
  selector: 'app-page-header',
  imports: [RouterModule],
  templateUrl: './page-header.html',
  styleUrl: './page-header.scss',
})
export class PageHeader {
  private readonly profileService = inject(ProfileService);

  user: User = {
    email: '',
    isAuthenticated: false,
    password: '',
    username: '',
  };

  logIn() {
    this.profileService.login(this.user).subscribe((user) => (this.user = user));
  }

  logOut() {
    this.user = {} as User;
  }

  signUp() {
    this.profileService.signUp(this.user).subscribe((user) => (this.user = user));
  }
}
