import { Component, inject } from '@angular/core';
import { ProfileService } from './profile.service';
import { UserInfo } from './profile.data';

@Component({
  selector: 'app-profile',
  imports: [],
  templateUrl: './profile.html',
  styleUrl: './profile.scss',
})
export class Profile {
  private readonly profileService = inject(ProfileService);

  userInfo: UserInfo = {} as UserInfo;

  getUserInfo() {
    this.profileService.getUserInfo({} as any).subscribe((userInfo) => (this.userInfo = userInfo));
  }
}
