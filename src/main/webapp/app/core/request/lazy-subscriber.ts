import { HttpResponse } from '@angular/common/http';
import { catchError, debounceTime, EMPTY, filter, map, Observable, pairwise, startWith, Subject, switchMap, takeUntil, tap } from 'rxjs';
import { ITEMS_PER_PAGE } from '../../config/pagination.constants';

type DropdownFilterWithExtras = {
  first: number;
  last: number;
  [_: string]: string | number | undefined;
};

type QueryParams = {
  page: number;
  size: number;
  [_: string]: string | number | undefined;
};

export class LazySubscriber<T> {
  options: (T | undefined)[] = [];
  itemsPerPage: number;
  maxItems: number;
  filterSubject = new Subject<{}>();

  constructor(
    query: (_: QueryParams) => Observable<HttpResponse<T[]>>,
    destroy$: Observable<unknown>,
    itemsPerPage = ITEMS_PER_PAGE,
    maxItems = 10000
  ) {
    this.itemsPerPage = itemsPerPage;
    this.maxItems = maxItems;
    this.filterSubject
      .pipe(
        takeUntil(destroy$),
        debounceTime(100),
        startWith({}),
        map((event: Partial<DropdownFilterWithExtras>) => ({
          ...event,
          first: event.first ?? 0,
          last: event.last && event.last > 0 ? event.last : this.itemsPerPage,
        })),
        pairwise(),
        // we clear the options if filter have changed
        tap(([previousEvent, newEvent]: DropdownFilterWithExtras[]) => {
          const { first: pf, last: pl, ...previousFilters } = previousEvent;
          const { first, last, ...filters } = newEvent;
          const keys = Object.keys(filters);
          // because dropdown filter sometimes return null and other undefined
          if (
            Object.keys(previousFilters).length !== keys.length ||
            keys.some(k => (previousFilters[k] ?? undefined) !== (filters[k] ?? undefined))
          ) {
            this.options = [];
          }
        }),
        map(([_, newEvent]) => newEvent),
        // at anytime, we would want to have both first and last in options, if any of them is missing we should load it
        filter(event => this.options[event.first] === undefined || this.options[event.last - 1] === undefined),
        switchMap(event => {
          const { first, last, ...filters } = event;
          if (last - first > this.itemsPerPage) {
            this.itemsPerPage = last - first;
          }
          const page = this.options[first] === undefined ? Math.floor(first / this.itemsPerPage) : Math.floor(last / this.itemsPerPage);
          return query({ ...filters, page, size: this.itemsPerPage }).pipe(
            catchError(() => {
              this.options = [];
              return EMPTY;
            }),
            map(res => ({ page, res }))
          );
        })
      )
      .subscribe({
        next: ({ page, res }) => {
          const options = this.options.length
            ? [...this.options]
            : (Array.from({ length: Math.min(Number(res.headers.get('X-Total-Count')), this.maxItems) }) as [T | undefined]);
          options.splice(page * this.itemsPerPage, res.body!.length, ...res.body!);
          this.options = options;
        },
      });
  }

  filter(event: any): void {
    this.filterSubject.next(event);
  }
}
