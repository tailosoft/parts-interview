import { Injectable } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';

import { IPart } from '../part.model';

type PartFormGroupContent = {
  id: FormControl<IPart['id'] | null | undefined>;

  name: FormControl<IPart['name'] | null | undefined>;
  assemblyCost: FormControl<IPart['assemblyCost'] | null | undefined>;
  bestPrice: FormControl<IPart['bestPrice'] | null | undefined>;
  bestSupplier: FormControl<IPart['bestSupplier'] | null | undefined>;
};

export type PartFormGroup = FormGroup<PartFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class PartFormService {
  createPartFormGroup(part: Partial<IPart> = {}): PartFormGroup {
    const partRawValue = part;
    return new FormGroup<PartFormGroupContent>({
      id: new FormControl(partRawValue.id),

      name: new FormControl(partRawValue.name, {
        validators: [Validators.required],
      }),
      assemblyCost: new FormControl(partRawValue.assemblyCost),
      bestPrice: new FormControl(partRawValue.bestPrice),
      bestSupplier: new FormControl(partRawValue.bestSupplier),
    });
  }

  resetForm(form: PartFormGroup, part: Partial<IPart> = {}): void {
    form.reset(part);
  }
}
