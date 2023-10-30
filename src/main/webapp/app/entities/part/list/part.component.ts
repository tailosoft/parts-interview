import { Component, OnInit, ViewChild, OnDestroy } from '@angular/core';
import { HttpErrorResponse, HttpHeaders, HttpResponse } from '@angular/common/http';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { LazySubscriber } from 'app/core/request/lazy-subscriber';
import { DropdownLazyLoadEvent } from 'primeng/dropdown';
import { Subscription, ReplaySubject } from 'rxjs';
import { filter, tap, switchMap } from 'rxjs/operators';
import { FilterMetadata, MessageService } from 'primeng/api';
import { IPart } from '../part.model';
import SharedModule from 'app/shared/shared.module';
import { PartService } from '../service/part.service';

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
import { ISupplier } from 'app/entities/supplier/supplier.model';
import { SupplierService } from 'app/entities/supplier/service/supplier.service';
import { Table } from 'primeng/table';

@Component({
  standalone: true,
  selector: 'jhi-part',
  templateUrl: './part.component.html',
  imports: [RouterModule, FormsModule, SharedModule],
})
export class PartComponent implements OnInit, OnDestroy {
  parts?: IPart[];
  eventSubscriber?: Subscription;
  bestSupplierOptionsSubscriber?: LazySubscriber<ISupplier>;
  bestSupplierSelectedOptions: ISupplier[] | null = null;

  totalItems?: number;
  itemsPerPage!: number;
  loading!: boolean;

  @ViewChild('partTable', { static: true })
  partTable!: Table;

  private filtersDetails: { [_: string]: { type: string } } = {
    id: { type: 'number' },
    name: { type: 'string' },
    assemblyCost: { type: 'number' },
    bestPrice: { type: 'number' },
    ['bestSupplierId']: { type: 'number' },
  };
  private onDestroySubject = new ReplaySubject(1);

  constructor(
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
    this.bestSupplierOptionsSubscriber = new LazySubscriber(req => this.supplierService.query(req), this.onDestroySubject);

    this.activatedRoute.queryParams
      .pipe(
        tap(queryParams => fillTableFromQueryParams(this.partTable, queryParams, this.filtersDetails)),
        tap(() => (this.loading = true)),
        switchMap(() =>
          this.partService.query(lazyLoadEventToServerQueryParams(this.partTable.createLazyLoadMetadata(), undefined, this.filtersDetails))
        ),
        // TODO add catchError inside switchMap in blueprint
        filter((res: HttpResponse<IPart[]>) => res.ok)
      )
      .subscribe(
        (res: HttpResponse<IPart[]>) => {
          this.paginateParts(res.body!, res.headers);
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
    return this.partTable.filters as { [s: string]: FilterMetadata };
  }

  onLazyLoadEvent(event: TableLazyLoadEvent): void {
    const queryParams = lazyLoadEventToRouterQueryParams(event, this.filtersDetails);
    this.router.navigate(['/part'], { queryParams });
  }

  delete(id: number): void {
    this.confirmationService.confirm({
      header: this.translateService.instant('entity.delete.title'),
      message: this.translateService.instant('partsApp.part.delete.question', { id }),
      accept: () => {
        this.partService.delete(id).subscribe(() => {
          this.router.navigate(['/part'], { queryParams: { r: Date.now() }, queryParamsHandling: 'merge' });
        });
      },
    });
  }
  onBestSupplierLazyLoadEvent(event: DropdownLazyLoadEvent): void {
    this.bestSupplierOptionsSubscriber!.filter({ first: event.first, last: event.last, 'name.contains': event.filter });
  }

  trackId(index: number, item: IPart): number {
    return item.id;
  }

  protected paginateParts(data: IPart[], headers: HttpHeaders): void {
    this.totalItems = Number(headers.get('X-Total-Count'));
    this.parts = data;
  }

  protected onError(errorMessage: string): void {
    this.messageService.add({ severity: 'error', summary: errorMessage });
  }
}
