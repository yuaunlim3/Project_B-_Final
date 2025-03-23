import { Component, inject, OnDestroy, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { Subscription } from 'rxjs';
import { UserService } from '../../../services/user.service';
import { VerifyPayment } from '../../../model/model';
import { PaymentService } from '../../../services/payment.service';


@Component({
  selector: 'app-payment-success',
  standalone: false,
  templateUrl: './payment-success.component.html',
  styleUrl: './payment-success.component.css'
})
export class PaymentSuccessComponent implements OnInit, OnDestroy {

  private activeRoute = inject(ActivatedRoute)
  private router = inject(Router)
  protected email: string = ''
  protected id: string = ''
  protected date: string = new Date().toISOString().split('T')[0];
  private sub !: Subscription
  protected name: string = ''
  private userSvc = inject(UserService)
  private paySvc = inject(PaymentService)

  ngOnInit(): void {
    this.sub = this.activeRoute.queryParams.subscribe(params => {
      this.email = params['email'];
      this.id = params['id'];
      let verify: VerifyPayment = {
        id: this.id,
        email: this.email
      }

      this.paySvc.verifyPay(verify)

    });


  }

  ngOnDestroy(): void {
    this.sub.unsubscribe()
  }

  login() {
    this.router.navigate(['/healthtracker/login']);
  }
}
