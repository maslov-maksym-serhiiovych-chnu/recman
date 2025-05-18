import {HttpInterceptorFn} from '@angular/common/http';
import {inject} from '@angular/core';
import {TokenService} from './token.service';
import {Router} from '@angular/router';
import {catchError, throwError} from 'rxjs';

export const authInterceptor: HttpInterceptorFn = (req, next) => {
  const tokenService = inject(TokenService);
  const router = inject(Router);

  const token = tokenService.get();

  const isPublic = req.url.includes('/auth/login') || req.url.includes('/auth/register');

  const modifiedReq = !token || isPublic ? req : req.clone({
    setHeaders: {Authorization: `Bearer ${token}`}
  });

  return next(modifiedReq).pipe(
    catchError((error) => {
      if (error.status === 401) {
        tokenService.remove();
        router.navigate(['/landing']).then();
      }

      return throwError(() => error);
    })
  );
};
