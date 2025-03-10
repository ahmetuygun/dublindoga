import { IUser } from 'app/entities/user/user.model';
import { IEvent } from 'app/entities/event/event.model';
import { JoinStatus } from 'app/entities/enumerations/join-status.model';
import { Gender } from 'app/entities/enumerations/gender.model';

export interface IJoiner {
  id: number;
  fullName?: string | null;
  email?: string | null;
  phone?: string | null;
  status?: keyof typeof JoinStatus | null;
  photo1?: string | null;
  photo1ContentType?: string | null;
  gender?: keyof typeof Gender | null;
  point?: number | null;
  internalUser?: Pick<IUser, 'id'> | null;
  pendingEvents?: IEvent[] | null;
  aprovedEvents?: IEvent[] | null;
}

export type NewJoiner = Omit<IJoiner, 'id'> & { id: null };
