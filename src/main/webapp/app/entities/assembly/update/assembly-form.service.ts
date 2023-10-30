import { Injectable } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';

import { IAssembly } from '../assembly.model';

type AssemblyFormGroupContent = {
  parent: FormControl<IAssembly['parent'] | null | undefined>;
  child: FormControl<IAssembly['child'] | null | undefined>;

  quantity: FormControl<IAssembly['quantity'] | null | undefined>;
};

export type AssemblyFormGroup = FormGroup<AssemblyFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class AssemblyFormService {
  createAssemblyFormGroup(assembly: Partial<IAssembly> = {}): AssemblyFormGroup {
    const assemblyRawValue = assembly;
    return new FormGroup<AssemblyFormGroupContent>({
      parent: new FormControl(assemblyRawValue.parent, {
        validators: [Validators.required],
      }),
      child: new FormControl(assemblyRawValue.child, {
        validators: [Validators.required],
      }),

      quantity: new FormControl(assemblyRawValue.quantity, {
        validators: [Validators.required],
      }),
    });
  }

  resetForm(form: AssemblyFormGroup, assembly: Partial<IAssembly> = {}): void {
    form.reset(assembly);
  }
}
