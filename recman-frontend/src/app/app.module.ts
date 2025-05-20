import {AppComponent} from './app.component';
import {LayoutComponent} from './layout/layout.component';
import {SidebarComponent} from './sidebar/sidebar.component';
import {RecipeFormDialogComponent} from './recipes/recipe-form-dialog/recipe-form-dialog.component';
import {LandingComponent} from './landing/landing.component';
import {AppRoutingModule} from './app-routing.module';
import {ReactiveFormsModule} from '@angular/forms';
import {MatDialogContent, MatDialogTitle} from '@angular/material/dialog';
import {BrowserModule} from '@angular/platform-browser';
import {NgOptimizedImage} from "@angular/common";
import {NgModule} from '@angular/core';
import {provideHttpClient, withInterceptors} from '@angular/common/http';
import {authInterceptor} from './auth/auth.interceptor';

@NgModule({
  declarations: [
    AppComponent,
    LayoutComponent,
    SidebarComponent,
    LandingComponent,
  ],
  imports: [
    AppRoutingModule,
    ReactiveFormsModule,
    MatDialogContent,
    MatDialogTitle,
    BrowserModule,
    NgOptimizedImage,
  ],
  providers: [provideHttpClient(withInterceptors([authInterceptor]))],
  bootstrap: [AppComponent]
})
export class AppModule {
}
