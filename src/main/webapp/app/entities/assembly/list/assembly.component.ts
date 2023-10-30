import { Component, OnInit, ViewChild, OnDestroy } from '@angular/core';
import { HttpErrorResponse, HttpHeaders, HttpResponse } from '@angular/common/http';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { LazySubscriber } from 'app/core/request/lazy-subscriber';
import { DropdownLazyLoadEvent } from 'primeng/dropdown';
import { Subscription, ReplaySubject } from 'rxjs';
import { filter, tap, switchMap } from 'rxjs/operators';
import { FilterMetadata, MessageService } from 'primeng/api';
import { IAssembly } from '../assembly.model';
import SharedModule from 'app/shared/shared.module';
import { AssemblyService } from '../service/assembly.service';

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
import { Table } from 'primeng/table';

@Component({
  standalone: true,
  selector: 'jhi-assembly',
  templateUrl: './assembly.component.html',
  imports: [RouterModule, FormsModule, SharedModule],
})
export class AssemblyComponent implements OnInit, OnDestroy {
  assemblies?: IAssembly[];
  eventSubscriber?: Subscription;
  parentOptionsSubscriber?: LazySubscriber<IPart>;
  parentSelectedOptions: IPart[] | null = null;
  childOptionsSubscriber?: LazySubscriber<IPart>;
  childSelectedOptions: IPart[] | null = null;

  totalItems?: number;
  itemsPerPage!: number;
  loading!: boolean;

  @ViewChild('assemblyTable', { static: true })
  assemblyTable!: Table;

  private filtersDetails: { [_: string]: { type: string } } = {
    quantity: { type: 'number' },
    ['parentId']: { type: 'number' },
    ['childId']: { type: 'number' },
  };
  private onDestroySubject = new ReplaySubject(1);

  constructor(
    protected assemblyService: AssemblyService,
    protected partService: PartService,
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
    this.parentOptionsSubscriber = new LazySubscriber(req => this.partService.query(req), this.onDestroySubject);
    this.childOptionsSubscriber = new LazySubscriber(req => this.partService.query(req), this.onDestroySubject);

    this.activatedRoute.queryParams
      .pipe(
        tap(queryParams => fillTableFromQueryParams(this.assemblyTable, queryParams, this.filtersDetails)),
        tap(() => (this.loading = true)),
        switchMap(() =>
          this.assemblyService.query(
            lazyLoadEventToServerQueryParams(this.assemblyTable.createLazyLoadMetadata(), undefined, this.filtersDetails)
          )
        ),
        // TODO add catchError inside switchMap in blueprint
        filter((res: HttpResponse<IAssembly[]>) => res.ok)
      )
      .subscribe(
        (res: HttpResponse<IAssembly[]>) => {
          this.paginateAssemblies(res.body!, res.headers);
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
    return this.assemblyTable.filters as { [s: string]: FilterMetadata };
  }

  onLazyLoadEvent(event: TableLazyLoadEvent): void {
    const queryParams = lazyLoadEventToRouterQueryParams(event, this.filtersDetails);
    this.router.navigate(['/assembly'], { queryParams });
  }

  delete(parentId: number, childId: number): void {
    this.confirmationService.confirm({
      header: this.translateService.instant('entity.delete.title'),
      message: this.translateService.instant('partsApp.assembly.delete.question', { id: `${parentId} , ${childId}` }),
      accept: () => {
        this.assemblyService.delete(parentId, childId).subscribe(() => {
          this.router.navigate(['/assembly'], { queryParams: { r: Date.now() }, queryParamsHandling: 'merge' });
        });
      },
    });
  }
  onParentLazyLoadEvent(event: DropdownLazyLoadEvent): void {
    this.parentOptionsSubscriber!.filter({ first: event.first, last: event.last, 'name.contains': event.filter });
  }
  onChildLazyLoadEvent(event: DropdownLazyLoadEvent): void {
    this.childOptionsSubscriber!.filter({ first: event.first, last: event.last, 'name.contains': event.filter });
  }

  trackId(index: number, item: IAssembly): string {
    return `${item.parent.id},${item.child.id}`;
  }

  protected paginateAssemblies(data: IAssembly[], headers: HttpHeaders): void {
    this.totalItems = Number(headers.get('X-Total-Count'));
    this.assemblies = data;
  }

  protected onError(errorMessage: string): void {
    this.messageService.add({ severity: 'error', summary: errorMessage });
  }
}
