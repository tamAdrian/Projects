import { Injectable } from '@angular/core';
import { AngularFireAuth } from 'angularfire2/auth';
import { map, switchMap } from 'rxjs/operators';
import { auth } from 'firebase';
import { AngularFireDatabase } from 'angularfire2/database';
import { of as observableOf, Observable, Subject } from 'rxjs';
import { User } from 'src/app/models/user.model';

@Injectable({
  providedIn: 'root',
})
export class LoginService {

  constructor(
    private angularFireAuth: AngularFireAuth,
    private db: AngularFireDatabase
  ) {}

  userEmail: Observable<string> = this.angularFireAuth.authState.pipe(
    map((authState) => {
      if (!authState) {
        return null;
      } else {
        return authState.email;
      }
    })
  );

  dbUser: Observable<User> = this.userEmail.pipe(
    switchMap((userEmail) => {
      if (!userEmail) {
        return observableOf(null);
      } else {
        var response = new Subject<User>();

        this.getUserByEmail(userEmail).subscribe((data) => {
          console.log("get user by email " + JSON.stringify(data[0]))
          if(data.length == 1) {
            response.next(data[0] as User);
          }
          else {
            response.next(null);
          }
        });
        return response.asObservable()
      }
    })
  );

  public login() {
    this.angularFireAuth.auth.signInWithPopup(new auth.GoogleAuthProvider());
  }

  public logout() {
    this.angularFireAuth.auth.signOut();
  }

  public getUserByEmail(email: string) {
    return this.db
      .list('/admins/', (ref) =>
        ref.orderByChild('email').equalTo(email).limitToFirst(1)
      ).valueChanges()
  }
}
