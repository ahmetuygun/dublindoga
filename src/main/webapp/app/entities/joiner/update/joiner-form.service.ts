import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import { IJoiner, NewJoiner } from '../joiner.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IJoiner for edit and NewJoinerFormGroupInput for create.
 */
type JoinerFormGroupInput = IJoiner | PartialWithRequiredKeyOf<NewJoiner>;

type JoinerFormDefaults = Pick<NewJoiner, 'id' | 'pendingEvents' | 'aprovedEvents'>;

type JoinerFormGroupContent = {
  id: FormControl<IJoiner['id'] | NewJoiner['id']>;
  fullName: FormControl<IJoiner['fullName']>;
  email: FormControl<IJoiner['email']>;
  phone: FormControl<IJoiner['phone']>;
  status: FormControl<IJoiner['status']>;
  photo1: FormControl<IJoiner['photo1']>;
  photo1ContentType: FormControl<IJoiner['photo1ContentType']>;
  gender: FormControl<IJoiner['gender']>;
  point: FormControl<IJoiner['point']>;
  internalUser: FormControl<IJoiner['internalUser']>;
  pendingEvents: FormControl<IJoiner['pendingEvents']>;
  aprovedEvents: FormControl<IJoiner['aprovedEvents']>;
};

export type JoinerFormGroup = FormGroup<JoinerFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class JoinerFormService {
  createJoinerFormGroup(joiner: JoinerFormGroupInput = { id: null }): JoinerFormGroup {
    const joinerRawValue = {
      ...this.getFormDefaults(),
      ...joiner,
    };
    return new FormGroup<JoinerFormGroupContent>({
      id: new FormControl(
        { value: joinerRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      fullName: new FormControl(joinerRawValue.fullName, {
        validators: [Validators.required],
      }),
      email: new FormControl(joinerRawValue.email, {
        validators: [Validators.required],
      }),
      phone: new FormControl(joinerRawValue.phone),
      status: new FormControl(joinerRawValue.status, {
        validators: [Validators.required],
      }),
      photo1: new FormControl(joinerRawValue.photo1),
      photo1ContentType: new FormControl(joinerRawValue.photo1ContentType),
      gender: new FormControl(joinerRawValue.gender),
      point: new FormControl(joinerRawValue.point),
      internalUser: new FormControl(joinerRawValue.internalUser),
      pendingEvents: new FormControl(joinerRawValue.pendingEvents ?? []),
      aprovedEvents: new FormControl(joinerRawValue.aprovedEvents ?? []),
    });
  }

  getJoiner(form: JoinerFormGroup): IJoiner | NewJoiner {
    return form.getRawValue() as IJoiner | NewJoiner;
  }

  resetForm(form: JoinerFormGroup, joiner: JoinerFormGroupInput): void {
    const joinerRawValue = { ...this.getFormDefaults(), ...joiner };
    form.reset(
      {
        ...joinerRawValue,
        id: { value: joinerRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */,
    );
  }

  private getFormDefaults(): JoinerFormDefaults {
    return {
      id: null,
      pendingEvents: [],
      aprovedEvents: [],
    };
  }
}
