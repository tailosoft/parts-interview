jest.mock('app/core/auth/account.service');

import { ComponentFixture, TestBed, fakeAsync, tick } from '@angular/core/testing';
import { of, BehaviorSubject } from 'rxjs';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute, Router } from '@angular/router';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { Table } from 'primeng/table';
import { ConfirmationService, Confirmation, MessageService } from 'primeng/api';
import { TranslateModule } from '@ngx-translate/core';

import { UserManagementService } from '../service/user-management.service';
import { User } from '../user-management.model';
import { AccountService } from 'app/core/auth/account.service';

import UserManagementComponent from './user-management.component';
import { RouterTestingModule } from '@angular/router/testing';

describe('User Management Component', () => {
  let comp: UserManagementComponent;
  let fixture: ComponentFixture<UserManagementComponent>;
  let service: UserManagementService;
  let mockAccountService: AccountService;
  let confirmationService: ConfirmationService;
  let activatedRoute: ActivatedRoute;
  let router: Router;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, TranslateModule.forRoot(), RouterTestingModule.withRoutes([]), UserManagementComponent],
      providers: [
        ConfirmationService,
        {
          provide: ActivatedRoute,
          useValue: { data: of(), queryParams: new BehaviorSubject({}) },
        },
        AccountService,
        MessageService,
      ],
    })
      .overrideTemplate(UserManagementComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(UserManagementComponent);
    comp = fixture.componentInstance;
    service = TestBed.inject(UserManagementService);
    confirmationService = TestBed.inject(ConfirmationService);
    activatedRoute = TestBed.inject(ActivatedRoute);
    mockAccountService = TestBed.inject(AccountService);
    mockAccountService.identity = jest.fn(() => of(null));
    router = TestBed.inject(Router);
    jest.spyOn(router, 'navigate').mockImplementation();

    comp.userTable = { filters: {}, createLazyLoadMetadata: () => undefined } as Table;
  });

  it('Should call load all on init', fakeAsync(() => {
    // GIVEN
    jest.spyOn(service, 'query').mockReturnValue(
      of(
        new HttpResponse({
          body: [new User(123)],
        })
      )
    );

    // WHEN
    fixture.detectChanges();

    // THEN
    expect(service.query).toHaveBeenCalled();
    expect(comp.users![0]).toEqual(expect.objectContaining({ id: 123 }));
  }));

  it('should load a page', fakeAsync(() => {
    // GIVEN
    jest.spyOn(service, 'query').mockReturnValue(
      of(
        new HttpResponse({
          body: [new User(123)],
        })
      )
    );

    // WHEN
    fixture.detectChanges();
    tick(100);
    (activatedRoute.queryParams as BehaviorSubject<any>).next({ first: 3 });

    // THEN
    expect(service.query).toHaveBeenCalled();
    expect(comp.users![0]).toEqual(expect.objectContaining({ id: 123 }));
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
    comp.delete('AAAAAAA');

    // THEN
    expect(confirmationService.confirm).toHaveBeenCalled();
    expect(service.delete).toHaveBeenCalledWith('AAAAAAA');
  }));
});
