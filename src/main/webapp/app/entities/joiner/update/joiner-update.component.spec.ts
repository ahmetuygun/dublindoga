import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse, provideHttpClient } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Subject, from, of } from 'rxjs';

import { IUser } from 'app/entities/user/user.model';
import { UserService } from 'app/entities/user/service/user.service';
import { IEvent } from 'app/entities/event/event.model';
import { EventService } from 'app/entities/event/service/event.service';
import { IJoiner } from '../joiner.model';
import { JoinerService } from '../service/joiner.service';
import { JoinerFormService } from './joiner-form.service';

import { JoinerUpdateComponent } from './joiner-update.component';

describe('Joiner Management Update Component', () => {
  let comp: JoinerUpdateComponent;
  let fixture: ComponentFixture<JoinerUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let joinerFormService: JoinerFormService;
  let joinerService: JoinerService;
  let userService: UserService;
  let eventService: EventService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [JoinerUpdateComponent],
      providers: [
        provideHttpClient(),
        FormBuilder,
        {
          provide: ActivatedRoute,
          useValue: {
            params: from([{}]),
          },
        },
      ],
    })
      .overrideTemplate(JoinerUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(JoinerUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    joinerFormService = TestBed.inject(JoinerFormService);
    joinerService = TestBed.inject(JoinerService);
    userService = TestBed.inject(UserService);
    eventService = TestBed.inject(EventService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call User query and add missing value', () => {
      const joiner: IJoiner = { id: 20001 };
      const internalUser: IUser = { id: 3944 };
      joiner.internalUser = internalUser;

      const userCollection: IUser[] = [{ id: 3944 }];
      jest.spyOn(userService, 'query').mockReturnValue(of(new HttpResponse({ body: userCollection })));
      const additionalUsers = [internalUser];
      const expectedCollection: IUser[] = [...additionalUsers, ...userCollection];
      jest.spyOn(userService, 'addUserToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ joiner });
      comp.ngOnInit();

      expect(userService.query).toHaveBeenCalled();
      expect(userService.addUserToCollectionIfMissing).toHaveBeenCalledWith(
        userCollection,
        ...additionalUsers.map(expect.objectContaining),
      );
      expect(comp.usersSharedCollection).toEqual(expectedCollection);
    });

    it('Should call Event query and add missing value', () => {
      const joiner: IJoiner = { id: 20001 };
      const pendingEvents: IEvent[] = [{ id: 22576 }];
      joiner.pendingEvents = pendingEvents;
      const aprovedEvents: IEvent[] = [{ id: 22576 }];
      joiner.aprovedEvents = aprovedEvents;

      const eventCollection: IEvent[] = [{ id: 22576 }];
      jest.spyOn(eventService, 'query').mockReturnValue(of(new HttpResponse({ body: eventCollection })));
      const additionalEvents = [...pendingEvents, ...aprovedEvents];
      const expectedCollection: IEvent[] = [...additionalEvents, ...eventCollection];
      jest.spyOn(eventService, 'addEventToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ joiner });
      comp.ngOnInit();

      expect(eventService.query).toHaveBeenCalled();
      expect(eventService.addEventToCollectionIfMissing).toHaveBeenCalledWith(
        eventCollection,
        ...additionalEvents.map(expect.objectContaining),
      );
      expect(comp.eventsSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const joiner: IJoiner = { id: 20001 };
      const internalUser: IUser = { id: 3944 };
      joiner.internalUser = internalUser;
      const pendingEvents: IEvent = { id: 22576 };
      joiner.pendingEvents = [pendingEvents];
      const aprovedEvents: IEvent = { id: 22576 };
      joiner.aprovedEvents = [aprovedEvents];

      activatedRoute.data = of({ joiner });
      comp.ngOnInit();

      expect(comp.usersSharedCollection).toContainEqual(internalUser);
      expect(comp.eventsSharedCollection).toContainEqual(pendingEvents);
      expect(comp.eventsSharedCollection).toContainEqual(aprovedEvents);
      expect(comp.joiner).toEqual(joiner);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IJoiner>>();
      const joiner = { id: 28560 };
      jest.spyOn(joinerFormService, 'getJoiner').mockReturnValue(joiner);
      jest.spyOn(joinerService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ joiner });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: joiner }));
      saveSubject.complete();

      // THEN
      expect(joinerFormService.getJoiner).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(joinerService.update).toHaveBeenCalledWith(expect.objectContaining(joiner));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IJoiner>>();
      const joiner = { id: 28560 };
      jest.spyOn(joinerFormService, 'getJoiner').mockReturnValue({ id: null });
      jest.spyOn(joinerService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ joiner: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: joiner }));
      saveSubject.complete();

      // THEN
      expect(joinerFormService.getJoiner).toHaveBeenCalled();
      expect(joinerService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IJoiner>>();
      const joiner = { id: 28560 };
      jest.spyOn(joinerService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ joiner });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(joinerService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Compare relationships', () => {
    describe('compareUser', () => {
      it('Should forward to userService', () => {
        const entity = { id: 3944 };
        const entity2 = { id: 6275 };
        jest.spyOn(userService, 'compareUser');
        comp.compareUser(entity, entity2);
        expect(userService.compareUser).toHaveBeenCalledWith(entity, entity2);
      });
    });

    describe('compareEvent', () => {
      it('Should forward to eventService', () => {
        const entity = { id: 22576 };
        const entity2 = { id: 3268 };
        jest.spyOn(eventService, 'compareEvent');
        comp.compareEvent(entity, entity2);
        expect(eventService.compareEvent).toHaveBeenCalledWith(entity, entity2);
      });
    });
  });
});
