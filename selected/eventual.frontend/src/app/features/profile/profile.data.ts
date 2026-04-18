import { FormControl } from '@angular/forms';

export interface UserAccount {
  email: FormControl;
  password: FormControl;
}

export interface UserInfo {
  avatar: string;
  email: string;
  location: string;
  name: string;
}
