import { NgModule, isDevMode } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { AppComponent } from './app.component';
import { Routes, RouterModule } from '@angular/router';
import { HomeComponent } from './component/website/home/home.component';
import { NavbarComponent } from './component/website/navbar/navbar.component';
import { FootersComponent } from './component/website/footers/footers.component';
import { LoginComponent } from './component/website/login/login.component';
import { CreateComponent } from './component/website/create/create.component';
import { ReactiveFormsModule } from '@angular/forms';
import { HttpHandlerFn, HttpInterceptorFn, HttpRequest, provideHttpClient, withInterceptors } from '@angular/common/http';
import { loginService } from './services/login.service';
import { HomepageComponent } from './component/user/homepage/homepage.component';
import { AccountInfoComponent } from './component/user/account-info/account-info.component';
import { UserService } from './services/user.service';
import { MealsComponent } from './component/user/meals/meals.component';
import { WorkoutsComponent } from './component/user/workouts/workouts.component';
import { MealService } from './services/meal.service';
import { ForgetComponent } from './component/website/forget/forget.component';
import { SidebarComponent } from './component/user/sidebar/sidebar.component';
import { StoreModule } from '@ngrx/store';
import { mealsInfoReducer } from './ngRx/meal_info.reducer';
import { MatRadioModule } from '@angular/material/radio';
import { WeatherService } from './services/weather.service';
import { WorkoutService } from './services/workout.service';
import { DexieService } from './services/dexie.service';
import { PasswordService } from './services/password.service';
import { DataComponent } from './component/user/data/data.component';
import { AppRoutingModule } from './app-routing.module';
import { PaymentService } from './services/payment.service';
import { PaymentSuccessComponent } from './component/website/payment-success/payment-success.component';
import { JwtService } from './JWT/Jwt.service';
import { AchievementService } from './services/achievement.service';
import { LeaderboardComponent } from './component/user/leaderboard/leaderboard.component';
import { LeaderboardService } from './services/leaderboard.service';



export const authInterceptorFn: HttpInterceptorFn = (req: HttpRequest<unknown>, next: HttpHandlerFn) => {
  const token = localStorage.getItem('auth_token');
  if (token) {
    const authReq = req.clone({
      headers: req.headers.set('Authorization', `Bearer ${token}`)
    });
    return next(authReq);
  }
  return next(req);
};




@NgModule({
  declarations: [
    AppComponent,
    HomeComponent,
    NavbarComponent,
    FootersComponent,
    LoginComponent,
    CreateComponent,
    HomepageComponent,
    AccountInfoComponent,
    MealsComponent,
    WorkoutsComponent,
    ForgetComponent,
    SidebarComponent,
    DataComponent,
    PaymentSuccessComponent,
    LeaderboardComponent
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    ReactiveFormsModule,
    StoreModule.forRoot({
      mealsInfo: mealsInfoReducer,
    }),
    MatRadioModule,

  ],
  providers: [provideHttpClient(withInterceptors([authInterceptorFn])), loginService, UserService, 
    MealService, WeatherService,WorkoutService,DexieService
    ,PasswordService,PaymentService,
    JwtService,AchievementService,LeaderboardService
  
  ],
  bootstrap: [AppComponent]
})
export class AppModule { }

