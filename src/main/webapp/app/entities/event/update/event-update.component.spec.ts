import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse, provideHttpClient } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Subject, from, of } from 'rxjs';

import { IJoiner } from 'app/entities/joiner/joiner.model';
import { JoinerService } from 'app/entities/joiner/service/joiner.service';
import { EventService } from '../service/event.service';
import { IEvent } from '../event.model';
import { EventFormService } from './event-form.service';

import { EventUpdateComponent } from './event-update.component';

describe('Event Management Update Component', () => {
  let comp: EventUpdateComponent;
  let fixture: ComponentFixture<EventUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let eventFormService: EventFormService;
  let eventService: EventService;
  let joinerService: JoinerService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [EventUpdateComponent],
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
      .overrideTemplate(EventUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(EventUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    eventFormService = TestBed.inject(EventFormService);
    eventService = TestBed.inject(EventService);
    joinerService = TestBed.inject(JoinerService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call Joiner query and add missing value', () => {
      const event: IEvent = { id: 3268 };
      const pendingJoiners: IJoiner[] = [{ id: 28560 }];
      event.pendingJoiners = pendingJoiners;
      const approvedJoiners: IJoiner[] = [{ id: 28560 }];
      event.approvedJoiners = approvedJoiners;

      const joinerCollection: IJoiner[] = [{ id: 28560 }];
      jest.spyOn(joinerService, 'query').mockReturnValue(of(new HttpResponse({ body: joinerCollection })));
      const additionalJoiners = [...pendingJoiners, ...approvedJoiners];
      const expectedCollection: IJoiner[] = [...additionalJoiners, ...joinerCollection];
      jest.spyOn(joinerService, 'addJoinerToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ event });
      comp.ngOnInit();

      expect(joinerService.query).toHaveBeenCalled();
      expect(joinerService.addJoinerToCollectionIfMissing).toHaveBeenCalledWith(
        joinerCollection,
        ...additionalJoiners.map(expect.objectContaining),
      );
      expect(comp.joinersSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const event: IEvent = { id: 3268 };
      const pendingJoiner: IJoiner = { id: 28560 };
      event.pendingJoiners = [pendingJoiner];
      const approvedJoiner: IJoiner = { id: 28560 };
      event.approvedJoiners = [approvedJoiner];

      activatedRoute.data = of({ event });
      comp.ngOnInit();

      expect(comp.joinersSharedCollection).toContainEqual(pendingJoiner);
      expect(comp.joinersSharedCollection).toContainEqual(approvedJoiner);
      expect(comp.event).toEqual(event);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IEvent>>();
      const event = { id: 22576 };
      jest.spyOn(eventFormService, 'getEvent').mockReturnValue(event);
      jest.spyOn(eventService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ event });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: event }));
      saveSubject.complete();

      // THEN
      expect(eventFormService.getEvent).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(eventService.update).toHaveBeenCalledWith(expect.objectContaining(event));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IEvent>>();
      const event = { id: 22576 };
      jest.spyOn(eventFormService, 'getEvent').mockReturnValue({ id: null });
      jest.spyOn(eventService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ event: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: event }));
      saveSubject.complete();

      // THEN
      expect(eventFormService.getEvent).toHaveBeenCalled();
      expect(eventService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IEvent>>();
      const event = { id: 22576 };
      jest.spyOn(eventService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ event });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(eventService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Compare relationships', () => {
    describe('compareJoiner', () => {
      it('Should forward to joinerService', () => {
        const entity = { id: 28560 };
        const entity2 = { id: 20001 };
        jest.spyOn(joinerService, 'compareJoiner');
        comp.compareJoiner(entity, entity2);
        expect(joinerService.compareJoiner).toHaveBeenCalledWith(entity, entity2);
      });
    });
  });
});
