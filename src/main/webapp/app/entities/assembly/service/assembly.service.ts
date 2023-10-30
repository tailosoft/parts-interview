import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IAssembly, getAssemblyIdentifier } from '../assembly.model';

export type EntityResponseType = HttpResponse<IAssembly>;
export type EntityArrayResponseType = HttpResponse<IAssembly[]>;

@Injectable({ providedIn: 'root' })
export class AssemblyService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/assemblies');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(assembly: IAssembly): Observable<EntityResponseType> {
    return this.http.post<IAssembly>(this.resourceUrl, assembly, { observe: 'response' });
  }

  update(assembly: IAssembly): Observable<EntityResponseType> {
    return this.http.put<IAssembly>(`${this.resourceUrl}/${getAssemblyIdentifier(assembly)}`, assembly, { observe: 'response' });
  }

  partialUpdate(assembly: IAssembly): Observable<EntityResponseType> {
    return this.http.patch<IAssembly>(`${this.resourceUrl}/${getAssemblyIdentifier(assembly)!}`, assembly, { observe: 'response' });
  }

  find(parentId: number, childId: number): Observable<EntityResponseType> {
    return this.http.get<IAssembly>(`${this.resourceUrl}/parentId=${parentId};childId=${childId}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IAssembly[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(parentId: number, childId: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/parentId=${parentId};childId=${childId}`, { observe: 'response' });
  }
}
