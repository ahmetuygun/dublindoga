import { Component, ElementRef, OnInit, inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import SharedModule from 'app/shared/shared.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { AlertError } from 'app/shared/alert/alert-error.model';
import { EventManager, EventWithContent } from 'app/core/util/event-manager.service';
import { DataUtils, FileLoadError } from 'app/core/util/data-util.service';
import { IUser } from 'app/entities/user/user.model';
import { UserService } from 'app/entities/user/service/user.service';
import { IEvent } from 'app/entities/event/event.model';
import { EventService } from 'app/entities/event/service/event.service';
import { JoinStatus } from 'app/entities/enumerations/join-status.model';
import { Gender } from 'app/entities/enumerations/gender.model';
import { JoinerService } from '../service/joiner.service';
import { IJoiner } from '../joiner.model';
import { JoinerFormGroup, JoinerFormService } from './joiner-form.service';

import { AccountService } from 'app/core/auth/account.service';


@Component({
  selector: 'jhi-joiner-update',
  templateUrl: './joiner-update.component.html',
  imports: [SharedModule, FormsModule, ReactiveFormsModule],
})
export class JoinerUpdateComponent implements OnInit {
  isSaving = false;
  joiner: IJoiner | null = null;
  joinStatusValues = Object.keys(JoinStatus);
  genderValues = Object.keys(Gender);

  usersSharedCollection: IUser[] = [];
  eventsSharedCollection: IEvent[] = [];

  protected dataUtils = inject(DataUtils);
  protected eventManager = inject(EventManager);
  protected joinerService = inject(JoinerService);
  protected joinerFormService = inject(JoinerFormService);
  protected userService = inject(UserService);
  protected eventService = inject(EventService);
  protected elementRef = inject(ElementRef);
  protected activatedRoute = inject(ActivatedRoute);
  private readonly accountService = inject(AccountService);


  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: JoinerFormGroup = this.joinerFormService.createJoinerFormGroup();

  compareUser = (o1: IUser | null, o2: IUser | null): boolean => this.userService.compareUser(o1, o2);

  compareEvent = (o1: IEvent | null, o2: IEvent | null): boolean => this.eventService.compareEvent(o1, o2);

ngOnInit(): void {
  this.activatedRoute.data.subscribe(({ joiner }) => {
    this.joiner = joiner;
    if (joiner) {
      this.updateForm(joiner);
    }  else {
        this.accountService.identity().subscribe(account => {
          console.log(account);

            if (account?.joiner) {
              // If account.joiner is NOT null, update the form with joiner data
              this.updateForm(account.joiner);
            } else {
              // If account.joiner is null, set default form values
              this.editForm.patchValue({
                email: account?.email,
                status: 'PENDING',
                internalUser: account?.id ? { id: account.id } : null
              });
              this.editForm.controls['email'].disable(); // Disable the email field
            }

        });
      }

    this.loadRelationshipsOptions();
  });
}


  byteSize(base64String: string): string {
    return this.dataUtils.byteSize(base64String);
  }

  openFile(base64String: string, contentType: string | null | undefined): void {
    this.dataUtils.openFile(base64String, contentType);
  }

  setFileData(event: Event, field: string, isImage: boolean): void {
    this.dataUtils.loadFileToForm(event, this.editForm, field, isImage).subscribe({
      error: (err: FileLoadError) =>
        this.eventManager.broadcast(new EventWithContent<AlertError>('dublindogaApp.error', { message: err.message })),
    });
  }

  clearInputImage(field: string, fieldContentType: string, idInput: string): void {
    this.editForm.patchValue({
      [field]: null,
      [fieldContentType]: null,
    });
    if (idInput && this.elementRef.nativeElement.querySelector(`#${idInput}`)) {
      this.elementRef.nativeElement.querySelector(`#${idInput}`).value = null;
    }
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const joiner = this.joinerFormService.getJoiner(this.editForm);
    if (joiner.id !== null) {
      this.subscribeToSaveResponse(this.joinerService.update(joiner));
    } else {
      this.subscribeToSaveResponse(this.joinerService.create(joiner));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IJoiner>>): void {
    result.pipe(finalize(() => this.onSaveFinalize())).subscribe({
      next: () => this.onSaveSuccess(),
      error: () => this.onSaveError(),
    });
  }

  protected onSaveSuccess(): void {
    this.previousState();
  }

  protected onSaveError(): void {
    // Api for inheritance.
  }

  protected onSaveFinalize(): void {
    this.isSaving = false;
  }

  protected updateForm(joiner: IJoiner): void {
    this.joiner = joiner;
    this.joinerFormService.resetForm(this.editForm, joiner);

    this.usersSharedCollection = this.userService.addUserToCollectionIfMissing<IUser>(this.usersSharedCollection, joiner.internalUser);
    this.eventsSharedCollection = this.eventService.addEventToCollectionIfMissing<IEvent>(
      this.eventsSharedCollection,
      ...(joiner.pendingEvents ?? []),
      ...(joiner.aprovedEvents ?? []),
    );
  }

  protected loadRelationshipsOptions(): void {
    this.userService
      .query()
      .pipe(map((res: HttpResponse<IUser[]>) => res.body ?? []))
      .pipe(map((users: IUser[]) => this.userService.addUserToCollectionIfMissing<IUser>(users, this.joiner?.internalUser)))
      .subscribe((users: IUser[]) => (this.usersSharedCollection = users));

    this.eventService
      .query()
      .pipe(map((res: HttpResponse<IEvent[]>) => res.body ?? []))
      .pipe(
        map((events: IEvent[]) =>
          this.eventService.addEventToCollectionIfMissing<IEvent>(
            events,
            ...(this.joiner?.pendingEvents ?? []),
            ...(this.joiner?.aprovedEvents ?? []),
          ),
        ),
      )
      .subscribe((events: IEvent[]) => (this.eventsSharedCollection = events));
  }
}
