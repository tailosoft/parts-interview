import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IPart, getPartIdentifier } from '../part.model';

export type EntityResponseType = HttpResponse<IPart>;
export type EntityArrayResponseType = HttpResponse<IPart[]>;

@Injectable({ providedIn: 'root' })
export class PartService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/parts');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(part: IPart): Observable<EntityResponseType> {
    return this.http.post<IPart>(this.resourceUrl, part, { observe: 'response' });
  }

  update(part: IPart): Observable<EntityResponseType> {
    return this.http.put<IPart>(`${this.resourceUrl}/${getPartIdentifier(part)}`, part, { observe: 'response' });
  }

  partialUpdate(part: IPart): Observable<EntityResponseType> {
    return this.http.patch<IPart>(`${this.resourceUrl}/${getPartIdentifier(part)!}`, part, { observe: 'response' });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<IPart>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IPart[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }
}
