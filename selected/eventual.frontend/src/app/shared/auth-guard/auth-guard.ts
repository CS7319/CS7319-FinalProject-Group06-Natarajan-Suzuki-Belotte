import { CanActivateFn } from '@angular/router';

export const authGuard: CanActivateFn = (route, state) => {
  if (state.url === 'search' || localStorage.getItem('user')) return true;

  return false;
};
