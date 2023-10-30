import { inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { of, EMPTY, Observable } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IPart } from '../part.model';
import { PartService } from '../service/part.service';

export const partResolve = (route: ActivatedRouteSnapshot): Observable<null | IPart> => {
  const id = route.params['id'] ? route.params['id'] : null;
  if (id) {
    return inject(PartService)
      .find(id)
      .pipe(
        mergeMap((part: HttpResponse<IPart>) => {
          if (part.body) {
            return of(part.body);
          } else {
            inject(Router).navigate(['404']);
            return EMPTY;
          }
        })
      );
  }
  return of(null);
};

export default partResolve;
