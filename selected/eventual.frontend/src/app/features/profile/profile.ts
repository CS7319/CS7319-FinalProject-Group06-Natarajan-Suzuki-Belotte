import { Component, inject, OnInit } from '@angular/core';
import { ProfileService } from './profile.service';
import { UserProfile } from './profile.data';

@Component({
  selector: 'app-profile',
  imports: [],
  templateUrl: './profile.html',
  styleUrl: './profile.scss',
})
export class Profile implements OnInit {
  private readonly profileService = inject(ProfileService);

  userProfile: UserProfile = {} as UserProfile;

  ngOnInit() {
    const user = JSON.parse(localStorage.getItem('user') || '{}');

    if (user.email) {
      this.profileService.getUserInfo(user).subscribe((profile) => (this.userProfile = profile));
    }
  }
}
