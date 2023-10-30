import { Component, OnInit, OnDestroy } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import SharedModule from 'app/shared/shared.module';
import { ReactiveFormsModule } from '@angular/forms';
import { finalize, Observable, ReplaySubject } from 'rxjs';
import { LazySubscriber } from 'app/core/request/lazy-subscriber';
import { DropdownLazyLoadEvent } from 'primeng/dropdown';

import { PartSupplierFormService, PartSupplierFormGroup } from './part-supplier-form.service';
import { IPartSupplier } from '../part-supplier.model';
import { PartSupplierService } from '../service/part-supplier.service';
import { MessageService } from 'primeng/api';
import { IPart } from 'app/entities/part/part.model';
import { PartService } from 'app/entities/part/service/part.service';
import { ISupplier } from 'app/entities/supplier/supplier.model';
import { SupplierService } from 'app/entities/supplier/service/supplier.service';

@Component({
  standalone: true,
  selector: 'jhi-part-supplier-update',
  templateUrl: './part-supplier-update.component.html',
  imports: [SharedModule, ReactiveFormsModule],
})
export class PartSupplierUpdateComponent implements OnInit, OnDestroy {
  edit = false;
  isSaving = false;
  partOptionsSubscriber?: LazySubscriber<IPart>;
  partSelectedOptions: IPart[] | null = null;
  partFilterValue?: any;
  supplierOptionsSubscriber?: LazySubscriber<ISupplier>;
  supplierSelectedOptions: ISupplier[] | null = null;
  supplierFilterValue?: any;

  editForm: PartSupplierFormGroup = this.partSupplierFormService.createPartSupplierFormGroup();

  private onDestroySubject = new ReplaySubject(1);

  constructor(
    protected messageService: MessageService,
    protected partSupplierService: PartSupplierService,
    protected partSupplierFormService: PartSupplierFormService,
    protected partService: PartService,
    protected supplierService: SupplierService,
    protected activatedRoute: ActivatedRoute
  ) {}

  ngOnInit(): void {
    this.isSaving = false;

    this.partOptionsSubscriber = new LazySubscriber(req => this.partService.query(req), this.onDestroySubject);
    this.supplierOptionsSubscriber = new LazySubscriber(req => this.supplierService.query(req), this.onDestroySubject);

    this.activatedRoute.data.subscribe(({ partSupplier }) => {
      this.updateForm(partSupplier);
    });
  }

  ngOnDestroy(): void {
    this.onDestroySubject.next(undefined);
    this.onDestroySubject.complete();
  }

  onPartLazyLoadEvent(event: Partial<DropdownLazyLoadEvent>): void {
    this.partOptionsSubscriber!.filter({ first: event.first, last: event.last, 'name.contains': event.filter });
  }

  onSupplierLazyLoadEvent(event: Partial<DropdownLazyLoadEvent>): void {
    this.supplierOptionsSubscriber!.filter({ first: event.first, last: event.last, 'name.contains': event.filter });
  }

  updateForm(partSupplier: IPartSupplier | null): void {
    if (partSupplier) {
      this.edit = true;
      this.editForm.reset({ ...partSupplier }, { emitEvent: false, onlySelf: true });
      this.partOptionsSubscriber!.options = [partSupplier.part];
      this.partFilterValue = partSupplier.part.name;
      this.supplierOptionsSubscriber!.options = [partSupplier.supplier];
      this.supplierFilterValue = partSupplier.supplier.name;
    } else {
      this.edit = false;
      this.editForm.reset({});
    }
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    if (this.editForm.valid) {
      this.isSaving = true;
      const partSupplier = this.editForm.value;
      if (this.edit) {
        this.subscribeToSaveResponse(this.partSupplierService.update(partSupplier as IPartSupplier));
      } else {
        this.subscribeToSaveResponse(this.partSupplierService.create(partSupplier as IPartSupplier));
      }
    } else {
      this.editForm.markAllAsTouched();
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IPartSupplier>>): void {
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
