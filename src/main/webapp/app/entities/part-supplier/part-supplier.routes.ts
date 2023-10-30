import { Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { PartSupplierComponent } from './list/part-supplier.component';
import { PartSupplierDetailComponent } from './detail/part-supplier-detail.component';
import { PartSupplierUpdateComponent } from './update/part-supplier-update.component';
import PartSupplierResolve from './route/part-supplier-routing-resolve.service';

const partSupplierRoute: Routes = [
  {
    path: '',
    component: PartSupplierComponent,
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'view',
    component: PartSupplierDetailComponent,
    resolve: {
      partSupplier: PartSupplierResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: PartSupplierUpdateComponent,
    resolve: {
      partSupplier: PartSupplierResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'edit',
    component: PartSupplierUpdateComponent,
    resolve: {
      partSupplier: PartSupplierResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default partSupplierRoute;
