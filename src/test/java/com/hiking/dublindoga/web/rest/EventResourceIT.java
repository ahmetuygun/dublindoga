package com.hiking.dublindoga.web.rest;

import static com.hiking.dublindoga.domain.EventAsserts.*;
import static com.hiking.dublindoga.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hiking.dublindoga.IntegrationTest;
import com.hiking.dublindoga.domain.Event;
import com.hiking.dublindoga.domain.enumeration.Difficulty;
import com.hiking.dublindoga.repository.EventRepository;
import com.hiking.dublindoga.service.EventService;
import jakarta.persistence.EntityManager;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link EventResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class EventResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final String DEFAULT_LOCATION = "AAAAAAAAAA";
    private static final String UPDATED_LOCATION = "BBBBBBBBBB";

    private static final Instant DEFAULT_DATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_DATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Difficulty DEFAULT_DIFFICULTY = Difficulty.EASY;
    private static final Difficulty UPDATED_DIFFICULTY = Difficulty.MEDIUM;

    private static final byte[] DEFAULT_PHOTO_1 = TestUtil.createByteArray(1, "0");
    private static final byte[] UPDATED_PHOTO_1 = TestUtil.createByteArray(1, "1");
    private static final String DEFAULT_PHOTO_1_CONTENT_TYPE = "image/jpg";
    private static final String UPDATED_PHOTO_1_CONTENT_TYPE = "image/png";

    private static final byte[] DEFAULT_PHOTO_2 = TestUtil.createByteArray(1, "0");
    private static final byte[] UPDATED_PHOTO_2 = TestUtil.createByteArray(1, "1");
    private static final String DEFAULT_PHOTO_2_CONTENT_TYPE = "image/jpg";
    private static final String UPDATED_PHOTO_2_CONTENT_TYPE = "image/png";

    private static final byte[] DEFAULT_PHOTO_3 = TestUtil.createByteArray(1, "0");
    private static final byte[] UPDATED_PHOTO_3 = TestUtil.createByteArray(1, "1");
    private static final String DEFAULT_PHOTO_3_CONTENT_TYPE = "image/jpg";
    private static final String UPDATED_PHOTO_3_CONTENT_TYPE = "image/png";

    private static final Integer DEFAULT_LIMIT = 1;
    private static final Integer UPDATED_LIMIT = 2;

    private static final String ENTITY_API_URL = "/api/events";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private EventRepository eventRepository;

    @Mock
    private EventRepository eventRepositoryMock;

    @Mock
    private EventService eventServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restEventMockMvc;

    private Event event;

    private Event insertedEvent;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Event createEntity() {
        return new Event()
            .name(DEFAULT_NAME)
            .description(DEFAULT_DESCRIPTION)
            .location(DEFAULT_LOCATION)
            .date(DEFAULT_DATE)
            .difficulty(DEFAULT_DIFFICULTY)
            .photo1(DEFAULT_PHOTO_1)
            .photo1ContentType(DEFAULT_PHOTO_1_CONTENT_TYPE)
            .photo2(DEFAULT_PHOTO_2)
            .photo2ContentType(DEFAULT_PHOTO_2_CONTENT_TYPE)
            .photo3(DEFAULT_PHOTO_3)
            .photo3ContentType(DEFAULT_PHOTO_3_CONTENT_TYPE)
            .limit(DEFAULT_LIMIT);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Event createUpdatedEntity() {
        return new Event()
            .name(UPDATED_NAME)
            .description(UPDATED_DESCRIPTION)
            .location(UPDATED_LOCATION)
            .date(UPDATED_DATE)
            .difficulty(UPDATED_DIFFICULTY)
            .photo1(UPDATED_PHOTO_1)
            .photo1ContentType(UPDATED_PHOTO_1_CONTENT_TYPE)
            .photo2(UPDATED_PHOTO_2)
            .photo2ContentType(UPDATED_PHOTO_2_CONTENT_TYPE)
            .photo3(UPDATED_PHOTO_3)
            .photo3ContentType(UPDATED_PHOTO_3_CONTENT_TYPE)
            .limit(UPDATED_LIMIT);
    }

    @BeforeEach
    public void initTest() {
        event = createEntity();
    }

    @AfterEach
    public void cleanup() {
        if (insertedEvent != null) {
            eventRepository.delete(insertedEvent);
            insertedEvent = null;
        }
    }

    @Test
    @Transactional
    void createEvent() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Event
        var returnedEvent = om.readValue(
            restEventMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(event)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            Event.class
        );

        // Validate the Event in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        assertEventUpdatableFieldsEquals(returnedEvent, getPersistedEvent(returnedEvent));

        insertedEvent = returnedEvent;
    }

    @Test
    @Transactional
    void createEventWithExistingId() throws Exception {
        // Create the Event with an existing ID
        event.setId(1L);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restEventMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(event)))
            .andExpect(status().isBadRequest());

        // Validate the Event in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkNameIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        event.setName(null);

        // Create the Event, which fails.

        restEventMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(event)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkLocationIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        event.setLocation(null);

        // Create the Event, which fails.

        restEventMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(event)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkDateIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        event.setDate(null);

        // Create the Event, which fails.

        restEventMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(event)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkDifficultyIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        event.setDifficulty(null);

        // Create the Event, which fails.

        restEventMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(event)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllEvents() throws Exception {
        // Initialize the database
        insertedEvent = eventRepository.saveAndFlush(event);

        // Get all the eventList
        restEventMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(event.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].location").value(hasItem(DEFAULT_LOCATION)))
            .andExpect(jsonPath("$.[*].date").value(hasItem(DEFAULT_DATE.toString())))
            .andExpect(jsonPath("$.[*].difficulty").value(hasItem(DEFAULT_DIFFICULTY.toString())))
            .andExpect(jsonPath("$.[*].photo1ContentType").value(hasItem(DEFAULT_PHOTO_1_CONTENT_TYPE)))
            .andExpect(jsonPath("$.[*].photo1").value(hasItem(Base64.getEncoder().encodeToString(DEFAULT_PHOTO_1))))
            .andExpect(jsonPath("$.[*].photo2ContentType").value(hasItem(DEFAULT_PHOTO_2_CONTENT_TYPE)))
            .andExpect(jsonPath("$.[*].photo2").value(hasItem(Base64.getEncoder().encodeToString(DEFAULT_PHOTO_2))))
            .andExpect(jsonPath("$.[*].photo3ContentType").value(hasItem(DEFAULT_PHOTO_3_CONTENT_TYPE)))
            .andExpect(jsonPath("$.[*].photo3").value(hasItem(Base64.getEncoder().encodeToString(DEFAULT_PHOTO_3))))
            .andExpect(jsonPath("$.[*].limit").value(hasItem(DEFAULT_LIMIT)));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllEventsWithEagerRelationshipsIsEnabled() throws Exception {
        when(eventServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restEventMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(eventServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllEventsWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(eventServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restEventMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(eventRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getEvent() throws Exception {
        // Initialize the database
        insertedEvent = eventRepository.saveAndFlush(event);

        // Get the event
        restEventMockMvc
            .perform(get(ENTITY_API_URL_ID, event.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(event.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION))
            .andExpect(jsonPath("$.location").value(DEFAULT_LOCATION))
            .andExpect(jsonPath("$.date").value(DEFAULT_DATE.toString()))
            .andExpect(jsonPath("$.difficulty").value(DEFAULT_DIFFICULTY.toString()))
            .andExpect(jsonPath("$.photo1ContentType").value(DEFAULT_PHOTO_1_CONTENT_TYPE))
            .andExpect(jsonPath("$.photo1").value(Base64.getEncoder().encodeToString(DEFAULT_PHOTO_1)))
            .andExpect(jsonPath("$.photo2ContentType").value(DEFAULT_PHOTO_2_CONTENT_TYPE))
            .andExpect(jsonPath("$.photo2").value(Base64.getEncoder().encodeToString(DEFAULT_PHOTO_2)))
            .andExpect(jsonPath("$.photo3ContentType").value(DEFAULT_PHOTO_3_CONTENT_TYPE))
            .andExpect(jsonPath("$.photo3").value(Base64.getEncoder().encodeToString(DEFAULT_PHOTO_3)))
            .andExpect(jsonPath("$.limit").value(DEFAULT_LIMIT));
    }

    @Test
    @Transactional
    void getNonExistingEvent() throws Exception {
        // Get the event
        restEventMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingEvent() throws Exception {
        // Initialize the database
        insertedEvent = eventRepository.saveAndFlush(event);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the event
        Event updatedEvent = eventRepository.findById(event.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedEvent are not directly saved in db
        em.detach(updatedEvent);
        updatedEvent
            .name(UPDATED_NAME)
            .description(UPDATED_DESCRIPTION)
            .location(UPDATED_LOCATION)
            .date(UPDATED_DATE)
            .difficulty(UPDATED_DIFFICULTY)
            .photo1(UPDATED_PHOTO_1)
            .photo1ContentType(UPDATED_PHOTO_1_CONTENT_TYPE)
            .photo2(UPDATED_PHOTO_2)
            .photo2ContentType(UPDATED_PHOTO_2_CONTENT_TYPE)
            .photo3(UPDATED_PHOTO_3)
            .photo3ContentType(UPDATED_PHOTO_3_CONTENT_TYPE)
            .limit(UPDATED_LIMIT);

        restEventMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedEvent.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(updatedEvent))
            )
            .andExpect(status().isOk());

        // Validate the Event in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedEventToMatchAllProperties(updatedEvent);
    }

    @Test
    @Transactional
    void putNonExistingEvent() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        event.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restEventMockMvc
            .perform(put(ENTITY_API_URL_ID, event.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(event)))
            .andExpect(status().isBadRequest());

        // Validate the Event in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchEvent() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        event.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restEventMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(event))
            )
            .andExpect(status().isBadRequest());

        // Validate the Event in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamEvent() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        event.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restEventMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(event)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Event in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateEventWithPatch() throws Exception {
        // Initialize the database
        insertedEvent = eventRepository.saveAndFlush(event);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the event using partial update
        Event partialUpdatedEvent = new Event();
        partialUpdatedEvent.setId(event.getId());

        partialUpdatedEvent
            .location(UPDATED_LOCATION)
            .photo1(UPDATED_PHOTO_1)
            .photo1ContentType(UPDATED_PHOTO_1_CONTENT_TYPE)
            .limit(UPDATED_LIMIT);

        restEventMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedEvent.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedEvent))
            )
            .andExpect(status().isOk());

        // Validate the Event in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertEventUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedEvent, event), getPersistedEvent(event));
    }

    @Test
    @Transactional
    void fullUpdateEventWithPatch() throws Exception {
        // Initialize the database
        insertedEvent = eventRepository.saveAndFlush(event);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the event using partial update
        Event partialUpdatedEvent = new Event();
        partialUpdatedEvent.setId(event.getId());

        partialUpdatedEvent
            .name(UPDATED_NAME)
            .description(UPDATED_DESCRIPTION)
            .location(UPDATED_LOCATION)
            .date(UPDATED_DATE)
            .difficulty(UPDATED_DIFFICULTY)
            .photo1(UPDATED_PHOTO_1)
            .photo1ContentType(UPDATED_PHOTO_1_CONTENT_TYPE)
            .photo2(UPDATED_PHOTO_2)
            .photo2ContentType(UPDATED_PHOTO_2_CONTENT_TYPE)
            .photo3(UPDATED_PHOTO_3)
            .photo3ContentType(UPDATED_PHOTO_3_CONTENT_TYPE)
            .limit(UPDATED_LIMIT);

        restEventMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedEvent.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedEvent))
            )
            .andExpect(status().isOk());

        // Validate the Event in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertEventUpdatableFieldsEquals(partialUpdatedEvent, getPersistedEvent(partialUpdatedEvent));
    }

    @Test
    @Transactional
    void patchNonExistingEvent() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        event.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restEventMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, event.getId()).contentType("application/merge-patch+json").content(om.writeValueAsBytes(event))
            )
            .andExpect(status().isBadRequest());

        // Validate the Event in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchEvent() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        event.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restEventMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(event))
            )
            .andExpect(status().isBadRequest());

        // Validate the Event in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamEvent() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        event.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restEventMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(event)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Event in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteEvent() throws Exception {
        // Initialize the database
        insertedEvent = eventRepository.saveAndFlush(event);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the event
        restEventMockMvc
            .perform(delete(ENTITY_API_URL_ID, event.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return eventRepository.count();
    }

    protected void assertIncrementedRepositoryCount(long countBefore) {
        assertThat(countBefore + 1).isEqualTo(getRepositoryCount());
    }

    protected void assertDecrementedRepositoryCount(long countBefore) {
        assertThat(countBefore - 1).isEqualTo(getRepositoryCount());
    }

    protected void assertSameRepositoryCount(long countBefore) {
        assertThat(countBefore).isEqualTo(getRepositoryCount());
    }

    protected Event getPersistedEvent(Event event) {
        return eventRepository.findById(event.getId()).orElseThrow();
    }

    protected void assertPersistedEventToMatchAllProperties(Event expectedEvent) {
        assertEventAllPropertiesEquals(expectedEvent, getPersistedEvent(expectedEvent));
    }

    protected void assertPersistedEventToMatchUpdatableProperties(Event expectedEvent) {
        assertEventAllUpdatablePropertiesEquals(expectedEvent, getPersistedEvent(expectedEvent));
    }
}
