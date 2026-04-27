import { FormControl } from '@angular/forms';

export interface UserAccount {
  email: FormControl;
  name: FormControl;
  password: FormControl;
  role: string;
}

export interface UserProfile {
  about_me: string;
  category_types: string[];
  created_at: string;
  email: string;
  group_ids: string[];
  location: string;
  name: string;
  profile_picture_path: string | null;
  pronoun: string;
  role: string;
  token: string;
  updated_at: string;
}
