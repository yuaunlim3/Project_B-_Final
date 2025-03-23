import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class JwtService {
  private readonly TOKEN_KEY = 'auth_token';
  private readonly USER_KEY = 'auth_user';

  constructor() { }

 
  public saveToken(token: string): void {
    console.info(token)
    localStorage.removeItem(this.TOKEN_KEY);
    localStorage.setItem(this.TOKEN_KEY, token);
  }

  public saveUser(user: any): void {
    localStorage.removeItem(this.USER_KEY);
    localStorage.setItem(this.USER_KEY, JSON.stringify(user));
  }

 
  public getToken(): string | null {
    return localStorage.getItem(this.TOKEN_KEY);
  }

  public getUser(): any {
    const user = localStorage.getItem(this.USER_KEY);
    if (user) {
      return JSON.parse(user);
    }
    return null;
  }

 
  public isLoggedIn(): boolean {
    const token = this.getToken();
    return !!token;
  }


  public logout(): void {
    localStorage.removeItem(this.TOKEN_KEY);
    localStorage.removeItem(this.USER_KEY);
  }
}