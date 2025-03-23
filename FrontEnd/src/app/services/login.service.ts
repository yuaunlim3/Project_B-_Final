import { HttpClient, HttpErrorResponse } from "@angular/common/http";
import { inject, Injectable } from "@angular/core";
import { LoginDetails, login } from "../model/model";
import { firstValueFrom, map, Subject } from "rxjs";
import { JwtService } from "../JWT/Jwt.service";

@Injectable()
export class loginService {
    private http = inject(HttpClient)
    private jwtService = inject(JwtService);
    
    create(loginDetails: LoginDetails): Promise<login> {
        return firstValueFrom(
            this.http.post<login>('app/create', loginDetails)
        ).catch(error => {
            this.handleError(error);
            throw error;
        });
    }

    login(loginDetails: LoginDetails): Promise<any> {
        return firstValueFrom(
            this.http.post<any>('app/login', loginDetails)
        ).then(response => {
           
            if (response.token) {
                this.jwtService.saveToken(response.token);
                this.jwtService.saveUser({ name: response.name });
            }
            return response;
        }).catch(error => {
            this.handleError(error);
            throw error;
        });
    }
    
    logout(): void {
        this.jwtService.logout();
    }
    
    isLoggedIn(): boolean {
        return this.jwtService.isLoggedIn();
    }
    private handleError(error: HttpErrorResponse): void {
        console.error('Login error:', error);
        if (error.status === 0) {
            console.error('Network error:', error.error);
        } else {
            console.error(`Backend returned code ${error.status}, body:`, error.error);
        }
    }
    
}