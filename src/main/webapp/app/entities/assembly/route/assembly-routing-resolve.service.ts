import { inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { of, EMPTY, Observable } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IAssembly } from '../assembly.model';
import { AssemblyService } from '../service/assembly.service';

export const assemblyResolve = (route: ActivatedRouteSnapshot): Observable<null | IAssembly> => {
  const parentId = route.params['parentId'] ? route.params['parentId'] : null;
  const childId = route.params['childId'] ? route.params['childId'] : null;
  if (parentId && childId) {
    return inject(AssemblyService)
      .find(parentId, childId)
      .pipe(
        mergeMap((assembly: HttpResponse<IAssembly>) => {
          if (assembly.body) {
            return of(assembly.body);
          } else {
            inject(Router).navigate(['404']);
            return EMPTY;
          }
        })
      );
  }
  return of(null);
};

export default assemblyResolve;
