import { Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { ASC } from 'app/config/navigation.constants';
import JoinerResolve from './route/joiner-routing-resolve.service';

const joinerRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/joiner.component').then(m => m.JoinerComponent),
    data: {
      defaultSort: `id,${ASC}`,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/joiner-detail.component').then(m => m.JoinerDetailComponent),
    resolve: {
      joiner: JoinerResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/joiner-update.component').then(m => m.JoinerUpdateComponent),
    resolve: {
      joiner: JoinerResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/joiner-update.component').then(m => m.JoinerUpdateComponent),
    resolve: {
      joiner: JoinerResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default joinerRoute;
