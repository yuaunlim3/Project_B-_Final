import { Component, inject, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { UserDetails } from '../../../model/model';
import { loginService } from '../../../services/login.service';
import { DataServices } from '../../../services/data.service';
import { DexieService } from '../../../services/dexie.service';
import { User } from '../../../model/workout.model';
import { PaymentService } from '../../../services/payment.service';
import { JwtService } from '../../../JWT/Jwt.service';

@Component({
  selector: 'app-create',
  standalone: false,
  templateUrl: './create.component.html',
  styleUrl: './create.component.css'
})
export class CreateComponent implements OnInit {

  private fb = inject(FormBuilder)
  protected form!: FormGroup
  private router = inject(Router)
  private createSvc = inject(loginService)
  private dataSvc = inject(DataServices)
  private dexieSvc = inject(DexieService)
  private paymentSvc = inject(PaymentService)
  private jwtSvc = inject(JwtService);
  private createUser: User = {
    name: "",
    workouts: []
  };

  ngOnInit(): void {
    this.form = this.createForm()
  }
  createAccount() {
    const details: UserDetails = this.form.value;
    this.form.reset();
    
    if (details.subscription === "premium") {
      this.paymentSvc.makePayment(details.email).then(info => {
        console.info(info)
        if (info.status == "Paying") {

          window.location.href = info.url;
  
          this.createSvc.create(details).then(result => {
            this.jwtSvc.saveToken(result.token);
            this.jwtSvc.saveUser({ name: result.name });
            this.dataSvc.setDate(result.name);
            this.createUser.name = result.name;
  
            return this.dexieSvc.getUser(result.name);
          }).then(user => {
            if (!user) {
              this.dexieSvc.addUser(this.createUser);
            }
          }).catch(error => {
            if (error.status === 409) {
              alert("Account already exists");
            } else {
              alert("An error occurred. Please try again later.");
            }
          });
        } else {
          alert("Payment failed to initiate.");
        }
      });
    } else {
      let userName: string; 
      
      this.createSvc.create(details)
        .then(result => {
          userName = result.name;
          
          this.jwtSvc.saveToken(result.token);
          this.jwtSvc.saveUser({ name: result.name });
          this.dataSvc.setDate(result.name);
          this.createUser.name = result.name;
          
          return this.dexieSvc.getUser(result.name);
        })
        .then(user => {
          if (!user) {
            this.dexieSvc.addUser(this.createUser);
          }
          this.router.navigate(['/user/homepage', userName]);
        })
        .catch(error => {
          if (error.status === 405) {
            alert("Account already exists");
          } else {
            alert("An error occurred. Please try again later.");
          }
        });
    }
  }


  formValid(): boolean {
    return this.form.invalid
  }

  private createForm(): FormGroup {
    return this.fb.group(
      {
        name: this.fb.control<string>('', Validators.required),
        email: this.fb.control<string>('', Validators.required),
        password: this.fb.control<string>('', Validators.required),
        height: this.fb.control<number>(0, Validators.required),
        weight: this.fb.control<number>(0, Validators.required),
        age: this.fb.control<number>(0, Validators.required),
        gender: this.fb.control<string>('', Validators.required),
        activity_level: this.fb.control<string>('', Validators.required),
        target: this.fb.control<string>('', Validators.required),
        subscription: ['free', [Validators.required]],
      }
    )
  }

  getErrorMessage(field: string): string {
    const control = this.form.get(field);

    if (control?.touched && control.invalid) {
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
