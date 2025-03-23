import { HttpClient, HttpParams } from "@angular/common/http";
import { inject, Injectable } from "@angular/core";
import { firstValueFrom } from "rxjs";
import { ResetPassword, VerifyCode } from "../model/model";

Injectable()
export class PasswordService {
    private http = inject(HttpClient)

    getCode(email: string){
        return firstValueFrom(this.http.get('app/forgetPassword', {
            params: new HttpParams().set('email',email)
        })).catch(error=>{
            throw error
        }
        )
    }

    checkCode(info:VerifyCode) {
        return firstValueFrom(this.http.post('app/verifyCode', info))
        .catch(error=>{
            throw error
        }
        )
    }

    resetPassword(info:ResetPassword) {
        return firstValueFrom(this.http.post('app/resetPassword', info))
        .catch(error=>{
            throw error
        }
        )
    }
}