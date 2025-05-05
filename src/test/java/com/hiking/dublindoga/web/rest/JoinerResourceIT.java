package com.hiking.dublindoga.web.rest;

import static com.hiking.dublindoga.domain.JoinerAsserts.*;
import static com.hiking.dublindoga.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hiking.dublindoga.IntegrationTest;
import com.hiking.dublindoga.domain.Joiner;
import com.hiking.dublindoga.domain.enumeration.Gender;
import com.hiking.dublindoga.domain.enumeration.JoinStatus;
import com.hiking.dublindoga.repository.JoinerRepository;
import com.hiking.dublindoga.repository.UserRepository;
import jakarta.persistence.EntityManager;
import java.util.Base64;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link JoinerResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class JoinerResourceIT {

    private static final String DEFAULT_FULL_NAME = "AAAAAAAAAA";
    private static final String UPDATED_FULL_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_EMAIL = "AAAAAAAAAA";
    private static final String UPDATED_EMAIL = "BBBBBBBBBB";

    private static final String DEFAULT_PHONE = "AAAAAAAAAA";
    private static final String UPDATED_PHONE = "BBBBBBBBBB";

    private static final JoinStatus DEFAULT_STATUS = JoinStatus.PENDING;
    private static final JoinStatus UPDATED_STATUS = JoinStatus.CONFIRMED;

    private static final byte[] DEFAULT_PHOTO_1 = TestUtil.createByteArray(1, "0");
    private static final byte[] UPDATED_PHOTO_1 = TestUtil.createByteArray(1, "1");
    private static final String DEFAULT_PHOTO_1_CONTENT_TYPE = "image/jpg";
    private static final String UPDATED_PHOTO_1_CONTENT_TYPE = "image/png";

    private static final Gender DEFAULT_GENDER = Gender.MAN;
    private static final Gender UPDATED_GENDER = Gender.WOMAN;

    private static final Integer DEFAULT_POINT = 1;
    private static final Integer UPDATED_POINT = 2;

    private static final String ENTITY_API_URL = "/api/joiners";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private JoinerRepository joinerRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restJoinerMockMvc;

    private Joiner joiner;

    private Joiner insertedJoiner;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Joiner createEntity() {
        return new Joiner()
            .fullName(DEFAULT_FULL_NAME)
            .email(DEFAULT_EMAIL)
            .phone(DEFAULT_PHONE)
            .status(DEFAULT_STATUS)
            .gender(DEFAULT_GENDER)
            .point(DEFAULT_POINT);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Joiner createUpdatedEntity() {
        return new Joiner()
            .fullName(UPDATED_FULL_NAME)
            .email(UPDATED_EMAIL)
            .phone(UPDATED_PHONE)
            .status(UPDATED_STATUS)
            .gender(UPDATED_GENDER)
            .point(UPDATED_POINT);
    }

    @BeforeEach
    public void initTest() {
        joiner = createEntity();
    }

    @AfterEach
    public void cleanup() {
        if (insertedJoiner != null) {
            joinerRepository.delete(insertedJoiner);
            insertedJoiner = null;
        }
    }

    @Test
    @Transactional
    void createJoiner() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Joiner
        var returnedJoiner = om.readValue(
            restJoinerMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(joiner)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            Joiner.class
        );

        // Validate the Joiner in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        assertJoinerUpdatableFieldsEquals(returnedJoiner, getPersistedJoiner(returnedJoiner));

        insertedJoiner = returnedJoiner;
    }

    @Test
    @Transactional
    void createJoinerWithExistingId() throws Exception {
        // Create the Joiner with an existing ID
        joiner.setId(1L);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restJoinerMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(joiner)))
            .andExpect(status().isBadRequest());

        // Validate the Joiner in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkFullNameIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        joiner.setFullName(null);

        // Create the Joiner, which fails.

        restJoinerMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(joiner)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkEmailIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        joiner.setEmail(null);

        // Create the Joiner, which fails.

        restJoinerMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(joiner)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkStatusIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        joiner.setStatus(null);

        // Create the Joiner, which fails.

        restJoinerMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(joiner)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllJoiners() throws Exception {
        // Initialize the database
        insertedJoiner = joinerRepository.saveAndFlush(joiner);

        // Get all the joinerList
        restJoinerMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(joiner.getId().intValue())))
            .andExpect(jsonPath("$.[*].fullName").value(hasItem(DEFAULT_FULL_NAME)))
            .andExpect(jsonPath("$.[*].email").value(hasItem(DEFAULT_EMAIL)))
            .andExpect(jsonPath("$.[*].phone").value(hasItem(DEFAULT_PHONE)))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())))
            .andExpect(jsonPath("$.[*].photo1ContentType").value(hasItem(DEFAULT_PHOTO_1_CONTENT_TYPE)))
            .andExpect(jsonPath("$.[*].photo1").value(hasItem(Base64.getEncoder().encodeToString(DEFAULT_PHOTO_1))))
            .andExpect(jsonPath("$.[*].gender").value(hasItem(DEFAULT_GENDER.toString())))
            .andExpect(jsonPath("$.[*].point").value(hasItem(DEFAULT_POINT)));
    }

    @Test
    @Transactional
    void getJoiner() throws Exception {
        // Initialize the database
        insertedJoiner = joinerRepository.saveAndFlush(joiner);

        // Get the joiner
        restJoinerMockMvc
            .perform(get(ENTITY_API_URL_ID, joiner.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(joiner.getId().intValue()))
            .andExpect(jsonPath("$.fullName").value(DEFAULT_FULL_NAME))
            .andExpect(jsonPath("$.email").value(DEFAULT_EMAIL))
            .andExpect(jsonPath("$.phone").value(DEFAULT_PHONE))
            .andExpect(jsonPath("$.status").value(DEFAULT_STATUS.toString()))
            .andExpect(jsonPath("$.photo1ContentType").value(DEFAULT_PHOTO_1_CONTENT_TYPE))
            .andExpect(jsonPath("$.photo1").value(Base64.getEncoder().encodeToString(DEFAULT_PHOTO_1)))
            .andExpect(jsonPath("$.gender").value(DEFAULT_GENDER.toString()))
            .andExpect(jsonPath("$.point").value(DEFAULT_POINT));
    }

    @Test
    @Transactional
    void getNonExistingJoiner() throws Exception {
        // Get the joiner
        restJoinerMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingJoiner() throws Exception {
        // Initialize the database
        insertedJoiner = joinerRepository.saveAndFlush(joiner);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the joiner
        Joiner updatedJoiner = joinerRepository.findById(joiner.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedJoiner are not directly saved in db
        em.detach(updatedJoiner);
        updatedJoiner
            .fullName(UPDATED_FULL_NAME)
            .email(UPDATED_EMAIL)
            .phone(UPDATED_PHONE)
            .status(UPDATED_STATUS)
            .gender(UPDATED_GENDER)
            .point(UPDATED_POINT);

        restJoinerMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedJoiner.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(updatedJoiner))
            )
            .andExpect(status().isOk());

        // Validate the Joiner in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedJoinerToMatchAllProperties(updatedJoiner);
    }

    @Test
    @Transactional
    void putNonExistingJoiner() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        joiner.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restJoinerMockMvc
            .perform(put(ENTITY_API_URL_ID, joiner.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(joiner)))
            .andExpect(status().isBadRequest());

        // Validate the Joiner in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchJoiner() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        joiner.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restJoinerMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(joiner))
            )
            .andExpect(status().isBadRequest());

        // Validate the Joiner in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamJoiner() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        joiner.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restJoinerMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(joiner)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Joiner in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateJoinerWithPatch() throws Exception {
        // Initialize the database
        insertedJoiner = joinerRepository.saveAndFlush(joiner);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the joiner using partial update
        Joiner partialUpdatedJoiner = new Joiner();
        partialUpdatedJoiner.setId(joiner.getId());

        partialUpdatedJoiner
            .email(UPDATED_EMAIL)
            .phone(UPDATED_PHONE)
            .gender(UPDATED_GENDER)
            .point(UPDATED_POINT);

        restJoinerMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedJoiner.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedJoiner))
            )
            .andExpect(status().isOk());

        // Validate the Joiner in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertJoinerUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedJoiner, joiner), getPersistedJoiner(joiner));
    }

    @Test
    @Transactional
    void fullUpdateJoinerWithPatch() throws Exception {
        // Initialize the database
        insertedJoiner = joinerRepository.saveAndFlush(joiner);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the joiner using partial update
        Joiner partialUpdatedJoiner = new Joiner();
        partialUpdatedJoiner.setId(joiner.getId());

        partialUpdatedJoiner
            .fullName(UPDATED_FULL_NAME)
            .email(UPDATED_EMAIL)
            .phone(UPDATED_PHONE)
            .status(UPDATED_STATUS)
            .gender(UPDATED_GENDER)
            .point(UPDATED_POINT);

        restJoinerMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedJoiner.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedJoiner))
            )
            .andExpect(status().isOk());

        // Validate the Joiner in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertJoinerUpdatableFieldsEquals(partialUpdatedJoiner, getPersistedJoiner(partialUpdatedJoiner));
    }

    @Test
    @Transactional
    void patchNonExistingJoiner() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        joiner.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restJoinerMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, joiner.getId()).contentType("application/merge-patch+json").content(om.writeValueAsBytes(joiner))
            )
            .andExpect(status().isBadRequest());

        // Validate the Joiner in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchJoiner() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        joiner.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restJoinerMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(joiner))
            )
            .andExpect(status().isBadRequest());

        // Validate the Joiner in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamJoiner() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        joiner.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restJoinerMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(joiner)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Joiner in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteJoiner() throws Exception {
        // Initialize the database
        insertedJoiner = joinerRepository.saveAndFlush(joiner);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the joiner
        restJoinerMockMvc
            .perform(delete(ENTITY_API_URL_ID, joiner.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return joinerRepository.count();
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

    protected Joiner getPersistedJoiner(Joiner joiner) {
        return joinerRepository.findById(joiner.getId()).orElseThrow();
    }

    protected void assertPersistedJoinerToMatchAllProperties(Joiner expectedJoiner) {
        assertJoinerAllPropertiesEquals(expectedJoiner, getPersistedJoiner(expectedJoiner));
    }

    protected void assertPersistedJoinerToMatchUpdatableProperties(Joiner expectedJoiner) {
        assertJoinerAllUpdatablePropertiesEquals(expectedJoiner, getPersistedJoiner(expectedJoiner));
    }
}
