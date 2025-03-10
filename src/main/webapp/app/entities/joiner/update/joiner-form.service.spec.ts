import { TestBed } from '@angular/core/testing';

import { sampleWithNewData, sampleWithRequiredData } from '../joiner.test-samples';

import { JoinerFormService } from './joiner-form.service';

describe('Joiner Form Service', () => {
  let service: JoinerFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(JoinerFormService);
  });

  describe('Service methods', () => {
    describe('createJoinerFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createJoinerFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            fullName: expect.any(Object),
            email: expect.any(Object),
            phone: expect.any(Object),
            status: expect.any(Object),
            photo1: expect.any(Object),
            gender: expect.any(Object),
            point: expect.any(Object),
            internalUser: expect.any(Object),
            pendingEvents: expect.any(Object),
            aprovedEvents: expect.any(Object),
          }),
        );
      });

      it('passing IJoiner should create a new form with FormGroup', () => {
        const formGroup = service.createJoinerFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            fullName: expect.any(Object),
            email: expect.any(Object),
            phone: expect.any(Object),
            status: expect.any(Object),
            photo1: expect.any(Object),
            gender: expect.any(Object),
            point: expect.any(Object),
            internalUser: expect.any(Object),
            pendingEvents: expect.any(Object),
            aprovedEvents: expect.any(Object),
          }),
        );
      });
    });

    describe('getJoiner', () => {
      it('should return NewJoiner for default Joiner initial value', () => {
        const formGroup = service.createJoinerFormGroup(sampleWithNewData);

        const joiner = service.getJoiner(formGroup) as any;

        expect(joiner).toMatchObject(sampleWithNewData);
      });

      it('should return NewJoiner for empty Joiner initial value', () => {
        const formGroup = service.createJoinerFormGroup();

        const joiner = service.getJoiner(formGroup) as any;

        expect(joiner).toMatchObject({});
      });

      it('should return IJoiner', () => {
        const formGroup = service.createJoinerFormGroup(sampleWithRequiredData);

        const joiner = service.getJoiner(formGroup) as any;

        expect(joiner).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IJoiner should not enable id FormControl', () => {
        const formGroup = service.createJoinerFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewJoiner should disable id FormControl', () => {
        const formGroup = service.createJoinerFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
