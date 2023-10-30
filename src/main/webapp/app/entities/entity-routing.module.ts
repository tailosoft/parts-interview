import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

@NgModule({
  imports: [
    RouterModule.forChild([
      {
        path: 'part',
        data: { pageTitle: 'partsApp.part.home.title' },
        loadChildren: () => import('./part/part.routes'),
      },
      {
        path: 'assembly',
        data: { pageTitle: 'partsApp.assembly.home.title' },
        loadChildren: () => import('./assembly/assembly.routes'),
      },
      {
        path: 'supplier',
        data: { pageTitle: 'partsApp.supplier.home.title' },
        loadChildren: () => import('./supplier/supplier.routes'),
      },
      {
        path: 'part-supplier',
        data: { pageTitle: 'partsApp.partSupplier.home.title' },
        loadChildren: () => import('./part-supplier/part-supplier.routes'),
      },
      /* jhipster-needle-add-entity-route - JHipster will add entity modules routes here */
    ]),
  ],
})
export class EntityRoutingModule {}
