import { Component, inject } from '@angular/core';
import { ProfileService } from './profile.service';
import { UserProfile } from './profile.data';

@Component({
  selector: 'app-profile',
  imports: [],
  templateUrl: './profile.html',
  styleUrl: './profile.scss',
})
export class Profile {
  private readonly profileService = inject(ProfileService);

  userProfile: UserProfile = {} as UserProfile;

  getUserInfo() {
    this.profileService
      .getUserInfo({} as any)
      .subscribe((userProfile) => (this.userProfile = userProfile));
  }
}
