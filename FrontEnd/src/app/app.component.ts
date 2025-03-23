import { Component, inject, OnInit } from '@angular/core';
import { NavigationEnd, Router } from '@angular/router';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  standalone: false,
  styleUrl: './app.component.css'
})

export class AppComponent implements OnInit {
  showUserNavbar = false;
  showWebNavbar = true;

  private router = inject(Router)

  ngOnInit(): void {
    this.router.events.subscribe(event => {
      if (event instanceof NavigationEnd) {
        const firstSegment = event.urlAfterRedirects.split('/')[1]; 
        this.showUserNavbar = firstSegment === 'user';
        this.showWebNavbar = firstSegment === 'healthtracker';
      }
    });

  }

}
