import dayjs from 'dayjs/esm';
import { IJoiner } from 'app/entities/joiner/joiner.model';
import { Difficulty } from 'app/entities/enumerations/difficulty.model';

export interface IEvent {
  id: number;
  name?: string | null;
  description?: string | null;
  location?: string | null;
  date?: dayjs.Dayjs | null;
  difficulty?: keyof typeof Difficulty | null;
  photo1?: string | null;
  photo1ContentType?: string | null;
  photo2?: string | null;
  photo2ContentType?: string | null;
  photo3?: string | null;
  photo3ContentType?: string | null;
  limit?: number | null;
  pendingJoiners?: IJoiner[] | null;
  approvedJoiners?: IJoiner[] | null;
}

export type NewEvent = Omit<IEvent, 'id'> & { id: null };
