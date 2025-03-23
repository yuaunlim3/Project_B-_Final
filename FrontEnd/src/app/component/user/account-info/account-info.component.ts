import { Component, inject, OnDestroy, OnInit } from '@angular/core';
import { UserService } from '../../../services/user.service';
import { ActivatedRoute } from '@angular/router';
import { PaymentInfo, ToChange, UserDetails } from '../../../model/model';
import { Subscription } from 'rxjs';
import { FormBuilder, FormGroup } from '@angular/forms';
import { PaymentService } from '../../../services/payment.service';

@Component({
  selector: 'app-account-info',
  standalone: false,
  templateUrl: './account-info.component.html',
  styleUrl: './account-info.component.css'
})
export class AccountInfoComponent implements OnInit, OnDestroy {


  private userSvc = inject(UserService);
  private activeRoute = inject(ActivatedRoute);
  private fb = inject(FormBuilder);
  private sub!: Subscription;
  private paymentSvc = inject(PaymentService)

  userInfo: UserDetails = {} as UserDetails;

  passwordForm!: FormGroup;
  heightForm!: FormGroup;
  weightForm!: FormGroup;
  activityForm!: FormGroup;
  targetForm!: FormGroup;

  protected daysRemaining: number = 0;

  ngOnInit(): void {
    this.sub = this.activeRoute.params.subscribe(params => {
      this.userSvc.getInfo(params['name']).then(result => {
  
        this.userInfo = result;

        if (this.userInfo && this.userInfo.subscription === 'premium' && this.userInfo.paid_date) {
          this.calculateDaysRemaining();
        }
      });
    });

    this.initForms();
  }

  ngOnDestroy(): void {
    this.sub.unsubscribe();
  }

  private initForms(): void {
    this.passwordForm = this.fb.group({
      password: ['']
    });
    this.heightForm = this.fb.group({
      height: ['']
    });
    this.weightForm = this.fb.group({
      weight: ['']
    });
    this.activityForm = this.fb.group({
      activity_level: ['']
    });
    this.targetForm = this.fb.group({
      target: ['']
    });
  }
  updatePassword(): void {
    const change: ToChange = {
      item: "password",
      change: this.passwordForm.value.password,
      name: this.userInfo.name
    }
    this.userSvc.change(change)
      .then(response => {
        this.userInfo = { ...this.userInfo, password: response.change };
        alert("Change is made");
      })
      .catch(error => {
        if (error.status === 409) {
          alert("You have given the same information");
        } else if (error.status === 403) {
          alert('You do not have permission to make this change.');
        } else {
          alert("An error occurred. Please try again later.");
        }
      });
    this.passwordForm.reset();
  }

  updateHeight(): void {
    const change: ToChange = {
      item: "height",
      change: this.heightForm.value.height,
      name: this.userInfo.name
    }

    this.userSvc.change(change)
      .then(response => {
        this.userInfo = { ...this.userInfo, height: parseInt(response.change) };
        alert("Change is made");
      })
      .catch(error => {
        if (error.status === 409) {
          alert("You have given the same information");
        } else if (error.status === 403) {
          alert('You do not have permission to make this change.');
        } else {
          alert("An error occurred. Please try again later.");
        }
      });
    this.heightForm.reset();
  }

  updateWeight(): void {
    const change: ToChange = {
      item: "weight",
      change: this.weightForm.value.weight,
      name: this.userInfo.name
    }

    this.userSvc.change(change)
      .then(response => {
        this.userInfo = { ...this.userInfo, weight: parseInt(response.change) };
        alert("Change is made");
      })
      .catch(error => {
        if (error.status === 409) {
          alert("You have given the same information");
        } else if (error.status === 403) {
          alert('You do not have permission to make this change.');
        } else {
          alert("An error occurred. Please try again later.");
        }
      });
    this.weightForm.reset();
  }

  updateActivityLevel(): void {
    const change: ToChange = {
      item: "activity_level",
      change: this.activityForm.value.activity_level,
      name: this.userInfo.name
    }

    this.userSvc.change(change)
      .then(response => {
        this.userInfo = { ...this.userInfo, activity_level: response.change };
        alert("Change is made");
      })
      .catch(error => {
        if (error.status === 409) {
          alert("You have given the same information");
        } else if (error.status === 403) {
          alert('You do not have permission to make this change.');
        } else {
          alert("An error occurred. Please try again later.");
        }
      });
    this.activityForm.reset();
  }

  updateTarget(): void {
    const change: ToChange = {
      item: "target",
      change: this.targetForm.value.target,
      name: this.userInfo.name
    }

    this.userSvc.change(change)
      .then(response => {
        this.userInfo = { ...this.userInfo, target: response.change };
        alert("Change is made");
      })
      .catch(error => {
        if (error.status === 409) {
          alert("You have given the same information");
        } else if (error.status === 403) {
          alert('You do not have permission to make this change.');
        } else {
          alert("An error occurred. Please try again later.");
        }
      });
    this.targetForm.reset();
  }

  makePayment(): void {
    console.info("making payment")
    this.paymentSvc.makePayment(this.userInfo.email).then(info => {
      console.info(info)
      if (info && typeof info === 'object') {
        
        if (info.status === "Paying" && info.url) {
          setTimeout(() => {
            window.location.href = info.url;
          }, 1000);
        } else {
          console.error("Invalid payment info received:", info);
        }
      } else {
        console.error("Unexpected payment info format:", info);
      }
    }).catch(error => {
      console.error("Payment error:", error);
    });
  }


  calculateDaysRemaining() {
    const paidDate = new Date(this.userInfo.paid_date);
    
    const expiryDate = new Date(paidDate);
    expiryDate.setDate(expiryDate.getDate() + 30);
    
    const today = new Date();
    const timeDiff = expiryDate.getTime() - today.getTime();
    this.daysRemaining = Math.max(0, Math.ceil(timeDiff / (1000 * 3600 * 24)));
  }

}
