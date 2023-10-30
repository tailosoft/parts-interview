import { inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { of, EMPTY, Observable } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IPartSupplier } from '../part-supplier.model';
import { PartSupplierService } from '../service/part-supplier.service';

export const partSupplierResolve = (route: ActivatedRouteSnapshot): Observable<null | IPartSupplier> => {
  const partId = route.params['partId'] ? route.params['partId'] : null;
  const supplierId = route.params['supplierId'] ? route.params['supplierId'] : null;
  if (partId && supplierId) {
    return inject(PartSupplierService)
      .find(partId, supplierId)
      .pipe(
        mergeMap((partSupplier: HttpResponse<IPartSupplier>) => {
          if (partSupplier.body) {
            return of(partSupplier.body);
          } else {
            inject(Router).navigate(['404']);
            return EMPTY;
          }
        })
      );
  }
  return of(null);
};

export default partSupplierResolve;
