package com.hiking.dublindoga.service;

import com.hiking.dublindoga.domain.AddJoinerRequest;
import com.hiking.dublindoga.domain.Event;
import java.util.Optional;

import com.hiking.dublindoga.service.impl.PendingJoinerListFullException;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing {@link com.hiking.dublindoga.domain.Event}.
 */
public interface EventService {
    /**
     * Save a event.
     *
     * @param event the entity to save.
     * @return the persisted entity.
     */
    Event save(Event event);

    /**
     * Updates a event.
     *
     * @param event the entity to update.
     * @return the persisted entity.
     */
    Event update(Event event);

    /**
     * Partially updates a event.
     *
     * @param event the entity to update partially.
     * @return the persisted entity.
     */
    Optional<Event> partialUpdate(Event event);

    /**
     * Get all the events.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<Event> findAll(Pageable pageable);

    /**
     * Get all the events with eager load of many-to-many relationships.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<Event> findAllWithEagerRelationships(Pageable pageable);

    /**
     * Get the "id" event.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<Event> findOne(Long id);

    /**
     * Delete the "id" event.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);

    void addJoiner(@Valid AddJoinerRequest addJoinerRequest) throws PendingJoinerListFullException;

    void removeJoiner(Long eventId, Long joinerId);

    void approveJoiner(Long eventId, Long joinerId);

    void removeApprovedJoiner(Long eventId, Long joinerId);
}
