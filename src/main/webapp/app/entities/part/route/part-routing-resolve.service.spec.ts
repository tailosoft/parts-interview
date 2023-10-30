import { TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ActivatedRouteSnapshot, ActivatedRoute, Router, convertToParamMap } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { DatePipe } from '@angular/common';
import { of } from 'rxjs';

import { IPart } from '../part.model';
import { PartService } from '../service/part.service';

import partResolve from './part-routing-resolve.service';

describe('Service Tests', () => {
  describe('Part routing resolve service', () => {
    let mockRouter: Router;
    let mockActivatedRouteSnapshot: ActivatedRouteSnapshot;
    let service: PartService;
    let resultPart: IPart | null;
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

      service = TestBed.inject(PartService);
      resultPart = null;
    });

    describe('resolve', () => {
      it('should return IPart returned by find', () => {
        // GIVEN
        service.find = jest.fn(() => of(new HttpResponse({ body: { id: 123 } }) as any));
        mockActivatedRouteSnapshot.params = { id: 123 };

        // WHEN
        TestBed.runInInjectionContext(() => {
          partResolve(mockActivatedRouteSnapshot).subscribe({
            next(result) {
              resultPart = result;
            },
          });
        });

        // THEN
        expect(service.find).toBeCalledWith(123);
        expect(resultPart).toEqual({ id: 123 });
      });

      it('should return null if id is not provided', () => {
        // GIVEN
        service.find = jest.fn();
        mockActivatedRouteSnapshot.params = {};

        // WHEN
        TestBed.runInInjectionContext(() => {
          partResolve(mockActivatedRouteSnapshot).subscribe({
            next(result) {
              resultPart = result;
            },
          });
        });

        // THEN
        expect(service.find).not.toBeCalled();
        expect(resultPart).toEqual(null);
      });

      it('should route to 404 page if data not found in server', () => {
        // GIVEN
        jest.spyOn(service, 'find').mockReturnValue(of(new HttpResponse({ body: null }) as any));
        mockActivatedRouteSnapshot.params = { id: 123 };

        // WHEN
        TestBed.runInInjectionContext(() => {
          partResolve(mockActivatedRouteSnapshot).subscribe({
            next(result) {
              resultPart = result;
            },
          });
        });

        // THEN
        expect(service.find).toBeCalledWith(123);
        expect(resultPart).toEqual(null);
        expect(mockRouter.navigate).toHaveBeenCalledWith(['404']);
      });
    });
  });
});
