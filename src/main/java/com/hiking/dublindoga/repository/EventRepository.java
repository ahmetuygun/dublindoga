package com.hiking.dublindoga.repository;

import com.hiking.dublindoga.domain.Event;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Event entity.
 *
 * When extending this class, extend EventRepositoryWithBagRelationships too.
 * For more information refer to https://github.com/jhipster/generator-jhipster/issues/17990.
 */
@Repository
public interface EventRepository extends EventRepositoryWithBagRelationships, JpaRepository<Event, Long> {
    default Optional<Event> findOneWithEagerRelationships(Long id) {
        return this.fetchBagRelationships(this.findById(id));
    }

    default List<Event> findAllWithEagerRelationships() {
        return this.fetchBagRelationships(this.findAll());
    }

    default Page<Event> findAllWithEagerRelationships(Pageable pageable) {
        return this.fetchBagRelationships(this.findAll(pageable));
    }

    @Query(
        value = "SELECT CASE WHEN COUNT(*) > 0 THEN TRUE ELSE FALSE END " +
            "FROM  rel_event__pending_joiner " +
            "WHERE event_id = :eventId AND pending_joiner_id  = :joinerId",
        nativeQuery = true
    )
    boolean existsPendingJoinerForEvent(@Param("eventId") Long eventId, @Param("joinerId") Long joinerId);

    @Query(
        value = "SELECT CASE WHEN COUNT(*) > 0 THEN TRUE ELSE FALSE END " +
            "FROM rel_event__approved_joiner " +
            "WHERE event_id = :eventId AND  approved_joiner_id = :joinerId",
        nativeQuery = true
    )
    boolean existsApprovedJoinerForEvent(@Param("eventId") Long eventId, @Param("joinerId") Long joinerId);
}
