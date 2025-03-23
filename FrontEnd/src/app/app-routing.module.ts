import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { AccountInfoComponent } from './component/user/account-info/account-info.component';
import { DataComponent } from './component/user/data/data.component';
import { HomepageComponent } from './component/user/homepage/homepage.component';
import { MealsComponent } from './component/user/meals/meals.component';
import { WorkoutsComponent } from './component/user/workouts/workouts.component';
import { CreateComponent } from './component/website/create/create.component';
import { ForgetComponent } from './component/website/forget/forget.component';
import { HomeComponent } from './component/website/home/home.component';
import { LoginComponent } from './component/website/login/login.component';
import { PaymentSuccessComponent } from './component/website/payment-success/payment-success.component';
import { LeaderboardComponent } from './component/user/leaderboard/leaderboard.component';

const websiteRoutes: Routes = [
  { path: 'healthtracker/home', component: HomeComponent },
  { path: 'healthtracker/login', component: LoginComponent },
  { path: 'healthtracker/forget', component: ForgetComponent },
  { path: 'healthtracker/create', component: CreateComponent },
  { path: 'healthtracker/forget', component: ForgetComponent },
  { path: 'healthtracker/payment/success', component: PaymentSuccessComponent },
  { path: 'user/homepage/:name', component: HomepageComponent },
  { path: 'user/accountInfo/:name', component: AccountInfoComponent },
  { path: 'user/addMeal/:name', component: MealsComponent },
  { path: 'user/addWorkout/:name', component: WorkoutsComponent },
  { path: 'user/data/:name', component: DataComponent },
  { path: 'user/leaderboard/:name', component: LeaderboardComponent },
  { path: '**', redirectTo: 'healthtracker/home', pathMatch: 'full' }
]
@NgModule({
  imports: [RouterModule.forRoot(websiteRoutes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
