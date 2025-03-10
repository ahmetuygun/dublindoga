import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import dayjs from 'dayjs/esm';
import { DATE_TIME_FORMAT } from 'app/config/input.constants';
import { IEvent, NewEvent } from '../event.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IEvent for edit and NewEventFormGroupInput for create.
 */
type EventFormGroupInput = IEvent | PartialWithRequiredKeyOf<NewEvent>;

/**
 * Type that converts some properties for forms.
 */
type FormValueOf<T extends IEvent | NewEvent> = Omit<T, 'date'> & {
  date?: string | null;
};

type EventFormRawValue = FormValueOf<IEvent>;

type NewEventFormRawValue = FormValueOf<NewEvent>;

type EventFormDefaults = Pick<NewEvent, 'id' | 'date' | 'pendingJoiners' | 'approvedJoiners'>;

type EventFormGroupContent = {
  id: FormControl<EventFormRawValue['id'] | NewEvent['id']>;
  name: FormControl<EventFormRawValue['name']>;
  description: FormControl<EventFormRawValue['description']>;
  location: FormControl<EventFormRawValue['location']>;
  date: FormControl<EventFormRawValue['date']>;
  difficulty: FormControl<EventFormRawValue['difficulty']>;
  photo1: FormControl<EventFormRawValue['photo1']>;
  photo1ContentType: FormControl<EventFormRawValue['photo1ContentType']>;
  photo2: FormControl<EventFormRawValue['photo2']>;
  photo2ContentType: FormControl<EventFormRawValue['photo2ContentType']>;
  photo3: FormControl<EventFormRawValue['photo3']>;
  photo3ContentType: FormControl<EventFormRawValue['photo3ContentType']>;
  limit: FormControl<EventFormRawValue['limit']>;
  pendingJoiners: FormControl<EventFormRawValue['pendingJoiners']>;
  approvedJoiners: FormControl<EventFormRawValue['approvedJoiners']>;
};

export type EventFormGroup = FormGroup<EventFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class EventFormService {
  createEventFormGroup(event: EventFormGroupInput = { id: null }): EventFormGroup {
    const eventRawValue = this.convertEventToEventRawValue({
      ...this.getFormDefaults(),
      ...event,
    });
    return new FormGroup<EventFormGroupContent>({
      id: new FormControl(
        { value: eventRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      name: new FormControl(eventRawValue.name, {
        validators: [Validators.required],
      }),
      description: new FormControl(eventRawValue.description),
      location: new FormControl(eventRawValue.location, {
        validators: [Validators.required],
      }),
      date: new FormControl(eventRawValue.date, {
        validators: [Validators.required],
      }),
      difficulty: new FormControl(eventRawValue.difficulty, {
        validators: [Validators.required],
      }),
      photo1: new FormControl(eventRawValue.photo1),
      photo1ContentType: new FormControl(eventRawValue.photo1ContentType),
      photo2: new FormControl(eventRawValue.photo2),
      photo2ContentType: new FormControl(eventRawValue.photo2ContentType),
      photo3: new FormControl(eventRawValue.photo3),
      photo3ContentType: new FormControl(eventRawValue.photo3ContentType),
      limit: new FormControl(eventRawValue.limit),
      pendingJoiners: new FormControl(eventRawValue.pendingJoiners ?? []),
      approvedJoiners: new FormControl(eventRawValue.approvedJoiners ?? []),
    });
  }

  getEvent(form: EventFormGroup): IEvent | NewEvent {
    return this.convertEventRawValueToEvent(form.getRawValue() as EventFormRawValue | NewEventFormRawValue);
  }

  resetForm(form: EventFormGroup, event: EventFormGroupInput): void {
    const eventRawValue = this.convertEventToEventRawValue({ ...this.getFormDefaults(), ...event });
    form.reset(
      {
        ...eventRawValue,
        id: { value: eventRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */,
    );
  }

  private getFormDefaults(): EventFormDefaults {
    const currentTime = dayjs();

    return {
      id: null,
      date: currentTime,
      pendingJoiners: [],
      approvedJoiners: [],
    };
  }

  private convertEventRawValueToEvent(rawEvent: EventFormRawValue | NewEventFormRawValue): IEvent | NewEvent {
    return {
      ...rawEvent,
      date: dayjs(rawEvent.date, DATE_TIME_FORMAT),
    };
  }

  private convertEventToEventRawValue(
    event: IEvent | (Partial<NewEvent> & EventFormDefaults),
  ): EventFormRawValue | PartialWithRequiredKeyOf<NewEventFormRawValue> {
    return {
      ...event,
      date: event.date ? event.date.format(DATE_TIME_FORMAT) : undefined,
      pendingJoiners: event.pendingJoiners ?? [],
      approvedJoiners: event.approvedJoiners ?? [],
    };
  }
}
