import { Component, inject, OnInit } from '@angular/core';
import { ProfileService } from './profile.service';
import { UserInfo } from './profile.data';

@Component({
  selector: 'app-profile',
  imports: [],
  templateUrl: './profile.html',
  styleUrl: './profile.scss',
})
export class Profile implements OnInit {
  private readonly profileService = inject(ProfileService);

  userInfo: UserInfo = {} as UserInfo;

  ngOnInit() {
    const user = JSON.parse(localStorage.getItem('user') || '{}');

    if (user.email) {
      this.profileService.getUserInfo(user).subscribe((info) => (this.userInfo = info));
    }
  }
}
