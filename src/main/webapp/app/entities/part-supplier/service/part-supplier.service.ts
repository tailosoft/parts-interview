import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IPartSupplier, getPartSupplierIdentifier } from '../part-supplier.model';

export type EntityResponseType = HttpResponse<IPartSupplier>;
export type EntityArrayResponseType = HttpResponse<IPartSupplier[]>;

@Injectable({ providedIn: 'root' })
export class PartSupplierService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/part-suppliers');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(partSupplier: IPartSupplier): Observable<EntityResponseType> {
    return this.http.post<IPartSupplier>(this.resourceUrl, partSupplier, { observe: 'response' });
  }

  update(partSupplier: IPartSupplier): Observable<EntityResponseType> {
    return this.http.put<IPartSupplier>(`${this.resourceUrl}/${getPartSupplierIdentifier(partSupplier)}`, partSupplier, {
      observe: 'response',
    });
  }

  partialUpdate(partSupplier: IPartSupplier): Observable<EntityResponseType> {
    return this.http.patch<IPartSupplier>(`${this.resourceUrl}/${getPartSupplierIdentifier(partSupplier)!}`, partSupplier, {
      observe: 'response',
    });
  }

  find(partId: number, supplierId: number): Observable<EntityResponseType> {
    return this.http.get<IPartSupplier>(`${this.resourceUrl}/partId=${partId};supplierId=${supplierId}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IPartSupplier[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(partId: number, supplierId: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/partId=${partId};supplierId=${supplierId}`, { observe: 'response' });
  }
}
