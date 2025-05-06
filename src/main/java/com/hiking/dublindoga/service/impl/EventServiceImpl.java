package com.hiking.dublindoga.service.impl;

import com.hiking.dublindoga.domain.AddJoinerRequest;
import com.hiking.dublindoga.domain.Event;
import com.hiking.dublindoga.domain.JoinStatus;
import com.hiking.dublindoga.domain.Joiner;
import com.hiking.dublindoga.repository.EventRepository;
import com.hiking.dublindoga.repository.JoinerRepository;
import com.hiking.dublindoga.repository.UserRepository;
import com.hiking.dublindoga.service.EventService;

import java.util.NoSuchElementException;
import java.util.Optional;

import com.hiking.dublindoga.service.MailService;
import org.hibernate.Hibernate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link Event}.
 */
@Service
@Transactional
public class EventServiceImpl implements EventService {

    private static final Logger LOG = LoggerFactory.getLogger(EventServiceImpl.class);

    private final EventRepository eventRepository;

    private final UserRepository userRepository;

    private final JoinerRepository joinerRepository;

    private final MailService mailService;



    public EventServiceImpl(EventRepository eventRepository, UserRepository userRepository, JoinerRepository joinerRepository, MailService mailService) {
        this.eventRepository = eventRepository;
        this.userRepository = userRepository;
        this.joinerRepository = joinerRepository;
        this.mailService = mailService;
    }

    @Override
    public Event save(Event event) {
        LOG.debug("Request to save Event : {}", event);
        return eventRepository.save(event);
    }

    @Override
    public Event update(Event event) {
        LOG.debug("Request to update Event : {}", event);
        return eventRepository.save(event);
    }

    @Override
    public Optional<Event> partialUpdate(Event event) {
        LOG.debug("Request to partially update Event : {}", event);

        return eventRepository
            .findById(event.getId())
            .map(existingEvent -> {
                if (event.getName() != null) {
                    existingEvent.setName(event.getName());
                }
                if (event.getDescription() != null) {
                    existingEvent.setDescription(event.getDescription());
                }
                if (event.getLocation() != null) {
                    existingEvent.setLocation(event.getLocation());
                }
                if (event.getDate() != null) {
                    existingEvent.setDate(event.getDate());
                }
                if (event.getDifficulty() != null) {
                    existingEvent.setDifficulty(event.getDifficulty());
                }
                if (event.getPhoto1() != null) {
                    existingEvent.setPhoto1(event.getPhoto1());
                }
                if (event.getPhoto1ContentType() != null) {
                    existingEvent.setPhoto1ContentType(event.getPhoto1ContentType());
                }
                if (event.getPhoto2() != null) {
                    existingEvent.setPhoto2(event.getPhoto2());
                }
                if (event.getPhoto2ContentType() != null) {
                    existingEvent.setPhoto2ContentType(event.getPhoto2ContentType());
                }
                if (event.getPhoto3() != null) {
                    existingEvent.setPhoto3(event.getPhoto3());
                }
                if (event.getPhoto3ContentType() != null) {
                    existingEvent.setPhoto3ContentType(event.getPhoto3ContentType());
                }
                if (event.getLimit() != null) {
                    existingEvent.setLimit(event.getLimit());
                }

                return existingEvent;
            })
            .map(eventRepository::save);
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable("eventsCache")
    public Page<Event> findAll(Pageable pageable) {
        LOG.debug("Request to get all Events");
        return eventRepository.findAll(pageable);
    }

    public Page<Event> findAllWithEagerRelationships(Pageable pageable) {
        return eventRepository.findAllWithEagerRelationships(pageable);
    }
    public Optional<Event> findOne(Long id) {
        LOG.debug("Request to get Event : {}", id);
        return eventRepository.findById(id);

    }
    public Optional<Event> findOneForAdmin(Long id) {
        LOG.debug("Request to get Event for admin : {}", id);
        return eventRepository.findOneWithEagerRelationships(id);

    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete Event : {}", id);
        eventRepository.deleteById(id);
    }

    @Override
    public void addJoiner(AddJoinerRequest addJoinerRequest) throws PendingJoinerListFullException {

        Event event = findOne(addJoinerRequest.getEventId())
            .orElseThrow(() -> new NoSuchElementException("Event not found with id: " + addJoinerRequest.getEventId()));

        Joiner joiner = joinerRepository.findById(addJoinerRequest.getJoinerId())
            .orElseThrow(() -> new NoSuchElementException("Joiner not found with id: " + addJoinerRequest.getJoinerId()));

        if(event.getPendingJoiners().size() < event.getLimit() * 2){
            event.addPendingJoiner(joiner);

        }else {
           throw  new PendingJoinerListFullException("Pending List is Full!");
        }
        save(event);
    }

    @Override
    public void removeJoiner(Long eventId, Long joinerId) {

        Event event = findOne(eventId)
            .orElseThrow(() -> new NoSuchElementException("Event not found with id: " + eventId));

        Joiner joiner = joinerRepository.findByInternalUserId(joinerId)
            .orElseThrow(() -> new NoSuchElementException("Joiner not found with id: " + joinerId));

        event.removeApprovedJoiner(joiner);
        event.removePendingJoiner(joiner);

        save(event);

    }

    @Transactional
    @Override
    public void approveJoiner(Long eventId, Long joinerId) {
        Event event = findOne(eventId)
            .orElseThrow(() -> new NoSuchElementException("Event not found with id: " + eventId));

        Joiner joiner = joinerRepository.findById(joinerId)
            .orElseThrow(() -> new NoSuchElementException("Joiner not found with id: " + joinerId));

        // Initialize the collection to avoid LazyInitializationException
        Hibernate.initialize(event.getPendingJoiners());

        if (event.getPendingJoiners().contains(joiner)) {
            event.removePendingJoiner(joiner);
            event.addApprovedJoiner(joiner);
        }
        save(event);
        mailService.sendAttendanceApprovedMail(event, joiner);
    }


    @Override
    public void removeApprovedJoiner(Long eventId, Long joinerId) {

        Event event = findOne(eventId)
            .orElseThrow(() -> new NoSuchElementException("Event not found with id: " + eventId));

        Joiner joiner = joinerRepository.findById(joinerId)
            .orElseThrow(() -> new NoSuchElementException("Joiner not found with id: " + joinerId));

        if(event.getApprovedJoiners().contains(joiner)){
            event.removeApprovedJoiner(joiner);
            event.addPendingJoiner(joiner);
        }
        save(event);
    }

    @Override
    public Optional<JoinStatus> checkAttendance(Long eventId, Long joinerId) {

        boolean approved = false;
        boolean pending = false;
        pending = eventRepository.existsPendingJoinerForEvent(eventId, joinerId);

        if(!pending){
            approved =  eventRepository.existsApprovedJoinerForEvent(eventId, joinerId);
        }

        return Optional.of(new JoinStatus(approved,pending));
    }
}
