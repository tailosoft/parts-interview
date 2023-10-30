import { TestBed } from '@angular/core/testing';
import { provideRouter, withComponentInputBinding } from '@angular/router';
import { RouterTestingHarness, RouterTestingModule } from '@angular/router/testing';
import { of } from 'rxjs';

import { PartDetailComponent } from './part-detail.component';

describe('Part Management Detail Component', () => {
  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PartDetailComponent, RouterTestingModule.withRoutes([], { bindToComponentInputs: true })],
      providers: [
        provideRouter(
          [
            {
              path: '**',
              component: PartDetailComponent,
              resolve: { part: () => of({ id: 123 }) },
            },
          ],
          withComponentInputBinding()
        ),
      ],
    })
      .overrideTemplate(PartDetailComponent, '')
      .compileComponents();
  });

  describe('OnInit', () => {
    it('Should load part on init', async () => {
      const harness = await RouterTestingHarness.create();
      const instance = await harness.navigateByUrl('/', PartDetailComponent);

      // THEN
      expect(instance.part).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
