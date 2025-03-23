import { Component, inject, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { loginService } from '../../../services/login.service';
import { LoginDetails } from '../../../model/model';
import { DataServices } from '../../../services/data.service';
import { DexieService } from '../../../services/dexie.service';
import { User } from '../../../model/workout.model';
import { WorkoutService } from '../../../services/workout.service';
import { JwtService } from '../../../JWT/Jwt.service';

@Component({
  selector: 'app-login',
  standalone: false,
  templateUrl: './login.component.html',
  styleUrl: './login.component.css'
})
export class LoginComponent implements OnInit {

  private fb = inject(FormBuilder)
  protected form!: FormGroup
  private router = inject(Router)
  private loginSvc = inject(loginService)
  private dataSvc = inject(DataServices)
  private dexieSvc = inject(DexieService)
  private workoutSvc = inject(WorkoutService)
  private jwtSvc = inject(JwtService);

  private createUser!: User

  ngOnInit(): void {
    this.form = this.createForm()
    if (this.jwtSvc.isLoggedIn()) {
      const user = this.jwtSvc.getUser();
      if (user && user.name) {
        this.router.navigate(['/user/homepage', user.name]);
      }
    }
  }

  private createForm(): FormGroup {
    return this.fb.group({
      email: this.fb.control<string>('', Validators.required),
      password: this.fb.control<string>('', Validators.required)
    })
  }

  login() {
    const details: LoginDetails = this.form.value;
  
    if (!this.createUser) {
      this.createUser = { workouts: [], name: '' };
    }
  
    this.loginSvc.login(details)
      .then(result => {
        this.jwtSvc.saveToken(result.token);
        this.jwtSvc.saveUser({ name: result.name });
        this.dataSvc.checkAndReset(result.name);
        this.createUser.name = result.name;
        return this.workoutSvc.getPlanned(result.name)
  
      }).then(workout => {
        this.createUser.workouts = workout.workouts;
        this.dexieSvc.updateUser(this.createUser);
        this.router.navigate(['/user/homepage', this.createUser.name]);
      })
      .catch(error => {
        if (error.status === 401) {
          alert("Invalid password");
          this.form.get('password')?.setErrors({ invalidPassword: true });
        } else if (error.status === 404) {
          alert("Account not found. Please create an account.");
          this.router.navigate(['/healthtracker/create']);
        } else {
          alert("An error occurred. Please try again later.");
        }
      });
  }
  


  formValid(): boolean {
    return this.form.invalid
  }

  getErrorMessage(field: string): string {
    const control = this.form.get(field);

    if (control?.touched && control.invalid) {
      if (control?.hasError('invalidPassword')) {
        return 'Invalid password';
      }
      if (control.errors?.['required']) {
        return `${field.charAt(0).toUpperCase() + field.slice(1)} is required.`;
      }
      if (control.errors?.['email']) {
        return `Invalid email format.`;
      }
      if (control.errors?.['minlength']) {
        return `Password must be at least ${control.errors['minlength'].requiredLength} characters.`;
      }
    }
    return '';
  }
}
