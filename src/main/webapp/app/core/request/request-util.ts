import { HttpParams } from '@angular/common/http';
import { FilterMatchMode, FilterMetadata } from 'primeng/api';
import { TableLazyLoadEvent } from 'primeng/table';
import { ITEMS_PER_PAGE } from 'app/config/pagination.constants';
import { flatten, unflatten } from 'flat';
import { Params } from '@angular/router';
import { Table } from 'primeng/table';
import { DatePipe } from '@angular/common';
import { DATE_FORMAT } from 'app/config/input.constants';

type FiltersDetails = { [_: string]: { type: string } | undefined };

const datePipe = new DatePipe('en');

export const createRequestOption = (req?: any): HttpParams => {
  let options: HttpParams = new HttpParams();

  if (req) {
    Object.keys(req).forEach(key => {
      if (key !== 'sort' && req[key] != null) {
        for (const value of [].concat(req[key]).filter(v => v !== '')) {
          options = options.append(key, value);
        }
      }
    });

    if (req.sort) {
      req.sort.forEach((val: string) => {
        options = options.append('sort', val);
      });
    }
  }

  return options;
};

export const lazyLoadEventToServerQueryParams = (
  event?: TableLazyLoadEvent,
  globalFilter = 'globalFilter',
  filtersDetails: FiltersDetails = {}
): { [_: string]: any } => {
  const params: { [index: string]: any } = {};
  if (event) {
    if (event.filters) {
      for (const filterName of Object.keys(event.filters)) {
        let filters: FilterMetadata[];
        if (!Array.isArray(event.filters[filterName])) {
          filters = [event.filters[filterName]! as FilterMetadata];
        } else {
          filters = event.filters[filterName] as FilterMetadata[];
        }
        filters.forEach(filter => {
          if (filter.value !== undefined && filter.value !== null) {
            let matchMode = filter.matchMode;
            if (matchMode) {
              if (matchModes.has(matchMode)) {
                matchMode = matchModes.get(matchMode);
              }
              params[`${filterName}.${matchMode!}`] = serializeFilter(filter.value, filtersDetails[filterName]?.type);
            } else {
              params[filterName] = filter.value;
            }
          }
        });
      }
    }
    if (event.globalFilter && globalFilter) {
      params[globalFilter] = event.globalFilter;
    }
    if (event.multiSortMeta) {
      params['sort'] = event.multiSortMeta.map(s => s.field + (s.order === -1 ? ',desc' : ',asc'));
    }
    params['page'] = (event.first ?? 0) / (event.rows ?? ITEMS_PER_PAGE);
    params['size'] = event.rows ?? ITEMS_PER_PAGE;
  } else {
    params['size'] = ITEMS_PER_PAGE;
  }
  return params;
};

export const fillTableFromQueryParams = (table: Table, queryParams: Params, filtersDetails: FiltersDetails): void => {
  const params: any = unflatten(queryParams);
  table.first = +queryParams.first || 0;
  table.multiSortMeta = (params['msm'] || []).map((sm: any) => ({ field: sm.field, order: +sm.order }));
  const filters: { [_: string]: FilterMetadata | FilterMetadata[] | undefined } = {};
  if (params['f']) {
    Object.entries((flatten as any)(params['f'], { safe: true })).forEach(([field, value]) => {
      let filterName = field;
      let filterValue = value;
      if (filterName === 'globalFilter') {
        filters[filterName] = {
          value: filterName,
        };
      } else {
        const matchMode = Array.from(matchModes.keys()).find(mm => field.endsWith(`.${mm}`));
        if (matchMode) {
          filterName = field.slice(0, -matchMode.length - 1); // -1 for the dot (.)
        }
        if (matchMode === 'specified') {
          filterValue = deserializeFilter(filterValue as string, 'boolean');
        } else if (matchMode === FilterMatchMode.IN) {
          filterValue = (value as string).split(',');
          filterValue = (filterValue as string[]).map(fv => deserializeFilter(fv, filtersDetails[filterName]?.type));
        } else {
          filterValue = deserializeFilter(filterValue as string, filtersDetails[filterName]?.type);
        }
        if (!filters[filterName]) {
          filters[filterName] = [];
        }
        (filters[filterName] as FilterMetadata[]).push({
          value: filterValue,
          matchMode,
        });
      }
    });
  }
  // merge filters making sure not recreate any to avoid auto close on small change (example dateTime)
  Object.keys(table.filters).forEach(k => {
    if (!Array.isArray(table.filters[k])) {
      if (!filters[k]) {
        table.filters[k] = { ...(table.filters[k] as FilterMetadata), value: null };
      } else {
        table.filters[k] = { ...(table.filters[k] as FilterMetadata), ...filters[k] };
      }
    } else {
      const tableFilter = table.filters[k] as FilterMetadata[];
      if (!filters[k]) {
        tableFilter.splice(1);
        tableFilter[0].value = null;
      } else {
        tableFilter.splice((filters[k] as FilterMetadata[]).length);
        for (let i = 0; i < (filters[k] as FilterMetadata[]).length; i++) {
          if (!tableFilter[i]) {
            tableFilter[i] = (filters[k] as FilterMetadata[])[i];
          } else {
            Object.assign(tableFilter[i], (filters[k] as FilterMetadata[])[i]);
          }
        }
      }
    }
  });
  // starting from primeng 16, filters are initialized only after page is loaded, therefore if a filter is not find in the table we need to add it manually
  // the thing is, we don't know if the filter display is row or menu, for now we suppose it is menu, if this behavior changes, we can use filters details to specify which ones are row
  // we check that filter is present in filter details to avoid adding in the filters something that is not supposed to be there
  Object.keys(filters)
    .filter(k => (!table.filters[k] as any) && (filtersDetails[k] || k === 'globalFilter'))
    .forEach(k => {
      table.filters[k] = filters[k]!;
    });
};

export const lazyLoadEventToRouterQueryParams = (event: TableLazyLoadEvent, filtersDetails: FiltersDetails): Params => {
  const queryParams: { [_: string]: any } = {};
  if (event.first) {
    queryParams['first'] = event.first;
  }
  if (event.multiSortMeta?.length) {
    queryParams['msm'] = event.multiSortMeta;
  }
  if (event.filters) {
    Object.entries(event.filters).forEach(([field, filterMetas]) => {
      if (filterMetas) {
        if (!Array.isArray(filterMetas)) {
          filterMetas = [filterMetas];
        }
        filterMetas.forEach(filter => {
          let filterValue = filter.value;
          if (filterValue !== undefined && filterValue !== null) {
            const matchMode = filter.matchMode;
            if (matchMode === 'specified') {
              filterValue = serializeFilter(filterValue, 'boolean');
            } else if (matchMode === 'in') {
              if (filterValue.length === 0) {
                return;
              }
              filterValue = filterValue.map((v: any) => serializeFilter(v, filtersDetails[field]?.type));
              filterValue = filterValue.join(',');
            } else {
              filterValue = serializeFilter(filterValue, filtersDetails[field]?.type);
            }
            const paramKey = matchMode ? `f.${field}.${matchMode}` : `f.${field}`;
            queryParams[paramKey] = filterValue;
          }
        });
      }
    });
  }
  return flatten(queryParams);
};

const serializeFilter = (value: any, type?: string): string => {
  if (type === 'number') {
    return `${value as number}`;
  }
  if (type === 'date') {
    return datePipe.transform(value, DATE_FORMAT) as string;
  }
  if (type === 'dateTime') {
    return value.toISOString() as string;
  }
  if (type === 'boolean') {
    return value ? 'true' : 'false';
  }
  return value as string;
};

const deserializeFilter = (value: string, type?: string): number | Date | boolean | string => {
  if (type === 'number') {
    return +value;
  }
  if (type === 'date') {
    return new Date(value);
  }
  if (type === 'dateTime') {
    return new Date(value);
  }
  if (type === 'boolean') {
    return value === 'true';
  }
  return value;
};

/**
 * maps default primeng match modes to serverSide ones
 */
const matchModes = new Map([
  [FilterMatchMode.STARTS_WITH, 'contains'],
  [FilterMatchMode.CONTAINS, 'contains'],
  [FilterMatchMode.NOT_CONTAINS, 'notContains'],
  [FilterMatchMode.ENDS_WITH, 'contains'],
  [FilterMatchMode.EQUALS, 'equals'],
  [FilterMatchMode.NOT_EQUALS, 'notEquals'],
  [FilterMatchMode.IN, 'in'],
  [FilterMatchMode.LESS_THAN, 'lessThan'],
  [FilterMatchMode.LESS_THAN_OR_EQUAL_TO, 'lessThanOrEqual'],
  [FilterMatchMode.GREATER_THAN, 'greaterThan'],
  [FilterMatchMode.GREATER_THAN_OR_EQUAL_TO, 'greaterThanOrEqual'],
  [FilterMatchMode.IS, 'equals'],
  [FilterMatchMode.IS_NOT, 'notEquals'],
  [FilterMatchMode.BEFORE, 'lessThan'],
  [FilterMatchMode.AFTER, 'greaterThan'],
  [FilterMatchMode.DATE_IS, 'equals'],
  [FilterMatchMode.DATE_IS_NOT, 'notEquals'],
  [FilterMatchMode.DATE_BEFORE, 'lessThan'],
  [FilterMatchMode.DATE_AFTER, 'greaterThan'],
]);
