export interface UserAccount {
  email: string;
  isAuthenticated: boolean;
  password: string;
  username: string;
}

export interface UserInfo {
  user: UserAccount;
  firstName: string;
  lastName: string;
}
