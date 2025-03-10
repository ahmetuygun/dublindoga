import { IJoiner, NewJoiner } from './joiner.model';

export const sampleWithRequiredData: IJoiner = {
  id: 26581,
  fullName: 'unless transparency besides',
  email: 'K8hrlang8hc27@yahoo.com',
  status: 'PENDING',
};

export const sampleWithPartialData: IJoiner = {
  id: 22399,
  fullName: 'tomorrow profitable strong',
  email: 'Bulgak.Kasapoglu10@gmail.com',
  status: 'PENDING',
  gender: 'WOMAN',
  point: 7177,
};

export const sampleWithFullData: IJoiner = {
  id: 5723,
  fullName: 'till garrote legitimize',
  email: 'Berginsenge6@gmail.com',
  phone: '+90-241-409-6-952',
  status: 'CONFIRMED',
  photo1: '../fake-data/blob/hipster.png',
  photo1ContentType: 'unknown',
  gender: 'MAN',
  point: 25436,
};

export const sampleWithNewData: NewJoiner = {
  fullName: 'whereas',
  email: 'Bolen_Kumcuoglu@hotmail.com',
  status: 'CANCELLED',
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
