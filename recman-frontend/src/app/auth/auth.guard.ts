import {CanActivateFn, Router} from '@angular/router';
import {inject} from '@angular/core';
import {AuthService} from './auth.service';
import {tap} from 'rxjs';

export const authGuard: CanActivateFn = () => {
  const router = inject(Router);
  const authService = inject(AuthService);

  return authService.validateToken().pipe(
    tap((isValid) => {
      if (!isValid) {
        authService.removeToken();
        router.navigate(['/landing']).then();
      }
    })
  );
};
