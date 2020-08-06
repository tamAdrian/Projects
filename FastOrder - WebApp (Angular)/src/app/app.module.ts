import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';

//angular firebase
import { AngularFireModule } from 'angularfire2';
import { AngularFireAuthModule, AngularFireAuth } from 'angularfire2/auth'
import { AngularFireDatabaseModule } from 'angularfire2/database'
import { AngularFireStorageModule } from "angularfire2/storage";

//material forms
import { MatStepperModule } from '@angular/material/stepper'
import { MatInputModule } from "@angular/material/input";
import { MatSelectModule } from "@angular/material/select";
import { MatButtonModule, MatButton } from "@angular/material/button";
import { MatCheckboxModule } from "@angular/material/checkbox";
import { MatChipsModule } from "@angular/material/chips";
import { MatSnackBarModule } from "@angular/material/snack-bar";
import { MatCardModule } from "@angular/material/card";
import { MatGridListModule } from '@angular/material/grid-list';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatToolbarModule } from '@angular/material/toolbar';

//material animations
import { BrowserAnimationsModule} from '@angular/platform-browser/animations'

//reactive forms
import { ReactiveFormsModule } from "@angular/forms";

//filter
import { Ng2SearchPipeModule } from 'ng2-search-filter';

import { AppRoutingModule, routingComponents } from './app-routing.module';
import { AppComponent } from './app.component';
import { environment } from 'src/environments/environment';
import { RestaurantPageComponent } from './components/restaurant-page/restaurant-page.component';
import { RestaurantMenuComponent } from './components/restaurant-menu/restaurant-menu.component';

import { FormsModule } from '@angular/forms';
import { OrdersComponent } from './components/orders/orders.component';

@NgModule({
  declarations: [
    AppComponent,
    routingComponents,
    RestaurantPageComponent,
    RestaurantMenuComponent,
    OrdersComponent
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    AngularFireModule.initializeApp(environment.firebase),
    AngularFireAuthModule,
    AngularFireDatabaseModule,
    AngularFireStorageModule,
    BrowserAnimationsModule,
    ReactiveFormsModule,
    MatStepperModule,
    MatInputModule,
    MatButtonModule,
    MatSelectModule,
    MatCheckboxModule,
    MatChipsModule,
    MatCardModule,
    MatSnackBarModule,
    Ng2SearchPipeModule,
    FormsModule,
    MatGridListModule,
    MatProgressSpinnerModule,
    MatToolbarModule
  ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule { }
