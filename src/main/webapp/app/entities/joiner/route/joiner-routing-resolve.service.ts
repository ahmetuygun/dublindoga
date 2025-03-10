import { inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { EMPTY, Observable, of } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IJoiner } from '../joiner.model';
import { JoinerService } from '../service/joiner.service';

const joinerResolve = (route: ActivatedRouteSnapshot): Observable<null | IJoiner> => {
  const id = route.params.id;
  if (id) {
    return inject(JoinerService)
      .find(id)
      .pipe(
        mergeMap((joiner: HttpResponse<IJoiner>) => {
          if (joiner.body) {
            return of(joiner.body);
          }
          inject(Router).navigate(['404']);
          return EMPTY;
        }),
      );
  }
  return of(null);
};

export default joinerResolve;
