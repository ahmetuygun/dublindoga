import { TestBed } from '@angular/core/testing';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { provideHttpClient } from '@angular/common/http';

import { IJoiner } from '../joiner.model';
import { sampleWithFullData, sampleWithNewData, sampleWithPartialData, sampleWithRequiredData } from '../joiner.test-samples';

import { JoinerService } from './joiner.service';

const requireRestSample: IJoiner = {
  ...sampleWithRequiredData,
};

describe('Joiner Service', () => {
  let service: JoinerService;
  let httpMock: HttpTestingController;
  let expectedResult: IJoiner | IJoiner[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClient(), provideHttpClientTesting()],
    });
    expectedResult = null;
    service = TestBed.inject(JoinerService);
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

    it('should create a Joiner', () => {
      const joiner = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(joiner).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a Joiner', () => {
      const joiner = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(joiner).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a Joiner', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of Joiner', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a Joiner', () => {
      const expected = true;

      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult).toBe(expected);
    });

    describe('addJoinerToCollectionIfMissing', () => {
      it('should add a Joiner to an empty array', () => {
        const joiner: IJoiner = sampleWithRequiredData;
        expectedResult = service.addJoinerToCollectionIfMissing([], joiner);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(joiner);
      });

      it('should not add a Joiner to an array that contains it', () => {
        const joiner: IJoiner = sampleWithRequiredData;
        const joinerCollection: IJoiner[] = [
          {
            ...joiner,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addJoinerToCollectionIfMissing(joinerCollection, joiner);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a Joiner to an array that doesn't contain it", () => {
        const joiner: IJoiner = sampleWithRequiredData;
        const joinerCollection: IJoiner[] = [sampleWithPartialData];
        expectedResult = service.addJoinerToCollectionIfMissing(joinerCollection, joiner);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(joiner);
      });

      it('should add only unique Joiner to an array', () => {
        const joinerArray: IJoiner[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const joinerCollection: IJoiner[] = [sampleWithRequiredData];
        expectedResult = service.addJoinerToCollectionIfMissing(joinerCollection, ...joinerArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const joiner: IJoiner = sampleWithRequiredData;
        const joiner2: IJoiner = sampleWithPartialData;
        expectedResult = service.addJoinerToCollectionIfMissing([], joiner, joiner2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(joiner);
        expect(expectedResult).toContain(joiner2);
      });

      it('should accept null and undefined values', () => {
        const joiner: IJoiner = sampleWithRequiredData;
        expectedResult = service.addJoinerToCollectionIfMissing([], null, joiner, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(joiner);
      });

      it('should return initial array if no Joiner is added', () => {
        const joinerCollection: IJoiner[] = [sampleWithRequiredData];
        expectedResult = service.addJoinerToCollectionIfMissing(joinerCollection, undefined, null);
        expect(expectedResult).toEqual(joinerCollection);
      });
    });

    describe('compareJoiner', () => {
      it('Should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareJoiner(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('Should return false if one entity is null', () => {
        const entity1 = { id: 28560 };
        const entity2 = null;

        const compareResult1 = service.compareJoiner(entity1, entity2);
        const compareResult2 = service.compareJoiner(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey differs', () => {
        const entity1 = { id: 28560 };
        const entity2 = { id: 20001 };

        const compareResult1 = service.compareJoiner(entity1, entity2);
        const compareResult2 = service.compareJoiner(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey matches', () => {
        const entity1 = { id: 28560 };
        const entity2 = { id: 28560 };

        const compareResult1 = service.compareJoiner(entity1, entity2);
        const compareResult2 = service.compareJoiner(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
