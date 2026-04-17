import { Routes } from '@angular/router';
import { Profile } from './features/profile/profile';
import { Search } from './features/search/search';
import { authGuard } from './shared/auth-guard/auth-guard';

export const routes: Routes = [
  { path: 'profile', component: Profile, canActivate: [authGuard] },
  { path: 'search', component: Search },
  { path: '', redirectTo: 'search', pathMatch: 'full' },
];
