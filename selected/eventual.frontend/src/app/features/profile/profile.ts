import { Component, inject, OnInit, signal } from '@angular/core';
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

  userProfile = signal<UserProfile>({} as UserProfile);

  ngOnInit() {
    this.userProfile.set(this.profileService.getUserInfo());
  }
}
