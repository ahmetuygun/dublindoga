package com.hiking.dublindoga.web.rest;

import com.hiking.dublindoga.domain.AddJoinerRequest;
import com.hiking.dublindoga.domain.Event;
import com.hiking.dublindoga.repository.EventRepository;
import com.hiking.dublindoga.service.EventService;
import com.hiking.dublindoga.service.impl.PendingJoinerListFullException;
import com.hiking.dublindoga.web.rest.errors.BadRequestAlertException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.hiking.dublindoga.domain.Event}.
 */
@RestController
@RequestMapping("/api/events")
public class EventResource {

    private static final Logger LOG = LoggerFactory.getLogger(EventResource.class);

    private static final String ENTITY_NAME = "event";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final EventService eventService;

    private final EventRepository eventRepository;

    public EventResource(EventService eventService, EventRepository eventRepository) {
        this.eventService = eventService;
        this.eventRepository = eventRepository;
    }

    /**
     * {@code POST  /events} : Create a new event.
     *
     * @param event the event to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new event, or with status {@code 400 (Bad Request)} if the event has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    @CacheEvict(value = "eventsCache", allEntries = true)
    public ResponseEntity<Event> createEvent(@Valid @RequestBody Event event) throws URISyntaxException {
        LOG.debug("REST request to save Event : {}", event);
        if (event.getId() != null) {
            throw new BadRequestAlertException("A new event cannot already have an ID", ENTITY_NAME, "idexists");
        }
        event = eventService.save(event);
        return ResponseEntity.created(new URI("/api/events/" + event.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, event.getId().toString()))
            .body(event);
    }

    @PostMapping("/addJoiner")
    @CacheEvict(value = "eventSingle", key = "#addJoinerRequest.eventId")
    public ResponseEntity<Void> addJoiner(@Valid @RequestBody AddJoinerRequest addJoinerRequest) throws URISyntaxException, PendingJoinerListFullException {
        LOG.debug("REST request to addJoiner AddJoinerRequest : {}", addJoinerRequest);
        if (addJoinerRequest.getEventId() == null) {
            throw new BadRequestAlertException("EventId cant be null", ENTITY_NAME, "missingId");
        }
        eventService.addJoiner(addJoinerRequest);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{eventId}/joiners/{joinerId}")
    @CacheEvict(value = "eventSingle", key = "#eventId")
    public ResponseEntity<String> removeJoiner(@PathVariable Long eventId, @PathVariable Long joinerId) {
        LOG.debug("REST request to addJoiner AddJoinerRequest : {}", joinerId);
        if (eventId == null) {
            throw new BadRequestAlertException("EventId cant be null", ENTITY_NAME, "missingId");
        }
        eventService.removeJoiner(eventId,joinerId);
        return ResponseEntity.ok().build();
    }


    @PostMapping("/{eventId}/approve/{joinerId}")
    @CacheEvict(value = "eventSingle", key = "#eventId")
    public ResponseEntity<Void> approveJoiner(@PathVariable Long eventId, @PathVariable Long joinerId) {
        eventService.approveJoiner(eventId, joinerId);
        return ResponseEntity.ok().build();
    }


    @DeleteMapping("/{eventId}/approved/{joinerId}")
    @CacheEvict(value = "eventSingle", key = "#eventId")
    public ResponseEntity<Void> removeApprovedJoiner(@PathVariable Long eventId, @PathVariable Long joinerId) {
        eventService.removeApprovedJoiner(eventId, joinerId);
        return ResponseEntity.noContent().build();
    }



    /**
     * {@code PUT  /events/:id} : Updates an existing event.
     *
     * @param id the id of the event to save.
     * @param event the event to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated event,
     * or with status {@code 400 (Bad Request)} if the event is not valid,
     * or with status {@code 500 (Internal Server Error)} if the event couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<Event> updateEvent(@PathVariable(value = "id", required = false) final Long id, @Valid @RequestBody Event event)
        throws URISyntaxException {
        LOG.debug("REST request to update Event : {}, {}", id, event);
        if (event.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, event.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!eventRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        event = eventService.update(event);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, event.getId().toString()))
            .body(event);
    }

    /**
     * {@code PATCH  /events/:id} : Partial updates given fields of an existing event, field will ignore if it is null
     *
     * @param id the id of the event to save.
     * @param event the event to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated event,
     * or with status {@code 400 (Bad Request)} if the event is not valid,
     * or with status {@code 404 (Not Found)} if the event is not found,
     * or with status {@code 500 (Internal Server Error)} if the event couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<Event> partialUpdateEvent(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody Event event
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update Event partially : {}, {}", id, event);
        if (event.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, event.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!eventRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<Event> result = eventService.partialUpdate(event);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, event.getId().toString())
        );
    }

    /**
     * {@code GET  /events} : get all the events.
     *
     * @param pageable the pagination information.
     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of events in body.
     */

    @GetMapping("")
    @Cacheable(value = "eventsCache") // Use "eventsCache" as the cache name
    public ResponseEntity<List<Event>> getAllEvents(
        @org.springdoc.core.annotations.ParameterObject Pageable pageable,
        @RequestParam(name = "eagerload", required = false, defaultValue = "true") boolean eagerload
    ) {
        LOG.debug("REST request to get a page of Events");
        Page<Event> page;
        if (eagerload) {
            page = eventService.findAllWithEagerRelationships(pageable);
        } else {
            page = eventService.findAll(pageable);
        }
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /events/:id} : get the "id" event.
     *
     * @param id the id of the event to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the event, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    @Cacheable(value = "eventSingle", key = "#id")// Use "eventsCache" as the cache name
    public ResponseEntity<Event> getEvent(@PathVariable("id") Long id) {
        LOG.debug("REST request to get Event : {}", id);
        Optional<Event> event = eventService.findOne(id);
        return ResponseUtil.wrapOrNotFound(event);
    }

    /**
     * {@code DELETE  /events/:id} : delete the "id" event.
     *
     * @param id the id of the event to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    @CacheEvict(value = "eventsCache", allEntries = true)
    public ResponseEntity<Void> deleteEvent(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete Event : {}", id);
        eventService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }
}
