import { TestBed } from '@angular/core/testing';

import { sampleWithRequiredData, sampleWithNewData } from '../coffee.test-samples';

import { CoffeeFormService } from './coffee-form.service';

describe('Coffee Form Service', () => {
  let service: CoffeeFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(CoffeeFormService);
  });

  describe('Service methods', () => {
    describe('createCoffeeFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createCoffeeFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            fieldName: expect.any(Object),
            name: expect.any(Object),
          })
        );
      });

      it('passing ICoffee should create a new form with FormGroup', () => {
        const formGroup = service.createCoffeeFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            fieldName: expect.any(Object),
            name: expect.any(Object),
          })
        );
      });
    });

    describe('getCoffee', () => {
      it('should return NewCoffee for default Coffee initial value', () => {
        // eslint-disable-next-line @typescript-eslint/no-unused-vars
        const formGroup = service.createCoffeeFormGroup(sampleWithNewData);

        const coffee = service.getCoffee(formGroup) as any;

        expect(coffee).toMatchObject(sampleWithNewData);
      });

      it('should return NewCoffee for empty Coffee initial value', () => {
        const formGroup = service.createCoffeeFormGroup();

        const coffee = service.getCoffee(formGroup) as any;

        expect(coffee).toMatchObject({});
      });

      it('should return ICoffee', () => {
        const formGroup = service.createCoffeeFormGroup(sampleWithRequiredData);

        const coffee = service.getCoffee(formGroup) as any;

        expect(coffee).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing ICoffee should not enable id FormControl', () => {
        const formGroup = service.createCoffeeFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewCoffee should disable id FormControl', () => {
        const formGroup = service.createCoffeeFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
