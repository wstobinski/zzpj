import {ActivatedRouteSnapshot, CanActivate, Router, RouterStateSnapshot} from '@angular/router';
import {Injectable} from "@angular/core";
import {AuthService} from "../services/auth.service";
import {Observable, switchMap, take, tap} from "rxjs";
import {Utils} from "../utils/utils";

@Injectable({
  providedIn: 'root'
})
export class AuthenticatedGuard implements CanActivate {
  constructor(private authService: AuthService,
              private utils: Utils,
              private router: Router) {} // Inject AuthService and UtilsService here

  canActivate(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): Promise<boolean> | Observable<boolean> | boolean {
    return this.authService.isAuthenticated.pipe(
      take(1),
      switchMap(async isAuthenticated => {
        console.log('IS AUTH ', isAuthenticated)
        if (!isAuthenticated) {
          isAuthenticated = await this.authService.manualLoginCheck();
          if (!isAuthenticated) {
            this.utils.presentAlertToast(`You need to login to access ${route.url} page`);
          }
        }
        return isAuthenticated;
      }),
      tap(isAuthenticated => {
        if (!isAuthenticated) {
          const navigationExtras: { returnUrl: string } = {
            returnUrl: state.url // You can pass any additional data here
          };
          this.router.navigate(['login', navigationExtras]);
        }
      })
    );
  }
}
