import { Routes } from '@angular/router';

const routes: Routes = [
  {
    path: 'authority',
    data: { pageTitle: 'Authorities' },
    loadChildren: () => import('./admin/authority/authority.routes'),
  },
  {
    path: 'event',
    data: { pageTitle: 'Events' },
    loadChildren: () => import('./event/event.routes'),
  },
  {
    path: 'joiner',
    data: { pageTitle: 'Joiners' },
    loadChildren: () => import('./joiner/joiner.routes'),
  },
  /* jhipster-needle-add-entity-route - JHipster will add entity modules routes here */
];

export default routes;
