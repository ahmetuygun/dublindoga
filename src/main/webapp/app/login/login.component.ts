import { AfterViewInit, Component, ElementRef, OnInit, inject, signal, viewChild } from '@angular/core';
import { FormControl, FormGroup, FormsModule, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, RouterModule } from '@angular/router';

import SharedModule from 'app/shared/shared.module';
import { LoginService } from 'app/login/login.service';
import { AccountService } from 'app/core/auth/account.service';

@Component({
  selector: 'jhi-login',
  imports: [SharedModule, FormsModule, ReactiveFormsModule, RouterModule],
  templateUrl: './login.component.html',
})
export default class LoginComponent implements OnInit, AfterViewInit {
  username = viewChild.required<ElementRef>('username');

  authenticationError = signal(false);
  isLoading = signal(false);

  loginForm = new FormGroup({
    username: new FormControl('', { nonNullable: true, validators: [Validators.required] }),
    password: new FormControl('', { nonNullable: true, validators: [Validators.required] }),
    rememberMe: new FormControl(false, { nonNullable: true, validators: [Validators.required] }),
  });

  private readonly accountService = inject(AccountService);
  private readonly loginService = inject(LoginService);
  private readonly router = inject(Router);

  ngOnInit(): void {

    // if already authenticated then navigate to home page
    this.accountService.identity().subscribe(() => {
      if (this.accountService.isAuthenticated()) {
        this.router.navigate(['']);
      }
    });
  }

  ngAfterViewInit(): void {
    this.username().nativeElement.focus();
  }

login(): void {
  this.isLoading.set(true);
  this.loginService.login(this.loginForm.getRawValue()).subscribe({
    next: () => {
      this.authenticationError.set(false);
      this.accountService.identity().subscribe(account => {
        console.log(account);
        if (account?.joiner === null) {
          this.router.navigate(['/joiner/new']);
        }
      });

      if (!this.router.getCurrentNavigation()) {
        // There were no routing during login (eg from navigationToStoredUrl)
        this.router.navigate(['']);
      }

      this.isLoading.set(false); // Stop loading after successful login
    },
    error: () => {
      this.authenticationError.set(true);
      this.isLoading.set(false); // Stop loading in case of an error
    },
  });
}
}

