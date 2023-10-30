import { Component, OnInit, ViewChild, OnDestroy } from '@angular/core';
import { HttpErrorResponse, HttpHeaders, HttpResponse } from '@angular/common/http';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { LazySubscriber } from 'app/core/request/lazy-subscriber';
import { DropdownLazyLoadEvent } from 'primeng/dropdown';
import { Subscription, ReplaySubject } from 'rxjs';
import { filter, tap, switchMap } from 'rxjs/operators';
import { FilterMetadata, MessageService } from 'primeng/api';
import { IPartSupplier } from '../part-supplier.model';
import SharedModule from 'app/shared/shared.module';
import { PartSupplierService } from '../service/part-supplier.service';

import { ITEMS_PER_PAGE } from 'app/config/pagination.constants';
import { FormsModule } from '@angular/forms';
import {
  lazyLoadEventToServerQueryParams,
  lazyLoadEventToRouterQueryParams,
  fillTableFromQueryParams,
} from 'app/core/request/request-util';
import { ConfirmationService } from 'primeng/api';
import { TableLazyLoadEvent } from 'primeng/table';
import { TranslateService } from '@ngx-translate/core';
import { IPart } from 'app/entities/part/part.model';
import { PartService } from 'app/entities/part/service/part.service';
import { ISupplier } from 'app/entities/supplier/supplier.model';
import { SupplierService } from 'app/entities/supplier/service/supplier.service';
import { Table } from 'primeng/table';

@Component({
  standalone: true,
  selector: 'jhi-part-supplier',
  templateUrl: './part-supplier.component.html',
  imports: [RouterModule, FormsModule, SharedModule],
})
export class PartSupplierComponent implements OnInit, OnDestroy {
  partSuppliers?: IPartSupplier[];
  eventSubscriber?: Subscription;
  partOptionsSubscriber?: LazySubscriber<IPart>;
  partSelectedOptions: IPart[] | null = null;
  supplierOptionsSubscriber?: LazySubscriber<ISupplier>;
  supplierSelectedOptions: ISupplier[] | null = null;

  totalItems?: number;
  itemsPerPage!: number;
  loading!: boolean;

  @ViewChild('partSupplierTable', { static: true })
  partSupplierTable!: Table;

  private filtersDetails: { [_: string]: { type: string } } = {
    price: { type: 'number' },
    ['partId']: { type: 'number' },
    ['supplierId']: { type: 'number' },
  };
  private onDestroySubject = new ReplaySubject(1);

  constructor(
    protected partSupplierService: PartSupplierService,
    protected partService: PartService,
    protected supplierService: SupplierService,
    protected messageService: MessageService,
    protected activatedRoute: ActivatedRoute,
    protected router: Router,
    protected confirmationService: ConfirmationService,
    protected translateService: TranslateService
  ) {
    this.itemsPerPage = ITEMS_PER_PAGE;
    this.loading = true;
  }

  ngOnInit(): void {
    this.partOptionsSubscriber = new LazySubscriber(req => this.partService.query(req), this.onDestroySubject);
    this.supplierOptionsSubscriber = new LazySubscriber(req => this.supplierService.query(req), this.onDestroySubject);

    this.activatedRoute.queryParams
      .pipe(
        tap(queryParams => fillTableFromQueryParams(this.partSupplierTable, queryParams, this.filtersDetails)),
        tap(() => (this.loading = true)),
        switchMap(() =>
          this.partSupplierService.query(
            lazyLoadEventToServerQueryParams(this.partSupplierTable.createLazyLoadMetadata(), undefined, this.filtersDetails)
          )
        ),
        // TODO add catchError inside switchMap in blueprint
        filter((res: HttpResponse<IPartSupplier[]>) => res.ok)
      )
      .subscribe(
        (res: HttpResponse<IPartSupplier[]>) => {
          this.paginatePartSuppliers(res.body!, res.headers);
          this.loading = false;
        },
        (err: HttpErrorResponse) => {
          this.onError(err.message);
          console.error(err);
          this.loading = false;
        }
      );
  }
  ngOnDestroy(): void {
    this.onDestroySubject.next(undefined);
    this.onDestroySubject.complete();
  }

  get filters(): { [s: string]: FilterMetadata } {
    return this.partSupplierTable.filters as { [s: string]: FilterMetadata };
  }

  onLazyLoadEvent(event: TableLazyLoadEvent): void {
    const queryParams = lazyLoadEventToRouterQueryParams(event, this.filtersDetails);
    this.router.navigate(['/part-supplier'], { queryParams });
  }

  delete(partId: number, supplierId: number): void {
    this.confirmationService.confirm({
      header: this.translateService.instant('entity.delete.title'),
      message: this.translateService.instant('partsApp.partSupplier.delete.question', { id: `${partId} , ${supplierId}` }),
      accept: () => {
        this.partSupplierService.delete(partId, supplierId).subscribe(() => {
          this.router.navigate(['/part-supplier'], { queryParams: { r: Date.now() }, queryParamsHandling: 'merge' });
        });
      },
    });
  }
  onPartLazyLoadEvent(event: DropdownLazyLoadEvent): void {
    this.partOptionsSubscriber!.filter({ first: event.first, last: event.last, 'name.contains': event.filter });
  }
  onSupplierLazyLoadEvent(event: DropdownLazyLoadEvent): void {
    this.supplierOptionsSubscriber!.filter({ first: event.first, last: event.last, 'name.contains': event.filter });
  }

  trackId(index: number, item: IPartSupplier): string {
    return `${item.part.id},${item.supplier.id}`;
  }

  protected paginatePartSuppliers(data: IPartSupplier[], headers: HttpHeaders): void {
    this.totalItems = Number(headers.get('X-Total-Count'));
    this.partSuppliers = data;
  }

  protected onError(errorMessage: string): void {
    this.messageService.add({ severity: 'error', summary: errorMessage });
  }
}
