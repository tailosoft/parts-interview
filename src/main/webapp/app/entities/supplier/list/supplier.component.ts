import { Component, OnInit, ViewChild } from '@angular/core';
import { HttpErrorResponse, HttpHeaders, HttpResponse } from '@angular/common/http';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { Subscription } from 'rxjs';
import { filter, tap, switchMap } from 'rxjs/operators';
import { FilterMetadata, MessageService } from 'primeng/api';
import { ISupplier } from '../supplier.model';
import SharedModule from 'app/shared/shared.module';
import { SupplierService } from '../service/supplier.service';

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
import { Table } from 'primeng/table';

@Component({
  standalone: true,
  selector: 'jhi-supplier',
  templateUrl: './supplier.component.html',
  imports: [RouterModule, FormsModule, SharedModule],
})
export class SupplierComponent implements OnInit {
  suppliers?: ISupplier[];
  eventSubscriber?: Subscription;

  totalItems?: number;
  itemsPerPage!: number;
  loading!: boolean;

  @ViewChild('supplierTable', { static: true })
  supplierTable!: Table;

  private filtersDetails: { [_: string]: { type: string } } = {
    id: { type: 'number' },
    name: { type: 'string' },
  };

  constructor(
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
    this.activatedRoute.queryParams
      .pipe(
        tap(queryParams => fillTableFromQueryParams(this.supplierTable, queryParams, this.filtersDetails)),
        tap(() => (this.loading = true)),
        switchMap(() =>
          this.supplierService.query(
            lazyLoadEventToServerQueryParams(this.supplierTable.createLazyLoadMetadata(), undefined, this.filtersDetails)
          )
        ),
        // TODO add catchError inside switchMap in blueprint
        filter((res: HttpResponse<ISupplier[]>) => res.ok)
      )
      .subscribe(
        (res: HttpResponse<ISupplier[]>) => {
          this.paginateSuppliers(res.body!, res.headers);
          this.loading = false;
        },
        (err: HttpErrorResponse) => {
          this.onError(err.message);
          console.error(err);
          this.loading = false;
        }
      );
  }

  get filters(): { [s: string]: FilterMetadata } {
    return this.supplierTable.filters as { [s: string]: FilterMetadata };
  }

  onLazyLoadEvent(event: TableLazyLoadEvent): void {
    const queryParams = lazyLoadEventToRouterQueryParams(event, this.filtersDetails);
    this.router.navigate(['/supplier'], { queryParams });
  }

  delete(id: number): void {
    this.confirmationService.confirm({
      header: this.translateService.instant('entity.delete.title'),
      message: this.translateService.instant('partsApp.supplier.delete.question', { id }),
      accept: () => {
        this.supplierService.delete(id).subscribe(() => {
          this.router.navigate(['/supplier'], { queryParams: { r: Date.now() }, queryParamsHandling: 'merge' });
        });
      },
    });
  }

  trackId(index: number, item: ISupplier): number {
    return item.id;
  }

  protected paginateSuppliers(data: ISupplier[], headers: HttpHeaders): void {
    this.totalItems = Number(headers.get('X-Total-Count'));
    this.suppliers = data;
  }

  protected onError(errorMessage: string): void {
    this.messageService.add({ severity: 'error', summary: errorMessage });
  }
}
