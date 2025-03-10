package com.hiking.dublindoga.web.rest;

import com.hiking.dublindoga.domain.Joiner;
import com.hiking.dublindoga.repository.JoinerRepository;
import com.hiking.dublindoga.service.JoinerService;
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
 * REST controller for managing {@link com.hiking.dublindoga.domain.Joiner}.
 */
@RestController
@RequestMapping("/api/joiners")
public class JoinerResource {

    private static final Logger LOG = LoggerFactory.getLogger(JoinerResource.class);

    private static final String ENTITY_NAME = "joiner";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final JoinerService joinerService;

    private final JoinerRepository joinerRepository;

    public JoinerResource(JoinerService joinerService, JoinerRepository joinerRepository) {
        this.joinerService = joinerService;
        this.joinerRepository = joinerRepository;
    }

    /**
     * {@code POST  /joiners} : Create a new joiner.
     *
     * @param joiner the joiner to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new joiner, or with status {@code 400 (Bad Request)} if the joiner has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<Joiner> createJoiner(@Valid @RequestBody Joiner joiner) throws URISyntaxException {
        LOG.debug("REST request to save Joiner : {}", joiner);
        if (joiner.getId() != null) {
            throw new BadRequestAlertException("A new joiner cannot already have an ID", ENTITY_NAME, "idexists");
        }
        joiner = joinerService.save(joiner);
        return ResponseEntity.created(new URI("/api/joiners/" + joiner.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, joiner.getId().toString()))
            .body(joiner);
    }

    /**
     * {@code PUT  /joiners/:id} : Updates an existing joiner.
     *
     * @param id the id of the joiner to save.
     * @param joiner the joiner to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated joiner,
     * or with status {@code 400 (Bad Request)} if the joiner is not valid,
     * or with status {@code 500 (Internal Server Error)} if the joiner couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<Joiner> updateJoiner(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody Joiner joiner
    ) throws URISyntaxException {
        LOG.debug("REST request to update Joiner : {}, {}", id, joiner);
        if (joiner.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, joiner.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!joinerRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        joiner = joinerService.update(joiner);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, joiner.getId().toString()))
            .body(joiner);
    }

    /**
     * {@code PATCH  /joiners/:id} : Partial updates given fields of an existing joiner, field will ignore if it is null
     *
     * @param id the id of the joiner to save.
     * @param joiner the joiner to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated joiner,
     * or with status {@code 400 (Bad Request)} if the joiner is not valid,
     * or with status {@code 404 (Not Found)} if the joiner is not found,
     * or with status {@code 500 (Internal Server Error)} if the joiner couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<Joiner> partialUpdateJoiner(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody Joiner joiner
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update Joiner partially : {}, {}", id, joiner);
        if (joiner.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, joiner.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!joinerRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<Joiner> result = joinerService.partialUpdate(joiner);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, joiner.getId().toString())
        );
    }

    /**
     * {@code GET  /joiners} : get all the joiners.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of joiners in body.
     */
    @GetMapping("")
    public ResponseEntity<List<Joiner>> getAllJoiners(@org.springdoc.core.annotations.ParameterObject Pageable pageable) {
        LOG.debug("REST request to get a page of Joiners");
        Page<Joiner> page = joinerService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /joiners/:id} : get the "id" joiner.
     *
     * @param id the id of the joiner to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the joiner, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Joiner> getJoiner(@PathVariable("id") Long id) {
        LOG.debug("REST request to get Joiner : {}", id);
        Optional<Joiner> joiner = joinerService.findOne(id);
        return ResponseUtil.wrapOrNotFound(joiner);
    }

    /**
     * {@code DELETE  /joiners/:id} : delete the "id" joiner.
     *
     * @param id the id of the joiner to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteJoiner(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete Joiner : {}", id);
        joinerService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }
}
