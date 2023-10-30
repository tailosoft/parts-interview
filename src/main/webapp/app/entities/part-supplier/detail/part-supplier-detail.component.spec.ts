import { TestBed } from '@angular/core/testing';
import { provideRouter, withComponentInputBinding } from '@angular/router';
import { RouterTestingHarness, RouterTestingModule } from '@angular/router/testing';
import { of } from 'rxjs';

import { PartSupplierDetailComponent } from './part-supplier-detail.component';

describe('PartSupplier Management Detail Component', () => {
  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PartSupplierDetailComponent, RouterTestingModule.withRoutes([], { bindToComponentInputs: true })],
      providers: [
        provideRouter(
          [
            {
              path: '**',
              component: PartSupplierDetailComponent,
              resolve: { partSupplier: () => of({ partId: 123, supplierId: 123 }) },
            },
          ],
          withComponentInputBinding()
        ),
      ],
    })
      .overrideTemplate(PartSupplierDetailComponent, '')
      .compileComponents();
  });

  describe('OnInit', () => {
    it('Should load partSupplier on init', async () => {
      const harness = await RouterTestingHarness.create();
      const instance = await harness.navigateByUrl('/', PartSupplierDetailComponent);

      // THEN
      expect(instance.partSupplier).toEqual(expect.objectContaining({ partId: 123, supplierId: 123 }));
    });
  });
});
