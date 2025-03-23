import { HttpClient } from "@angular/common/http";
import { inject, Injectable } from "@angular/core";
import { firstValueFrom } from "rxjs";
import { login, PaymentInfo, VerifyPayment } from "../model/model";

Injectable()
export class PaymentService {
    private http = inject(HttpClient)

    makePayment(email: string): Promise<PaymentInfo> {
        return firstValueFrom(
            this.http.get<PaymentInfo>('app/payment', { params: { email } })
        );
    }

    verifyPay(info:VerifyPayment):Promise<login>{
        return firstValueFrom(
            this.http.post<login>('app/verify', info)
        );
    }
}