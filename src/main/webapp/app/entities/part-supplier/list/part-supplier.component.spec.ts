import { ComponentFixture, TestBed, fakeAsync, tick } from '@angular/core/testing';
import { of, BehaviorSubject } from 'rxjs';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { Confirmation, ConfirmationService, MessageService } from 'primeng/api';
import { ActivatedRoute, Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { TranslateModule } from '@ngx-translate/core';
import { Table } from 'primeng/table';

import { PartSupplierComponent } from './part-supplier.component';
import { PartSupplierService } from '../service/part-supplier.service';

describe('Component Tests', () => {
  describe('PartSupplier Management Component', () => {
    let comp: PartSupplierComponent;
    let fixture: ComponentFixture<PartSupplierComponent>;
    let service: PartSupplierService;
    let confirmationService: ConfirmationService;
    let activatedRoute: ActivatedRoute;
    let router: Router;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule, TranslateModule.forRoot(), RouterTestingModule.withRoutes([]), PartSupplierComponent],
        providers: [
          ConfirmationService,
          MessageService,
          {
            provide: ActivatedRoute,
            useValue: { data: of(), queryParams: new BehaviorSubject({}) },
          },
        ],
      })
        .overrideTemplate(PartSupplierComponent, '')
        .compileComponents();

      fixture = TestBed.createComponent(PartSupplierComponent);
      comp = fixture.componentInstance;
      service = TestBed.inject(PartSupplierService);
      confirmationService = TestBed.inject(ConfirmationService);
      activatedRoute = TestBed.inject(ActivatedRoute);
      router = TestBed.get(Router);
      jest.spyOn(router, 'navigate').mockImplementation();

      comp.partSupplierTable = { filters: {}, createLazyLoadMetadata: () => undefined } as Table;
    });

    it('Should call load all on init', fakeAsync(() => {
      // GIVEN
      jest.spyOn(service, 'query').mockReturnValue(
        of(
          new HttpResponse({
            body: [{ part: { id: 123 }, supplier: { id: 123 } }],
          }) as any
        )
      );

      // WHEN
      fixture.detectChanges();

      // THEN
      expect(service.query).toHaveBeenCalled();
      expect(comp.partSuppliers?.[0]).toEqual(expect.objectContaining({ part: { id: 123 }, supplier: { id: 123 } }));
    }));

    it('should load a page', fakeAsync(() => {
      // GIVEN
      jest.spyOn(service, 'query').mockReturnValue(
        of(
          new HttpResponse({
            body: [{ part: { id: 123 }, supplier: { id: 123 } }],
          })
        )
      );

      // WHEN
      fixture.detectChanges();
      tick(100);
      (activatedRoute.queryParams as BehaviorSubject<any>).next({ first: 3 });

      // THEN
      expect(service.query).toHaveBeenCalled();
      expect(comp.partSuppliers?.[0]).toEqual(expect.objectContaining({ part: { id: 123 }, supplier: { id: 123 } }));
    }));

    it('should call delete service using confirmDialog', fakeAsync(() => {
      // GIVEN
      jest.spyOn(service, 'delete').mockReturnValue(of({} as any));
      jest.spyOn(confirmationService, 'confirm').mockImplementation((confirmation: Confirmation) => {
        if (confirmation.accept) {
          confirmation.accept();
        }
        return confirmationService;
      });

      // WHEN
      comp.delete(123, 123);

      // THEN
      expect(confirmationService.confirm).toHaveBeenCalled();
      expect(service.delete).toHaveBeenCalledWith(123, 123);
    }));
  });
});
