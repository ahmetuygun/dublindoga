import { Component, inject, Input, OnInit , signal} from '@angular/core';
import SharedModule from 'app/shared/shared.module';
import { FormatMediumDatetimePipe, FormatMediumDatePipe } from 'app/shared/date';
import { DataUtils } from 'app/core/util/data-util.service';
import { IEvent } from '../event.model';
import HasAnyAuthorityDirective from 'app/shared/auth/has-any-authority.directive';
import { DomSanitizer, SafeHtml } from "@angular/platform-browser";
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { ActivatedRoute, Router, RouterModule } from "@angular/router";
import { EventService } from '../service/event.service';
import { AccountService } from 'app/core/auth/account.service';

@Component({
  selector: 'jhi-event-detail',
  templateUrl: './event-detail.component.html',
  styleUrl: './event-detail.component.scss',
  imports: [SharedModule, RouterModule, FormatMediumDatetimePipe, HasAnyAuthorityDirective, FormatMediumDatePipe],
})
export class EventDetailComponent implements OnInit {
  protected readonly eventService = inject(EventService);
  public readonly accountService = inject(AccountService);
  protected dataUtils = inject(DataUtils);

  event: IEvent | null = null; // Store fetched event
  eventId!: number; // Store event ID

  constructor(
    private route: ActivatedRoute, // Inject ActivatedRoute correctly
    private router: Router,
    private sanitizer: DomSanitizer,
    private modalService: NgbModal
  ) {}

  attendanceStatusMessage: string = "";
  attendanceButtonInProgress: boolean = false;
  attendanceButtonDisabled: boolean = true;
  showConfirmation: boolean = false; // Controls visibility of confirmation modal
  isLoading = signal(false);


  ngOnInit(): void {
    this.isLoading.set(true); // Start loading before registration request
    this.route.paramMap.subscribe(params => {
      const id = params.get('id');
      if (id) {
        this.eventId = +id; // Convert string to number
        this.fillTheEvent(); // Fetch event details

      } else {
        console.error("Event ID is missing from the route.");
      }
    });
  }

  fillTheEvent(): void {
    if (!this.eventId) {
      console.error("Event ID is undefined. Cannot fetch event details.");
      return;
    }

    if (this.accountService.hasAnyAuthority(['ROLE_ADMIN'])) {

      this.eventService.findForAdmin(this.eventId).subscribe({
        next: (response) => {
          if (response.body) {
            this.event = response.body;
            this.isLoading.set(false); // Start loading before registration request
          }
        },
        error: (err) => {
          console.error("Error fetching event details:", err);
        }
      });
    }else{
      this.eventService.find(this.eventId).subscribe({
        next: (response) => {
          if (response.body) {
            this.event = response.body;
            this.processButtonStatus();

            this.isLoading.set(false); // Start loading before registration request
          }
        },
        error: (err) => {
          console.error("Error fetching event details:", err);
        }
      });
    }

  }

  processButtonStatus(): void {
    this.isLoading.set(true); // Start loading before registration request

    this.accountService.identity().subscribe(account => {
      if (account?.id && account?.joiner && this.event?.id) {
        console.info("processButton -> ", account);

        this.eventService.checkAttendance(this.event.id, account.joiner.id).subscribe({
          next: response => {
            console.info("processButton 2 -> ", account);

            const status = response.body;
            if (status?.pending) {
              this.attendanceStatusMessage = "Bekleme Listesindesiniz";
              this.attendanceButtonDisabled = true;
            } else if (status?.approved) {
              this.attendanceStatusMessage = "Bu etkinliğe katılıyorsunuz";
              this.attendanceButtonDisabled = true;
            } else {
              this.attendanceStatusMessage = "";
              this.attendanceButtonDisabled = false;
            }
            this.isLoading.set(false); // Start loading before registration request

          },
          error: err => {
            console.error("Katılım durumu alınamadı", err);
            this.attendanceStatusMessage = "";
            this.attendanceButtonDisabled = false;
            this.isLoading.set(false); // Start loading before registration request

          }
        });
      }
    });
  }


  addJoiner(): void {
    if (!this.event || !this.event.id) {
      alert('Event ID is missing.');
      return;
    }
     this.isLoading.set(true); // Start loading before registration request

    const eventId = this.event.id;

    this.accountService.identity().subscribe({
      next: (account) => {
        if (account?.id && account?.joiner) {
          this.eventService.addJoiner(eventId, account.joiner.id).subscribe({
            next: () => {
              console.log('Joiner added successfully!');
              this.attendanceStatusMessage = "Bekleme Listesindesiniz";
              this.attendanceButtonDisabled = true;
              this.isLoading.set(false); // Start loading before registration request
            },
            error: (err) => {
              console.error('Error adding joiner:', err);
              alert('Failed to join the event. Please try again.');
             this.isLoading.set(false); // Start loading before registration request
            }
          });
        } else {
          this.router.navigate(['/login']);
        }
      },
      error: (err) => {
        console.error('Error fetching account details:', err);
        alert('Unable to retrieve user details.');
      }
    });
  }

  removeJoiner(): void {
    if (!this.event || !this.event.id) {
      alert('Event ID is missing.');
      return;
    }

    const eventId = this.event.id;

    this.accountService.identity().subscribe({
      next: (account) => {
        if (account?.id) {
          this.eventService.removeJoiner(eventId, account.id).subscribe({
            next: () => {
              console.log('Joiner removed successfully!');
            },
            error: (err) => {
              console.error('Error removing joiner:', err);
              alert('Failed to leave the event. Please try again.');
            }
          });
        } else {
          this.router.navigate(['/login']);
        }
      },
      error: (err) => {
        console.error('Error fetching account details:', err);
        alert('Unable to retrieve user details.');
      }
    });
  }

  cancelAttendance(): void {
    this.attendanceStatusMessage = "";
    this.attendanceButtonDisabled = false;
    this.showConfirmation = false;
    this.removeJoiner();
    console.log("Katılım iptal edildi.");
  }

  previousState(): void {
    this.router.navigate(['/events']); // Navigate back to events list
  }

  getDifficultyText(difficulty: string | null): string {
    const difficultyLevels: { [key: string]: string } = {
      EASY: "Kolay",
      MEDIUM: "Orta",
      HARD: "Zor",
      EXTREME: "Çok Zor",
    };
    return difficultyLevels[difficulty ?? "EASY"];
  }

  getDifficultyClass(difficulty: string | null): string {
    const difficultyClasses: { [key: string]: string } = {
      EASY: "easy",
      MEDIUM: "medium",
      HARD: "hard",
      EXTREME: "extreme",
    };
    return difficultyClasses[difficulty ?? "EASY"];
  }

  sanitizeHtml(html: string | null | undefined): SafeHtml {
    return this.sanitizer.bypassSecurityTrustHtml(typeof html === "string" ? html : "");
  }

  byteSize(base64String: string): string {
    return this.dataUtils.byteSize(base64String);
  }

  public openConfirmationModal(content: any): void {
    this.modalService.open(content, { centered: true });
  }

public maskName(fullName?: string | null): string {
  if (!fullName) return "Bilinmeyen Kişi";

  // Check if the user has the 'ROLE_ADMIN' role
  if (this.accountService.hasAnyAuthority(['ROLE_ADMIN'])) {
    return fullName; // Return full name without masking
  }

  // Mask the name for non-admin users
  const nameParts = fullName.split(" ");
  if (nameParts.length < 2) {
    return nameParts[0].charAt(0) + "*".repeat(nameParts[0].length - 1);
  }

  const maskedFirstName = nameParts[0].charAt(0) + "*".repeat(nameParts[0].length - 1);
  const maskedLastName = nameParts[nameParts.length - 1].charAt(0) + "*".repeat(nameParts[nameParts.length - 1].length - 1);

  return `${maskedFirstName} ${maskedLastName}`;
}

approveJoiner(joinerId: number): void {
  if (!this.event || !this.event.id) {
    alert('Event ID is missing.');
    return;
  }

  this.eventService.approveJoiner(this.event.id, joinerId).subscribe({
    next: () => {
      console.log(`Joiner ${joinerId} approved successfully!`);
    },
    error: (err) => {
      console.error('Error approving joiner:', err);
      alert('Failed to approve the joiner. Please try again.');
    }
  });
}

toRemoveFromApproved(joinerId: number): void {
  if (!this.event || !this.event.id) {
    alert('Event ID is missing.');
    return;
  }

  this.eventService.toRemoveFromApproved(this.event.id, joinerId).subscribe({
    next: () => {
      console.log(`Joiner ${joinerId} removed successfully!`);
      this.fillTheEvent(); // Refresh event details after removal
    },
    error: (err) => {
      console.error('Error removing joiner:', err);
      alert('Failed to remove the joiner. Please try again.');
    }
  });
}


}
