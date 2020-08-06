import { Component, OnInit, ViewChild } from '@angular/core';
import { LoginService } from '../login/login.service';
import { Router } from '@angular/router';
import { RestaurantPageComponent } from '../restaurant-page/restaurant-page.component';
import { RestaurantMenuComponent } from '../restaurant-menu/restaurant-menu.component';

@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.css']
})
export class HomeComponent implements OnInit {

  constructor(private service: LoginService, private router: Router) { }

  @ViewChild(RestaurantPageComponent) firstFormGroup: RestaurantPageComponent;
  @ViewChild(RestaurantMenuComponent) secondFormGroup: RestaurantMenuComponent;

  get frmStepOne() {
    return this.firstFormGroup ? this.firstFormGroup.frmStepOne : null;
  }

  get frmStepTwo() {
    return this.secondFormGroup ? this.secondFormGroup.frmStepTwo : null;
  }

  ngOnInit(): void {}

  public logout() {
    this.service.logout();
    this.router.navigateByUrl(''); //navigate to login screen
  }

}
