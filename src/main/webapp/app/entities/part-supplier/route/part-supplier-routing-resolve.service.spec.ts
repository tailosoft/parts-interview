import { TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ActivatedRouteSnapshot, ActivatedRoute, Router, convertToParamMap } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { DatePipe } from '@angular/common';
import { of } from 'rxjs';

import { IPartSupplier } from '../part-supplier.model';
import { PartSupplierService } from '../service/part-supplier.service';

import partSupplierResolve from './part-supplier-routing-resolve.service';

describe('Service Tests', () => {
  describe('PartSupplier routing resolve service', () => {
    let mockRouter: Router;
    let mockActivatedRouteSnapshot: ActivatedRouteSnapshot;
    let service: PartSupplierService;
    let resultPartSupplier: IPartSupplier | null;
    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
        providers: [
          {
            provide: ActivatedRoute,
            useValue: {
              snapshot: {
                paramMap: convertToParamMap({}),
              },
            },
          },
          DatePipe,
        ],
      });
      mockRouter = TestBed.inject(Router);
      jest.spyOn(mockRouter, 'navigate').mockImplementation(() => Promise.resolve(true));
      mockActivatedRouteSnapshot = TestBed.inject(ActivatedRoute).snapshot;

      service = TestBed.inject(PartSupplierService);
      resultPartSupplier = null;
    });

    describe('resolve', () => {
      it('should return IPartSupplier returned by find', () => {
        // GIVEN
        service.find = jest.fn(() => of(new HttpResponse({ body: { part: { id: 123 }, supplier: { id: 123 } } }) as any));
        mockActivatedRouteSnapshot.params = { partId: 123, supplierId: 123 };

        // WHEN
        TestBed.runInInjectionContext(() => {
          partSupplierResolve(mockActivatedRouteSnapshot).subscribe({
            next(result) {
              resultPartSupplier = result;
            },
          });
        });

        // THEN
        expect(service.find).toBeCalledWith(123, 123);
        expect(resultPartSupplier).toEqual({ part: { id: 123 }, supplier: { id: 123 } });
      });

      it('should return null if id is not provided', () => {
        // GIVEN
        service.find = jest.fn();
        mockActivatedRouteSnapshot.params = {};

        // WHEN
        TestBed.runInInjectionContext(() => {
          partSupplierResolve(mockActivatedRouteSnapshot).subscribe({
            next(result) {
              resultPartSupplier = result;
            },
          });
        });

        // THEN
        expect(service.find).not.toBeCalled();
        expect(resultPartSupplier).toEqual(null);
      });

      it('should route to 404 page if data not found in server', () => {
        // GIVEN
        jest.spyOn(service, 'find').mockReturnValue(of(new HttpResponse({ body: null }) as any));
        mockActivatedRouteSnapshot.params = { partId: 123, supplierId: 123 };

        // WHEN
        TestBed.runInInjectionContext(() => {
          partSupplierResolve(mockActivatedRouteSnapshot).subscribe({
            next(result) {
              resultPartSupplier = result;
            },
          });
        });

        // THEN
        expect(service.find).toBeCalledWith(123, 123);
        expect(resultPartSupplier).toEqual(null);
        expect(mockRouter.navigate).toHaveBeenCalledWith(['404']);
      });
    });
  });
});
