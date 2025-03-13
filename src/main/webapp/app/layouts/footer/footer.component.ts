import { Component, inject } from '@angular/core';
import { Router, RouterModule } from '@angular/router';

@Component({
  selector: 'jhi-footer',
  templateUrl: './footer.component.html',
  styleUrl: './footer.component.scss',
})
export default class FooterComponent {
    private readonly router = inject(Router);


    navigateToLogin(): void {
      this.router.navigate(['/login']);
    }

    navigateToRegister(): void {
      this.router.navigate(['/account/register']);
    }

     navigateToSettings(): void {
        this.router.navigate(['/joiner/new']);
      }
  }
