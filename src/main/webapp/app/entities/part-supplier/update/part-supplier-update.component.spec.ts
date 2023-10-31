import { sampleWithRequiredData } from '../part-supplier.test-samples';
import { ComponentFixture, TestBed, fakeAsync, tick } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { RouterTestingModule } from '@angular/router/testing';
import { MessageService } from 'primeng/api';
import { of } from 'rxjs';
import { DatePipe } from '@angular/common';

import { PartSupplierUpdateComponent } from './part-supplier-update.component';
import { PartSupplierService } from '../service/part-supplier.service';

describe('Component Tests', () => {
  describe('PartSupplier Management Update Component', () => {
    let comp: PartSupplierUpdateComponent;
    let fixture: ComponentFixture<PartSupplierUpdateComponent>;
    let service: PartSupplierService;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([]), PartSupplierUpdateComponent],
        providers: [FormBuilder, MessageService, DatePipe],
      })
        .overrideTemplate(PartSupplierUpdateComponent, '')
        .compileComponents();

      fixture = TestBed.createComponent(PartSupplierUpdateComponent);
      comp = fixture.componentInstance;
      Object.defineProperty(comp.editForm, 'valid', {
        get: () => true,
      });
      service = TestBed.inject(PartSupplierService);
    });

    describe('save', () => {
      it('Should call update service on save for existing entity', fakeAsync(() => {
        // GIVEN
        fixture.detectChanges();
        const entity = sampleWithRequiredData;
        jest.spyOn(service, 'update').mockReturnValue(of(new HttpResponse({ body: entity }) as any));
        comp.updateForm(entity);
        // WHEN
        comp.save();
        tick(); // simulate async

        // THEN
        expect(service.update).toHaveBeenCalledWith(expect.objectContaining(entity));
        expect(comp.isSaving).toEqual(false);
      }));

      it('Should call create service on save for new entity', fakeAsync(() => {
        // GIVEN
        jest.spyOn(service, 'create').mockReturnValue(of(new HttpResponse({ body: sampleWithRequiredData })));
        comp.updateForm(null);
        // WHEN
        comp.save();
        tick(); // simulate async

        // THEN
        expect(service.create).toHaveBeenCalledWith(comp.editForm.value);
        expect(comp.isSaving).toEqual(false);
      }));
    });
  });
});
