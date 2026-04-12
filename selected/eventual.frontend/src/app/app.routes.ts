import { Routes } from '@angular/router';
import { Profile } from './features/profile/profile';
import { Search } from './features/search/search';

export const routes: Routes = [
  { path: 'profile', component: Profile },
  { path: 'search', component: Search },
  { path: '', redirectTo: 'search', pathMatch: 'full' },
];
