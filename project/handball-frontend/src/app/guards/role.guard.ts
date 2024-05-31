import {ActivatedRouteSnapshot, CanActivateFn, Router, RouterStateSnapshot} from '@angular/router';
import {inject} from "@angular/core";
import {UserService} from "../services/user.service";
import {map, Observable, of, switchMap} from "rxjs";
import {AuthenticatedGuard} from "./authenticated.guard";

export const roleGuard: (allowedRoles: string[]) => CanActivateFn = (allowedRoles) => {
  return (route: ActivatedRouteSnapshot, state: RouterStateSnapshot): Observable<boolean> => {
    const userService = inject(UserService);
    const router = inject(Router);
    const authGuard = inject(AuthenticatedGuard);

    return authGuard.canActivate(route, state).pipe(
      switchMap(isAuthenticated => {
        if (isAuthenticated) {
          return userService.getUser().pipe(
            map(user => {
              if (user && allowedRoles.includes(user.role)) {
                return true;
              } else {
                router.navigate(['/404']);
                return false;
              }
            })
          );
        } else {
          return of(false);
        }
      })
    );
  };
};
