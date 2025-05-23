import { Routes } from '@angular/router';

const routes: Routes = [
  {
    path: 'authority',
    data: { pageTitle: 'Authorities' },
    loadChildren: () => import('./admin/authority/authority.routes'),
  },
  {
    path: 'event',
    data: { pageTitle: 'Etkinlikler' },
    loadChildren: () => import('./event/event.routes'),
  },
  {
    path: 'joiner',
    data: { pageTitle: 'Katilimcilar' },
    loadChildren: () => import('./joiner/joiner.routes'),
  },
  /* jhipster-needle-add-entity-route - JHipster will add entity modules routes here */
];

export default routes;
