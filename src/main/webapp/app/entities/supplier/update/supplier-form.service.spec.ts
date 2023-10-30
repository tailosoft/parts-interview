import { TestBed } from '@angular/core/testing';

import { sampleWithRequiredData } from '../supplier.test-samples';

import { SupplierFormService } from './supplier-form.service';

describe('Supplier Form Service', () => {
  let service: SupplierFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(SupplierFormService);
  });

  describe('Service methods', () => {
    describe('createSupplierFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createSupplierFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            name: expect.any(Object),
          })
        );
      });

      it('passing ISupplier should create a new form with FormGroup', () => {
        const formGroup = service.createSupplierFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            name: expect.any(Object),
          })
        );
      });
    });
  });
});
