import { Injectable } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';

import { ICoffee, NewCoffee } from '../coffee.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts ICoffee for edit and NewCoffeeFormGroupInput for create.
 */
type CoffeeFormGroupInput = ICoffee | PartialWithRequiredKeyOf<NewCoffee>;

type CoffeeFormDefaults = Pick<NewCoffee, 'id'>;

type CoffeeFormGroupContent = {
  id: FormControl<ICoffee['id'] | NewCoffee['id']>;
  fieldName: FormControl<ICoffee['fieldName']>;
  name: FormControl<ICoffee['name']>;
};

export type CoffeeFormGroup = FormGroup<CoffeeFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class CoffeeFormService {
  createCoffeeFormGroup(coffee: CoffeeFormGroupInput = { id: null }): CoffeeFormGroup {
    const coffeeRawValue = {
      ...this.getFormDefaults(),
      ...coffee,
    };
    return new FormGroup<CoffeeFormGroupContent>({
      id: new FormControl(
        { value: coffeeRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        }
      ),
      fieldName: new FormControl(coffeeRawValue.fieldName),
      name: new FormControl(coffeeRawValue.name, {
        validators: [Validators.required, Validators.maxLength(10)],
      }),
    });
  }

  getCoffee(form: CoffeeFormGroup): ICoffee | NewCoffee {
    return form.getRawValue() as ICoffee | NewCoffee;
  }

  resetForm(form: CoffeeFormGroup, coffee: CoffeeFormGroupInput): void {
    const coffeeRawValue = { ...this.getFormDefaults(), ...coffee };
    form.reset(
      {
        ...coffeeRawValue,
        id: { value: coffeeRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */
    );
  }

  private getFormDefaults(): CoffeeFormDefaults {
    return {
      id: null,
    };
  }
}
