import { Injectable } from '@angular/core';
import { LoginService } from '../components/login/login.service';
import { AngularFireAuth } from 'angularfire2/auth';
import { first } from 'rxjs/operators';
import { FirebaseAuth } from 'angularfire2';

@Injectable({
  providedIn: 'root'
})
export class AuthService {

  authUser: firebase.User;

  constructor(public userService: LoginService, private afAuth: AngularFireAuth) {
    this.afAuth.authState.subscribe((auth) => {
      this.authUser = auth;
    })
  }

  isAuthenticated(){
    return this.authUser !== null;
  }
}
