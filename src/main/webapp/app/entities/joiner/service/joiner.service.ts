import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IJoiner, NewJoiner } from '../joiner.model';

export type PartialUpdateJoiner = Partial<IJoiner> & Pick<IJoiner, 'id'>;

export type EntityResponseType = HttpResponse<IJoiner>;
export type EntityArrayResponseType = HttpResponse<IJoiner[]>;

@Injectable({ providedIn: 'root' })
export class JoinerService {
  protected readonly http = inject(HttpClient);
  protected readonly applicationConfigService = inject(ApplicationConfigService);

  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/joiners');

  create(joiner: NewJoiner): Observable<EntityResponseType> {
    return this.http.post<IJoiner>(this.resourceUrl, joiner, { observe: 'response' });
  }

  update(joiner: IJoiner): Observable<EntityResponseType> {
    return this.http.put<IJoiner>(`${this.resourceUrl}/${this.getJoinerIdentifier(joiner)}`, joiner, { observe: 'response' });
  }

  partialUpdate(joiner: PartialUpdateJoiner): Observable<EntityResponseType> {
    return this.http.patch<IJoiner>(`${this.resourceUrl}/${this.getJoinerIdentifier(joiner)}`, joiner, { observe: 'response' });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<IJoiner>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IJoiner[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  getJoinerIdentifier(joiner: Pick<IJoiner, 'id'>): number {
    return joiner.id;
  }

  compareJoiner(o1: Pick<IJoiner, 'id'> | null, o2: Pick<IJoiner, 'id'> | null): boolean {
    return o1 && o2 ? this.getJoinerIdentifier(o1) === this.getJoinerIdentifier(o2) : o1 === o2;
  }

  addJoinerToCollectionIfMissing<Type extends Pick<IJoiner, 'id'>>(
    joinerCollection: Type[],
    ...joinersToCheck: (Type | null | undefined)[]
  ): Type[] {
    const joiners: Type[] = joinersToCheck.filter(isPresent);
    if (joiners.length > 0) {
      const joinerCollectionIdentifiers = joinerCollection.map(joinerItem => this.getJoinerIdentifier(joinerItem));
      const joinersToAdd = joiners.filter(joinerItem => {
        const joinerIdentifier = this.getJoinerIdentifier(joinerItem);
        if (joinerCollectionIdentifiers.includes(joinerIdentifier)) {
          return false;
        }
        joinerCollectionIdentifiers.push(joinerIdentifier);
        return true;
      });
      return [...joinersToAdd, ...joinerCollection];
    }
    return joinerCollection;
  }
}
