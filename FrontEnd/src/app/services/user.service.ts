import { HttpClient } from "@angular/common/http";
import { inject, Injectable } from "@angular/core";
import { firstValueFrom } from "rxjs";
import { LoginDetails, ToChange, UserDetails, login } from "../model/model";

@Injectable()
export class UserService {
    private http = inject(HttpClient)

    getInfo(name: string): Promise<UserDetails> {
        return firstValueFrom(
            this.http.get<UserDetails>('app/getInfo', { params: { name } })
        );
    }

    

    getName(email:string):Promise<login> {
        return firstValueFrom(
            this.http.get<login>('app/getInfoByEmail', { params: { email } })
        );
    }

    change(item:ToChange):Promise<any>{
        return firstValueFrom(this.http.post("/app/change",item)).catch(error => {throw error})
    }

}