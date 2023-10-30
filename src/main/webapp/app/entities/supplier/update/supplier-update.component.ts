import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import SharedModule from 'app/shared/shared.module';
import { ReactiveFormsModule } from '@angular/forms';
import { finalize, Observable } from 'rxjs';

import { SupplierFormService, SupplierFormGroup } from './supplier-form.service';
import { ISupplier } from '../supplier.model';
import { SupplierService } from '../service/supplier.service';

@Component({
  standalone: true,
  selector: 'jhi-supplier-update',
  templateUrl: './supplier-update.component.html',
  imports: [SharedModule, ReactiveFormsModule],
})
export class SupplierUpdateComponent implements OnInit {
  isSaving = false;

  editForm: SupplierFormGroup = this.supplierFormService.createSupplierFormGroup();

  constructor(
    protected supplierService: SupplierService,
    protected supplierFormService: SupplierFormService,
    protected activatedRoute: ActivatedRoute
  ) {}

  ngOnInit(): void {
    this.isSaving = false;

    this.activatedRoute.data.subscribe(({ supplier }) => {
      this.updateForm(supplier);
    });
  }

  updateForm(supplier: ISupplier | null): void {
    if (supplier) {
      this.editForm.reset({ ...supplier }, { emitEvent: false, onlySelf: true });
    } else {
      this.editForm.reset({});
    }
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    if (this.editForm.valid) {
      this.isSaving = true;
      const supplier = this.editForm.value;
      if (supplier.id) {
        this.subscribeToSaveResponse(this.supplierService.update(supplier as ISupplier));
      } else {
        this.subscribeToSaveResponse(this.supplierService.create(supplier as ISupplier));
      }
    } else {
      this.editForm.markAllAsTouched();
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<ISupplier>>): void {
    result.pipe(finalize(() => this.onSaveFinalize())).subscribe({
      next: () => this.onSaveSuccess(),
    });
  }

  protected onSaveSuccess(): void {
    this.previousState();
  }

  protected onSaveFinalize(): void {
    this.isSaving = false;
  }
}
