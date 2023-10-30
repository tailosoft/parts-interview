import { MessageService } from 'primeng/api';
import { TranslateService } from '@ngx-translate/core';
import { HttpInterceptor, HttpRequest, HttpResponse, HttpHandler, HttpEvent } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { tap } from 'rxjs/operators';

@Injectable()
export class NotificationInterceptor implements HttpInterceptor {
  constructor(private messageService: MessageService, private translateService: TranslateService) {}

  intercept(request: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    return next.handle(request).pipe(
      tap((event: HttpEvent<any>) => {
        if (event instanceof HttpResponse) {
          let alert: string | null = null;
          let alertParams: string | null = null;

          for (const headerKey of event.headers.keys()) {
            if (headerKey.toLowerCase().endsWith('app-alert')) {
              alert = event.headers.get(headerKey);
            } else if (headerKey.toLowerCase().endsWith('app-params')) {
              alertParams = decodeURIComponent(event.headers.get(headerKey)!.replace(/\+/g, ' '));
            }
          }

          if (alert) {
            this.messageService.add({ severity: 'success', summary: this.translateService.instant(alert, { param: alertParams }) });
          }
        }
      })
    );
  }
}
