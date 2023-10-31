import { Component, OnInit, ViewChild } from '@angular/core';
import { RouterModule } from '@angular/router';
import { HttpResponse, HttpHeaders, HttpErrorResponse } from '@angular/common/http';
import { Subscription } from 'rxjs';
import { switchMap, tap, filter } from 'rxjs/operators';
import { ActivatedRoute, Router } from '@angular/router';
import SharedModule from 'app/shared/shared.module';
import { ConfirmationService, MessageService } from 'primeng/api';
import { TableLazyLoadEvent } from 'primeng/table';
import { ITEMS_PER_PAGE } from 'app/config/pagination.constants';
import {
  lazyLoadEventToServerQueryParams,
  lazyLoadEventToRouterQueryParams,
  fillTableFromQueryParams,
} from 'app/core/request/request-util';
import { AccountService } from 'app/core/auth/account.service';
import { Account } from 'app/core/auth/account.model';
import { UserManagementService } from '../service/user-management.service';
import { IUser } from '../user-management.model';
import { Table } from 'primeng/table';
import { TranslateService } from '@ngx-translate/core';

@Component({
  standalone: true,
  selector: 'jhi-user-mgmt',
  templateUrl: './user-management.component.html',
  imports: [RouterModule, SharedModule],
})
export default class UserManagementComponent implements OnInit {
  currentAccount: Account | null = null;
  users: IUser[] | null = null;
  eventSubscriber?: Subscription;

  totalItems?: number;
  itemsPerPage!: number;
  loading!: boolean;

  @ViewChild('userTable', { static: true })
  userTable!: Table;

  private filtersDetails: { [_: string]: { type: string } } = {};

  constructor(
    protected userService: UserManagementService,
    protected messageService: MessageService,
    protected accountService: AccountService,
    protected activatedRoute: ActivatedRoute,
    protected router: Router,
    protected confirmationService: ConfirmationService,
    protected translateService: TranslateService
  ) {
    this.itemsPerPage = ITEMS_PER_PAGE;
    this.loading = true;
  }

  ngOnInit(): void {
    this.accountService.identity().subscribe((account: Account | null) => {
      this.currentAccount = account;
    });
    this.activatedRoute.queryParams
      .pipe(
        tap(queryParams => fillTableFromQueryParams(this.userTable, queryParams, this.filtersDetails)),
        tap(() => (this.loading = true)),
        switchMap(() => this.userService.query(lazyLoadEventToServerQueryParams(this.userTable.createLazyLoadMetadata()))),
        filter((res: HttpResponse<IUser[]>) => res.ok)
      )
      .subscribe(
        (res: HttpResponse<IUser[]>) => {
          this.paginateUsers(res.body!, res.headers);
          this.loading = false;
        },
        (res: HttpErrorResponse) => {
          this.onError(res.message);
          this.loading = false;
        }
      );
  }

  onLazyLoadEvent(event: TableLazyLoadEvent): void {
    const queryParams = lazyLoadEventToRouterQueryParams(event, this.filtersDetails);
    this.router.navigate(['/admin/user-management'], { queryParams });
  }

  delete(login: string): void {
    this.confirmationService.confirm({
      header: this.translateService.instant('entity.delete.title'),
      message: this.translateService.instant('userManagement.delete.question', { login }),
      accept: () => {
        this.userService.delete(login).subscribe(() => {
          this.router.navigate(['/user'], { queryParams: { r: Date.now() }, queryParamsHandling: 'merge' });
        });
      },
    });
  }

  trackId(index: number, item: IUser): string {
    return item.login!;
  }

  setActive(user: IUser, isActivated: boolean): void {
    user.activated = isActivated;
    this.userService.update(user).subscribe();
  }

  protected paginateUsers(data: IUser[], headers: HttpHeaders): void {
    this.totalItems = Number(headers.get('X-Total-Count'));
    this.users = data;
  }

  protected onError(errorMessage: string): void {
    this.messageService.add({ severity: 'error', summary: errorMessage });
  }
}
