import { Component } from '@angular/core';
import { RouterModule } from '@angular/router';

@Component({
  selector: 'app-page-header',
  imports: [RouterModule],
  templateUrl: './page-header.html',
  styleUrl: './page-header.scss',
})
export class PageHeader {
  user = {
    isAuthenticated: false,
  };

  logIn() {
    this.user.isAuthenticated = true;
  }

  logOut() {
    this.user.isAuthenticated = false;
  }

  signUp() {}
}
