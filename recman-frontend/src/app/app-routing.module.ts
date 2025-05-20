import {LandingComponent} from './landing/landing.component';
import {LayoutComponent} from './layout/layout.component';
import {RouterModule, Routes} from '@angular/router';
import {authGuard} from './auth/auth.guard';
import {NgModule} from '@angular/core';

const routes: Routes = [
  {
    path: 'auth',
    loadChildren: () => import('./auth/auth.module').then(m => m.AuthModule),
  },
  {
    path: '',
    canActivateChild: [authGuard],
    component: LayoutComponent,
    children: [
      {path: '', redirectTo: 'recipes', pathMatch: 'full'},
      {
        path: 'recipes',
        loadChildren: () => import('./recipes/recipes.module')
          .then(m => m.RecipesModule)
      },
    ]
  },
  {path: 'landing', component: LandingComponent},
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule {
}
