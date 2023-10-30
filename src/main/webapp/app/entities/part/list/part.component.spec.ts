import { ComponentFixture, TestBed, fakeAsync, tick } from '@angular/core/testing';
import { of, BehaviorSubject } from 'rxjs';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { Confirmation, ConfirmationService, MessageService } from 'primeng/api';
import { ActivatedRoute, Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { TranslateModule } from '@ngx-translate/core';
import { Table } from 'primeng/table';

import { PartComponent } from './part.component';
import { PartService } from '../service/part.service';

describe('Component Tests', () => {
  describe('Part Management Component', () => {
    let comp: PartComponent;
    let fixture: ComponentFixture<PartComponent>;
    let service: PartService;
    let confirmationService: ConfirmationService;
    let activatedRoute: ActivatedRoute;
    let router: Router;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule, TranslateModule.forRoot(), RouterTestingModule.withRoutes([]), PartComponent],
        providers: [
          ConfirmationService,
          MessageService,
          {
            provide: ActivatedRoute,
            useValue: { data: of(), queryParams: new BehaviorSubject({}) },
          },
        ],
      })
        .overrideTemplate(PartComponent, '')
        .compileComponents();

      fixture = TestBed.createComponent(PartComponent);
      comp = fixture.componentInstance;
      service = TestBed.inject(PartService);
      confirmationService = TestBed.inject(ConfirmationService);
      activatedRoute = TestBed.inject(ActivatedRoute);
      router = TestBed.get(Router);
      jest.spyOn(router, 'navigate').mockImplementation();

      comp.partTable = { filters: {}, createLazyLoadMetadata: () => undefined } as Table;
    });

    it('Should call load all on init', fakeAsync(() => {
      // GIVEN
      jest.spyOn(service, 'query').mockReturnValue(
        of(
          new HttpResponse({
            body: [{ id: 123 }],
          }) as any
        )
      );

      // WHEN
      fixture.detectChanges();

      // THEN
      expect(service.query).toHaveBeenCalled();
      expect(comp.parts?.[0]).toEqual(expect.objectContaining({ id: 123 }));
    }));

    it('should load a page', fakeAsync(() => {
      // GIVEN
      jest.spyOn(service, 'query').mockReturnValue(
        of(
          new HttpResponse({
            body: [{ id: 123 }],
          })
        )
      );

      // WHEN
      fixture.detectChanges();
      tick(100);
      (activatedRoute.queryParams as BehaviorSubject<any>).next({ first: 3 });

      // THEN
      expect(service.query).toHaveBeenCalled();
      expect(comp.parts?.[0]).toEqual(expect.objectContaining({ id: 123 }));
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
      comp.delete(123);

      // THEN
      expect(confirmationService.confirm).toHaveBeenCalled();
      expect(service.delete).toHaveBeenCalledWith(123);
    }));
  });
});
