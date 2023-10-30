import { Component, OnInit, OnDestroy } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import SharedModule from 'app/shared/shared.module';
import { ReactiveFormsModule } from '@angular/forms';
import { finalize, Observable, ReplaySubject } from 'rxjs';
import { LazySubscriber } from 'app/core/request/lazy-subscriber';
import { DropdownLazyLoadEvent } from 'primeng/dropdown';

import { PartFormService, PartFormGroup } from './part-form.service';
import { IPart } from '../part.model';
import { PartService } from '../service/part.service';
import { MessageService } from 'primeng/api';
import { ISupplier } from 'app/entities/supplier/supplier.model';
import { SupplierService } from 'app/entities/supplier/service/supplier.service';

@Component({
  standalone: true,
  selector: 'jhi-part-update',
  templateUrl: './part-update.component.html',
  imports: [SharedModule, ReactiveFormsModule],
})
export class PartUpdateComponent implements OnInit, OnDestroy {
  isSaving = false;
  bestSupplierOptionsSubscriber?: LazySubscriber<ISupplier>;
  bestSupplierSelectedOptions: ISupplier[] | null = null;
  bestSupplierFilterValue?: any;

  editForm: PartFormGroup = this.partFormService.createPartFormGroup();

  private onDestroySubject = new ReplaySubject(1);

  constructor(
    protected messageService: MessageService,
    protected partService: PartService,
    protected partFormService: PartFormService,
    protected supplierService: SupplierService,
    protected activatedRoute: ActivatedRoute
  ) {}

  ngOnInit(): void {
    this.isSaving = false;

    this.bestSupplierOptionsSubscriber = new LazySubscriber(req => this.supplierService.query(req), this.onDestroySubject);

    this.activatedRoute.data.subscribe(({ part }) => {
      this.updateForm(part);
    });
  }

  ngOnDestroy(): void {
    this.onDestroySubject.next(undefined);
    this.onDestroySubject.complete();
  }

  onBestSupplierLazyLoadEvent(event: Partial<DropdownLazyLoadEvent>): void {
    this.bestSupplierOptionsSubscriber!.filter({ first: event.first, last: event.last, 'name.contains': event.filter });
  }

  updateForm(part: IPart | null): void {
    if (part) {
      this.editForm.reset({ ...part }, { emitEvent: false, onlySelf: true });
      if (part.bestSupplier) {
        this.bestSupplierOptionsSubscriber!.options = [part.bestSupplier];
        this.bestSupplierFilterValue = part.bestSupplier.name;
      }
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
      const part = this.editForm.value;
      if (part.id) {
        this.subscribeToSaveResponse(this.partService.update(part as IPart));
      } else {
        this.subscribeToSaveResponse(this.partService.create(part as IPart));
      }
    } else {
      this.editForm.markAllAsTouched();
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IPart>>): void {
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
