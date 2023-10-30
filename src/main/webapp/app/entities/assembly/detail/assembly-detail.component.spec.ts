import { TestBed } from '@angular/core/testing';
import { provideRouter, withComponentInputBinding } from '@angular/router';
import { RouterTestingHarness, RouterTestingModule } from '@angular/router/testing';
import { of } from 'rxjs';

import { AssemblyDetailComponent } from './assembly-detail.component';

describe('Assembly Management Detail Component', () => {
  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AssemblyDetailComponent, RouterTestingModule.withRoutes([], { bindToComponentInputs: true })],
      providers: [
        provideRouter(
          [
            {
              path: '**',
              component: AssemblyDetailComponent,
              resolve: { assembly: () => of({ parentId: 123, childId: 123 }) },
            },
          ],
          withComponentInputBinding()
        ),
      ],
    })
      .overrideTemplate(AssemblyDetailComponent, '')
      .compileComponents();
  });

  describe('OnInit', () => {
    it('Should load assembly on init', async () => {
      const harness = await RouterTestingHarness.create();
      const instance = await harness.navigateByUrl('/', AssemblyDetailComponent);

      // THEN
      expect(instance.assembly).toEqual(expect.objectContaining({ parentId: 123, childId: 123 }));
    });
  });
});
