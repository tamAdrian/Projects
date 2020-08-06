import { Component, OnInit } from '@angular/core';
import { LoginService } from './login.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css'],
})
export class LoginComponent implements OnInit {
  isAdmin: boolean;
  email: string;
  restaurantName: string;
  showSpinner:boolean = true;

  constructor(public userService: LoginService, public router: Router) {}

  ngOnInit(): void {
    this.userService.userEmail.subscribe((email) => {
      this.email = email;
    });

    this.userService.dbUser.subscribe((user) => {
      if (this.email != null && user != null) {
        console.log("Existing user with restaurant " + user.restaurant + ".");
        this.router.navigate(["home", user.restaurant]);
      }
      if (this.email != null && user == null) {
        console.log("New user.");
        this.userService.logout();
        this.router.navigateByUrl('/new-user');
      }
      this.showSpinner = false;
    });
  }
}
