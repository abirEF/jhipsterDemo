import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { ICoffee } from '../coffee.model';
import { sampleWithRequiredData, sampleWithNewData, sampleWithPartialData, sampleWithFullData } from '../coffee.test-samples';

import { CoffeeService } from './coffee.service';

const requireRestSample: ICoffee = {
  ...sampleWithRequiredData,
};

describe('Coffee Service', () => {
  let service: CoffeeService;
  let httpMock: HttpTestingController;
  let expectedResult: ICoffee | ICoffee[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(CoffeeService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  describe('Service methods', () => {
    it('should find an element', () => {
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.find(123).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should create a Coffee', () => {
      // eslint-disable-next-line @typescript-eslint/no-unused-vars
      const coffee = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(coffee).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a Coffee', () => {
      const coffee = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(coffee).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a Coffee', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of Coffee', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a Coffee', () => {
      const expected = true;

      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult).toBe(expected);
    });

    describe('addCoffeeToCollectionIfMissing', () => {
      it('should add a Coffee to an empty array', () => {
        const coffee: ICoffee = sampleWithRequiredData;
        expectedResult = service.addCoffeeToCollectionIfMissing([], coffee);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(coffee);
      });

      it('should not add a Coffee to an array that contains it', () => {
        const coffee: ICoffee = sampleWithRequiredData;
        const coffeeCollection: ICoffee[] = [
          {
            ...coffee,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addCoffeeToCollectionIfMissing(coffeeCollection, coffee);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a Coffee to an array that doesn't contain it", () => {
        const coffee: ICoffee = sampleWithRequiredData;
        const coffeeCollection: ICoffee[] = [sampleWithPartialData];
        expectedResult = service.addCoffeeToCollectionIfMissing(coffeeCollection, coffee);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(coffee);
      });

      it('should add only unique Coffee to an array', () => {
        const coffeeArray: ICoffee[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const coffeeCollection: ICoffee[] = [sampleWithRequiredData];
        expectedResult = service.addCoffeeToCollectionIfMissing(coffeeCollection, ...coffeeArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const coffee: ICoffee = sampleWithRequiredData;
        const coffee2: ICoffee = sampleWithPartialData;
        expectedResult = service.addCoffeeToCollectionIfMissing([], coffee, coffee2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(coffee);
        expect(expectedResult).toContain(coffee2);
      });

      it('should accept null and undefined values', () => {
        const coffee: ICoffee = sampleWithRequiredData;
        expectedResult = service.addCoffeeToCollectionIfMissing([], null, coffee, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(coffee);
      });

      it('should return initial array if no Coffee is added', () => {
        const coffeeCollection: ICoffee[] = [sampleWithRequiredData];
        expectedResult = service.addCoffeeToCollectionIfMissing(coffeeCollection, undefined, null);
        expect(expectedResult).toEqual(coffeeCollection);
      });
    });

    describe('compareCoffee', () => {
      it('Should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareCoffee(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('Should return false if one entity is null', () => {
        const entity1 = { id: 123 };
        const entity2 = null;

        const compareResult1 = service.compareCoffee(entity1, entity2);
        const compareResult2 = service.compareCoffee(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey differs', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 456 };

        const compareResult1 = service.compareCoffee(entity1, entity2);
        const compareResult2 = service.compareCoffee(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey matches', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 123 };

        const compareResult1 = service.compareCoffee(entity1, entity2);
        const compareResult2 = service.compareCoffee(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
