import { TestBed } from '@angular/core/testing';
import { provideRouter, withComponentInputBinding } from '@angular/router';
import { RouterTestingHarness, RouterTestingModule } from '@angular/router/testing';
import { of } from 'rxjs';

import { SupplierDetailComponent } from './supplier-detail.component';

describe('Supplier Management Detail Component', () => {
  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SupplierDetailComponent, RouterTestingModule.withRoutes([], { bindToComponentInputs: true })],
      providers: [
        provideRouter(
          [
            {
              path: '**',
              component: SupplierDetailComponent,
              resolve: { supplier: () => of({ id: 123 }) },
            },
          ],
          withComponentInputBinding()
        ),
      ],
    })
      .overrideTemplate(SupplierDetailComponent, '')
      .compileComponents();
  });

  describe('OnInit', () => {
    it('Should load supplier on init', async () => {
      const harness = await RouterTestingHarness.create();
      const instance = await harness.navigateByUrl('/', SupplierDetailComponent);

      // THEN
      expect(instance.supplier).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
