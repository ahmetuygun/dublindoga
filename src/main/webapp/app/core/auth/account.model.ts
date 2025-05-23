
import { IJoiner } from 'app/entities/joiner/joiner.model';

export class Account {
  constructor(
    public id: number | null,
    public activated: boolean,
    public authorities: string[],
    public email: string,
    public firstName: string | null,
    public langKey: string,
    public lastName: string | null,
    public login: string,
    public imageUrl: string | null,
     public joiner?: IJoiner ,
  ) {}
}
