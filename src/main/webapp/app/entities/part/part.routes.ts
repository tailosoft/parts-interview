import { Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { PartComponent } from './list/part.component';
import { PartDetailComponent } from './detail/part-detail.component';
import { PartUpdateComponent } from './update/part-update.component';
import PartResolve from './route/part-routing-resolve.service';

const partRoute: Routes = [
  {
    path: '',
    component: PartComponent,
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: PartDetailComponent,
    resolve: {
      part: PartResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: PartUpdateComponent,
    resolve: {
      part: PartResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: PartUpdateComponent,
    resolve: {
      part: PartResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default partRoute;
