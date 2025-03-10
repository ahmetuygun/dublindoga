package com.hiking.dublindoga.repository;

import com.hiking.dublindoga.domain.Joiner;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Spring Data JPA repository for the Joiner entity.
 */
@SuppressWarnings("unused")
@Repository
public interface JoinerRepository extends JpaRepository<Joiner, Long> {

    Optional<Joiner> findByInternalUserId(Long userId);
}
