import { TestBed } from '@angular/core/testing';

import { sampleWithRequiredData } from '../part.test-samples';

import { PartFormService } from './part-form.service';

describe('Part Form Service', () => {
  let service: PartFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(PartFormService);
  });

  describe('Service methods', () => {
    describe('createPartFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createPartFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            name: expect.any(Object),
            assemblyCost: expect.any(Object),
            bestPrice: expect.any(Object),
            bestSupplier: expect.any(Object),
          })
        );
      });

      it('passing IPart should create a new form with FormGroup', () => {
        const formGroup = service.createPartFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            name: expect.any(Object),
            assemblyCost: expect.any(Object),
            bestPrice: expect.any(Object),
            bestSupplier: expect.any(Object),
          })
        );
      });
    });
  });
});
