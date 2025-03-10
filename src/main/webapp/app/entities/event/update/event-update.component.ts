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
import { IJoiner } from 'app/entities/joiner/joiner.model';
import { JoinerService } from 'app/entities/joiner/service/joiner.service';
import { Difficulty } from 'app/entities/enumerations/difficulty.model';
import { EventService } from '../service/event.service';
import { IEvent } from '../event.model';
import { EventFormGroup, EventFormService } from './event-form.service';

import { toHTML } from 'ngx-editor';
import {Editor, NgxEditorModule, Toolbar} from "ngx-editor";



@Component({
  selector: 'jhi-event-update',
  templateUrl: './event-update.component.html',
  imports: [SharedModule, FormsModule, ReactiveFormsModule, NgxEditorModule],
})
export class EventUpdateComponent implements OnInit {
  isSaving = false;
  event: IEvent | null = null;
  difficultyValues = Object.keys(Difficulty);

  joinersSharedCollection: IJoiner[] = [];

  protected dataUtils = inject(DataUtils);
  protected eventManager = inject(EventManager);
  protected eventService = inject(EventService);
  protected eventFormService = inject(EventFormService);
  protected joinerService = inject(JoinerService);
  protected elementRef = inject(ElementRef);
  protected activatedRoute = inject(ActivatedRoute);

    editor!: Editor;
    html = '';
    toolbar: Toolbar = [
      ['bold', 'italic'],
      ['underline', 'strike'],
      ['code', 'blockquote'],
      ['ordered_list', 'bullet_list'],
      [{ heading: ['h1', 'h2', 'h3', 'h4', 'h5', 'h6'] }],
      ['link', 'image'],
      ['text_color', 'background_color'],
      ['align_left', 'align_center', 'align_right', 'align_justify'],
    ];

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: EventFormGroup = this.eventFormService.createEventFormGroup();

  compareJoiner = (o1: IJoiner | null, o2: IJoiner | null): boolean => this.joinerService.compareJoiner(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ event }) => {
      this.event = event;
      if (event) {
        this.updateForm(event);
      }

      this.loadRelationshipsOptions();
    });
   this.editor = new Editor();
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
    const event = this.eventFormService.getEvent(this.editForm);

        function isRecordStringAny(obj: any): obj is Record<string, any> {
          return typeof obj === 'object' && obj !== null;
        }

        if (isRecordStringAny(event.description)) {
          const html = toHTML(event.description);
          event.description = html;
        }

    if (event.id !== null) {
      this.subscribeToSaveResponse(this.eventService.update(event));
    } else {
      this.subscribeToSaveResponse(this.eventService.create(event));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IEvent>>): void {
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

  protected updateForm(event: IEvent): void {
    this.event = event;
    this.eventFormService.resetForm(this.editForm, event);

    this.joinersSharedCollection = this.joinerService.addJoinerToCollectionIfMissing<IJoiner>(
      this.joinersSharedCollection,
      ...(event.pendingJoiners ?? []),
      ...(event.approvedJoiners ?? []),
    );
  }

  protected loadRelationshipsOptions(): void {
    this.joinerService
      .query()
      .pipe(map((res: HttpResponse<IJoiner[]>) => res.body ?? []))
      .pipe(
        map((joiners: IJoiner[]) =>
          this.joinerService.addJoinerToCollectionIfMissing<IJoiner>(
            joiners,
            ...(this.event?.pendingJoiners ?? []),
            ...(this.event?.approvedJoiners ?? []),
          ),
        ),
      )
      .subscribe((joiners: IJoiner[]) => (this.joinersSharedCollection = joiners));
  }
}
