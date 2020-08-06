import { Injectable } from '@angular/core';
import { CanActivate, ActivatedRouteSnapshot, RouterStateSnapshot, UrlTree, Router } from '@angular/router';
import { Observable } from 'rxjs';
import { AuthService } from './auth.service';
import { AngularFireAuth } from 'angularfire2/auth';
import { isNullOrUndefined } from 'util';
import 'rxjs/add/operator/map';

@Injectable({
  providedIn: 'root'
})
export class AuthGuard implements CanActivate {

  constructor(private authService: AuthService, private router: Router, private afAuth: AngularFireAuth) {}

  canActivate(
    next: ActivatedRouteSnapshot,
    state: RouterStateSnapshot): Observable<boolean> | boolean {
      if(this.authService.authUser) { 
        console.log("ACCES PERMIS");
        return true;
      }
      this.router.navigateByUrl('/new-user');
      console.log("ACCES NEPERMIS");
      return false;
   }
  
}
