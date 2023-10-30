import { TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ActivatedRouteSnapshot, ActivatedRoute, Router, convertToParamMap } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { DatePipe } from '@angular/common';
import { of } from 'rxjs';

import { IAssembly } from '../assembly.model';
import { AssemblyService } from '../service/assembly.service';

import assemblyResolve from './assembly-routing-resolve.service';

describe('Service Tests', () => {
  describe('Assembly routing resolve service', () => {
    let mockRouter: Router;
    let mockActivatedRouteSnapshot: ActivatedRouteSnapshot;
    let service: AssemblyService;
    let resultAssembly: IAssembly | null;
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

      service = TestBed.inject(AssemblyService);
      resultAssembly = null;
    });

    describe('resolve', () => {
      it('should return IAssembly returned by find', () => {
        // GIVEN
        service.find = jest.fn(() => of(new HttpResponse({ body: { parent: { id: 123 }, child: { id: 123 } } }) as any));
        mockActivatedRouteSnapshot.params = { parentId: 123, childId: 123 };

        // WHEN
        TestBed.runInInjectionContext(() => {
          assemblyResolve(mockActivatedRouteSnapshot).subscribe({
            next(result) {
              resultAssembly = result;
            },
          });
        });

        // THEN
        expect(service.find).toBeCalledWith(123, 123);
        expect(resultAssembly).toEqual({ parent: { id: 123 }, child: { id: 123 } });
      });

      it('should return null if id is not provided', () => {
        // GIVEN
        service.find = jest.fn();
        mockActivatedRouteSnapshot.params = {};

        // WHEN
        TestBed.runInInjectionContext(() => {
          assemblyResolve(mockActivatedRouteSnapshot).subscribe({
            next(result) {
              resultAssembly = result;
            },
          });
        });

        // THEN
        expect(service.find).not.toBeCalled();
        expect(resultAssembly).toEqual(null);
      });

      it('should route to 404 page if data not found in server', () => {
        // GIVEN
        jest.spyOn(service, 'find').mockReturnValue(of(new HttpResponse({ body: null }) as any));
        mockActivatedRouteSnapshot.params = { parentId: 123, childId: 123 };

        // WHEN
        TestBed.runInInjectionContext(() => {
          assemblyResolve(mockActivatedRouteSnapshot).subscribe({
            next(result) {
              resultAssembly = result;
            },
          });
        });

        // THEN
        expect(service.find).toBeCalledWith(123, 123);
        expect(resultAssembly).toEqual(null);
        expect(mockRouter.navigate).toHaveBeenCalledWith(['404']);
      });
    });
  });
});
