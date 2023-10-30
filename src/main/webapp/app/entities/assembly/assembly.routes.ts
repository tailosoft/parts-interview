import { Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { AssemblyComponent } from './list/assembly.component';
import { AssemblyDetailComponent } from './detail/assembly-detail.component';
import { AssemblyUpdateComponent } from './update/assembly-update.component';
import AssemblyResolve from './route/assembly-routing-resolve.service';

const assemblyRoute: Routes = [
  {
    path: '',
    component: AssemblyComponent,
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'view',
    component: AssemblyDetailComponent,
    resolve: {
      assembly: AssemblyResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: AssemblyUpdateComponent,
    resolve: {
      assembly: AssemblyResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'edit',
    component: AssemblyUpdateComponent,
    resolve: {
      assembly: AssemblyResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default assemblyRoute;
