import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common'; // ✅ Import CommonModule
import { registerLocaleData } from '@angular/common';
import dayjs from 'dayjs/esm';
import { FaIconLibrary } from '@fortawesome/angular-fontawesome';
import { NgbDatepickerConfig } from '@ng-bootstrap/ng-bootstrap';
import locale from '@angular/common/locales/tr';

import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { fontAwesomeIcons } from './config/font-awesome-icons';
import MainComponent from './layouts/main/main.component';

@Component({
  selector: 'jhi-app',
  template: `
    <div *ngIf="isPageLoaded" class="app-container">
      <jhi-main></jhi-main>
    </div>
  `,
  standalone: true, // ✅ Ensure this is a standalone component
  imports: [
    CommonModule, // ✅ Add this to use *ngIf
    MainComponent,
  ],
})
export default class AppComponent {
  private readonly applicationConfigService = inject(ApplicationConfigService);
  private readonly iconLibrary = inject(FaIconLibrary);
  private readonly dpConfig = inject(NgbDatepickerConfig);

  isPageLoaded = false;

  constructor() {
    this.applicationConfigService.setEndpointPrefix(SERVER_API_URL);
    registerLocaleData(locale);
    this.iconLibrary.addIcons(...fontAwesomeIcons);
    this.dpConfig.minDate = { year: dayjs().subtract(100, 'year').year(), month: 1, day: 1 };

    // Ensure all elements load together
    setTimeout(() => {
      this.isPageLoaded = true;
    }, 1000);
  }
}
