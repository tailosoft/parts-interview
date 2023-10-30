import { TestBed } from '@angular/core/testing';

import { sampleWithRequiredData } from '../part-supplier.test-samples';

import { PartSupplierFormService } from './part-supplier-form.service';

describe('PartSupplier Form Service', () => {
  let service: PartSupplierFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(PartSupplierFormService);
  });

  describe('Service methods', () => {
    describe('createPartSupplierFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createPartSupplierFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            price: expect.any(Object),
            part: expect.any(Object),
            supplier: expect.any(Object),
          })
        );
      });

      it('passing IPartSupplier should create a new form with FormGroup', () => {
        const formGroup = service.createPartSupplierFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            price: expect.any(Object),
            part: expect.any(Object),
            supplier: expect.any(Object),
          })
        );
      });
    });
  });
});
