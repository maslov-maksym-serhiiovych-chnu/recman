import {NgModule} from '@angular/core';
import {BrowserModule} from '@angular/platform-browser';

import {AppRoutingModule} from './app-routing.module';
import {AppComponent} from './app.component';
import {SidebarComponent} from './sidebar/sidebar.component';
import {NgOptimizedImage} from "@angular/common";
import {RecipesComponent} from './recipes/recipes.component';
import {ReactiveFormsModule} from '@angular/forms';
import {LayoutComponent} from './layout/layout.component';
import {provideHttpClient, withInterceptors} from '@angular/common/http';
import {LoginComponent} from './auth/login/login.component';
import {RegisterComponent} from './auth/register/register.component';
import {LandingComponent} from './landing/landing.component';
import {authInterceptor} from './auth/auth.interceptor';

@NgModule({
  declarations: [
    AppComponent,
    SidebarComponent,
    RecipesComponent,
    LoginComponent,
    RegisterComponent,
    LayoutComponent,
    LandingComponent,
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    NgOptimizedImage,
    ReactiveFormsModule
  ],
  providers: [provideHttpClient(withInterceptors([authInterceptor]))],
  bootstrap: [AppComponent]
})
export class AppModule {
}
