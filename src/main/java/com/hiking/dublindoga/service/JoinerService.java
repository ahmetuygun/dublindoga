package com.hiking.dublindoga.service;

import com.hiking.dublindoga.domain.Joiner;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing {@link com.hiking.dublindoga.domain.Joiner}.
 */
public interface JoinerService {
    /**
     * Save a joiner.
     *
     * @param joiner the entity to save.
     * @return the persisted entity.
     */
    Joiner save(Joiner joiner);

    /**
     * Updates a joiner.
     *
     * @param joiner the entity to update.
     * @return the persisted entity.
     */
    Joiner update(Joiner joiner);

    /**
     * Partially updates a joiner.
     *
     * @param joiner the entity to update partially.
     * @return the persisted entity.
     */
    Optional<Joiner> partialUpdate(Joiner joiner);

    /**
     * Get all the joiners.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<Joiner> findAll(Pageable pageable);

    /**
     * Get the "id" joiner.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<Joiner> findOne(Long id);

    /**
     * Delete the "id" joiner.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
