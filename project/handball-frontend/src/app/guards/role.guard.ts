import {ActivatedRouteSnapshot, CanActivateFn, Router, RouterStateSnapshot} from '@angular/router';
import {inject} from "@angular/core";
import {UserService} from "../services/user.service";
import {map} from "rxjs";

export const roleGuard: (allowedRoles: string[]) => CanActivateFn = (allowedRoles) => {
  return () => {
    const userService = inject(UserService);
    const router = inject(Router);

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
  };
};
