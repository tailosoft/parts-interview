import { Injectable } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';

import { IPartSupplier } from '../part-supplier.model';

type PartSupplierFormGroupContent = {
  part: FormControl<IPartSupplier['part'] | null | undefined>;
  supplier: FormControl<IPartSupplier['supplier'] | null | undefined>;

  price: FormControl<IPartSupplier['price'] | null | undefined>;
};

export type PartSupplierFormGroup = FormGroup<PartSupplierFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class PartSupplierFormService {
  createPartSupplierFormGroup(partSupplier: Partial<IPartSupplier> = {}): PartSupplierFormGroup {
    const partSupplierRawValue = partSupplier;
    return new FormGroup<PartSupplierFormGroupContent>({
      part: new FormControl(partSupplierRawValue.part, {
        validators: [Validators.required],
      }),
      supplier: new FormControl(partSupplierRawValue.supplier, {
        validators: [Validators.required],
      }),

      price: new FormControl(partSupplierRawValue.price, {
        validators: [Validators.required],
      }),
    });
  }

  resetForm(form: PartSupplierFormGroup, partSupplier: Partial<IPartSupplier> = {}): void {
    form.reset(partSupplier);
  }
}
