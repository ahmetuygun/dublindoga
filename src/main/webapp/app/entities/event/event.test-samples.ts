import dayjs from 'dayjs/esm';

import { IEvent, NewEvent } from './event.model';

export const sampleWithRequiredData: IEvent = {
  id: 18174,
  name: 'scent yum',
  location: 'indeed quaintly',
  date: dayjs('2025-03-02T01:01'),
  difficulty: 'MEDIUM',
};

export const sampleWithPartialData: IEvent = {
  id: 260,
  name: 'across tabulate aw',
  location: 'operating',
  date: dayjs('2025-03-02T05:01'),
  difficulty: 'EXTREME',
  photo1: '../fake-data/blob/hipster.png',
  photo1ContentType: 'unknown',
};

export const sampleWithFullData: IEvent = {
  id: 14744,
  name: 'know yahoo',
  description: 'opera overstay',
  location: 'when merge',
  date: dayjs('2025-03-02T13:30'),
  difficulty: 'MEDIUM',
  photo1: '../fake-data/blob/hipster.png',
  photo1ContentType: 'unknown',
  photo2: '../fake-data/blob/hipster.png',
  photo2ContentType: 'unknown',
  photo3: '../fake-data/blob/hipster.png',
  photo3ContentType: 'unknown',
  limit: 25282,
};

export const sampleWithNewData: NewEvent = {
  name: 'throughout solder aw',
  location: 'spork innocently when',
  date: dayjs('2025-03-02T07:42'),
  difficulty: 'EASY',
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
