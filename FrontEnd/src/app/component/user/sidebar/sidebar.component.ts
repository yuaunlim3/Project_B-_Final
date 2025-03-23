import { Component, inject } from '@angular/core';
import { Router } from '@angular/router';
import { DexieService } from '../../../services/dexie.service';
import { loginService } from '../../../services/login.service';
import { UserService } from '../../../services/user.service';
import { Subscription } from 'rxjs';

@Component({
  selector: 'app-sidebar',
  standalone: false,
  templateUrl: './sidebar.component.html',
  styleUrl: './sidebar.component.css'
})
export class SidebarComponent {
  private router = inject(Router);
  currentUrl: string = ''
  protected subs: string = ''
  private userSvc = inject(UserService);
  user: string = ''
  private dexieSvc = inject(DexieService)
  private loginSvc = inject(loginService)


  ngOnInit(): void {
    const urlSegments = this.router.url.split('/');
    this.user = urlSegments[urlSegments.length - 1];

    this.subs = localStorage.getItem("subscription") || "free";

    this.userSvc.getInfo(this.user).then(result => {
      this.subs = result.subscription
      localStorage.setItem("subscription",result.subscription)
    })
  }

  logout() {
    this.dexieSvc.deleteUser(this.user)
    this.loginSvc.logout();
    localStorage.removeItem('auth_token');
    localStorage.removeItem('auth_user');
    this.router.navigate(['/healthtracker/home']);
  }


}
