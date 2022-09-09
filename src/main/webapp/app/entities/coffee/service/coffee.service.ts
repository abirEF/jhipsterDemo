import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { ICoffee, NewCoffee } from '../coffee.model';

export type PartialUpdateCoffee = Partial<ICoffee> & Pick<ICoffee, 'id'>;

export type EntityResponseType = HttpResponse<ICoffee>;
export type EntityArrayResponseType = HttpResponse<ICoffee[]>;

@Injectable({ providedIn: 'root' })
export class CoffeeService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/coffees');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(coffee: NewCoffee): Observable<EntityResponseType> {
    return this.http.post<ICoffee>(this.resourceUrl, coffee, { observe: 'response' });
  }

  update(coffee: ICoffee): Observable<EntityResponseType> {
    return this.http.put<ICoffee>(`${this.resourceUrl}/${this.getCoffeeIdentifier(coffee)}`, coffee, { observe: 'response' });
  }

  partialUpdate(coffee: PartialUpdateCoffee): Observable<EntityResponseType> {
    return this.http.patch<ICoffee>(`${this.resourceUrl}/${this.getCoffeeIdentifier(coffee)}`, coffee, { observe: 'response' });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<ICoffee>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<ICoffee[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  getCoffeeIdentifier(coffee: Pick<ICoffee, 'id'>): number {
    return coffee.id;
  }

  compareCoffee(o1: Pick<ICoffee, 'id'> | null, o2: Pick<ICoffee, 'id'> | null): boolean {
    return o1 && o2 ? this.getCoffeeIdentifier(o1) === this.getCoffeeIdentifier(o2) : o1 === o2;
  }

  addCoffeeToCollectionIfMissing<Type extends Pick<ICoffee, 'id'>>(
    coffeeCollection: Type[],
    ...coffeesToCheck: (Type | null | undefined)[]
  ): Type[] {
    const coffees: Type[] = coffeesToCheck.filter(isPresent);
    if (coffees.length > 0) {
      const coffeeCollectionIdentifiers = coffeeCollection.map(coffeeItem => this.getCoffeeIdentifier(coffeeItem)!);
      const coffeesToAdd = coffees.filter(coffeeItem => {
        const coffeeIdentifier = this.getCoffeeIdentifier(coffeeItem);
        if (coffeeCollectionIdentifiers.includes(coffeeIdentifier)) {
          return false;
        }
        coffeeCollectionIdentifiers.push(coffeeIdentifier);
        return true;
      });
      return [...coffeesToAdd, ...coffeeCollection];
    }
    return coffeeCollection;
  }
}
