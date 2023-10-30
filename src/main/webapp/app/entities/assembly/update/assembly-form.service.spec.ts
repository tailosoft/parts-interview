import { TestBed } from '@angular/core/testing';

import { sampleWithRequiredData } from '../assembly.test-samples';

import { AssemblyFormService } from './assembly-form.service';

describe('Assembly Form Service', () => {
  let service: AssemblyFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(AssemblyFormService);
  });

  describe('Service methods', () => {
    describe('createAssemblyFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createAssemblyFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            quantity: expect.any(Object),
            parent: expect.any(Object),
            child: expect.any(Object),
          })
        );
      });

      it('passing IAssembly should create a new form with FormGroup', () => {
        const formGroup = service.createAssemblyFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            quantity: expect.any(Object),
            parent: expect.any(Object),
            child: expect.any(Object),
          })
        );
      });
    });
  });
});
