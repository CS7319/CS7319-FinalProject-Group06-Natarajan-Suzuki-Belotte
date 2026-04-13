export interface User {
  email: string;
  isAuthenticated: boolean;
  password: string;
  username: string;
}

export interface UserProfile {
  user: User;
  firstName: string;
  lastName: string;
}
