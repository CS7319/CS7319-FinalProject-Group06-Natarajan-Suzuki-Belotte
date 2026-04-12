import { Routes } from '@angular/router';
import { Profile } from './features/profile/profile';
import { Search } from './features/search/search';
import { SocialEvent } from './features/social-event/social-event';

export const routes: Routes = [
  {path: 'event', component: SocialEvent},
  {path: 'profile', component: Profile},
  {path: 'search', component: Search},
  {path: '', redirectTo: 'search', pathMatch: 'full'},
];
