import { Injectable } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';

import { ISupplier } from '../supplier.model';

type SupplierFormGroupContent = {
  id: FormControl<ISupplier['id'] | null | undefined>;

  name: FormControl<ISupplier['name'] | null | undefined>;
};

export type SupplierFormGroup = FormGroup<SupplierFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class SupplierFormService {
  createSupplierFormGroup(supplier: Partial<ISupplier> = {}): SupplierFormGroup {
    const supplierRawValue = supplier;
    return new FormGroup<SupplierFormGroupContent>({
      id: new FormControl(supplierRawValue.id),

      name: new FormControl(supplierRawValue.name, {
        validators: [Validators.required],
      }),
    });
  }

  resetForm(form: SupplierFormGroup, supplier: Partial<ISupplier> = {}): void {
    form.reset(supplier);
  }
}
