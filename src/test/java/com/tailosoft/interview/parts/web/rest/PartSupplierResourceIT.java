package com.tailosoft.interview.parts.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.tailosoft.interview.parts.IntegrationTest;
import com.tailosoft.interview.parts.domain.Part;
import com.tailosoft.interview.parts.domain.PartSupplier;
import com.tailosoft.interview.parts.domain.PartSupplierId;
import com.tailosoft.interview.parts.domain.Supplier;
import com.tailosoft.interview.parts.repository.PartSupplierRepository;
import com.tailosoft.interview.parts.service.PartSupplierService;
import com.tailosoft.interview.parts.service.criteria.PartSupplierCriteria;
import com.tailosoft.interview.parts.service.dto.PartSupplierDTO;
import com.tailosoft.interview.parts.service.mapper.PartSupplierMapper;
import jakarta.persistence.EntityManager;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link PartSupplierResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class PartSupplierResourceIT {

    private static final Double DEFAULT_PRICE = 1D;
    private static final Double UPDATED_PRICE = 2D;
    private static final Double SMALLER_PRICE = 1D - 1D;

    private static final String ENTITY_API_URL = "/api/part-suppliers";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private PartSupplierRepository partSupplierRepository;

    @Mock
    private PartSupplierRepository partSupplierRepositoryMock;

    @Autowired
    private PartSupplierMapper partSupplierMapper;

    @Mock
    private PartSupplierService partSupplierServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restPartSupplierMockMvc;

    private PartSupplier partSupplier;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static PartSupplier createEntity(EntityManager em) {
        PartSupplier partSupplier = new PartSupplier().price(DEFAULT_PRICE);
        // Add required entity
        Part part;
        if (TestUtil.findAll(em, Part.class).isEmpty()) {
            part = PartResourceIT.createEntity(em);
            em.persist(part);
            em.flush();
        } else {
            part = TestUtil.findAll(em, Part.class).get(0);
        }
        partSupplier.setPart(part);
        // Add required entity
        Supplier supplier;
        if (TestUtil.findAll(em, Supplier.class).isEmpty()) {
            supplier = SupplierResourceIT.createEntity(em);
            em.persist(supplier);
            em.flush();
        } else {
            supplier = TestUtil.findAll(em, Supplier.class).get(0);
        }
        partSupplier.setSupplier(supplier);
        partSupplier.setId(new PartSupplierId(partSupplier.getPart().getId(), partSupplier.getSupplier().getId()));
        return partSupplier;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static PartSupplier createUpdatedEntity(EntityManager em) {
        PartSupplier partSupplier = new PartSupplier().price(UPDATED_PRICE);
        // Add required entity
        Part part;
        if (TestUtil.findAll(em, Part.class).isEmpty()) {
            part = PartResourceIT.createUpdatedEntity(em);
            em.persist(part);
            em.flush();
        } else {
            part = TestUtil.findAll(em, Part.class).get(0);
        }
        partSupplier.setPart(part);
        // Add required entity
        Supplier supplier;
        if (TestUtil.findAll(em, Supplier.class).isEmpty()) {
            supplier = SupplierResourceIT.createUpdatedEntity(em);
            em.persist(supplier);
            em.flush();
        } else {
            supplier = TestUtil.findAll(em, Supplier.class).get(0);
        }
        partSupplier.setSupplier(supplier);
        partSupplier.setId(new PartSupplierId(partSupplier.getPart().getId(), partSupplier.getSupplier().getId()));
        return partSupplier;
    }

    @BeforeEach
    public void initTest() {
        partSupplier = createEntity(em);
    }

    @Test
    @Transactional
    void createPartSupplier() throws Exception {
        int databaseSizeBeforeCreate = partSupplierRepository.findAll().size();
        // Create the PartSupplier
        PartSupplierDTO partSupplierDTO = partSupplierMapper.toDto(partSupplier);
        restPartSupplierMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(partSupplierDTO))
            )
            .andExpect(status().isCreated());

        // Validate the PartSupplier in the database
        List<PartSupplier> partSupplierList = partSupplierRepository.findAll();
        assertThat(partSupplierList).hasSize(databaseSizeBeforeCreate + 1);
        PartSupplier testPartSupplier = partSupplierList.get(partSupplierList.size() - 1);
        assertThat(testPartSupplier.getPrice()).isEqualTo(DEFAULT_PRICE);
    }

    @Test
    @Transactional
    void createPartSupplierWithExistingId() throws Exception {
        // Create the PartSupplier with an existing ID
        partSupplierRepository.saveAndFlush(partSupplier);
        PartSupplierDTO partSupplierDTO = partSupplierMapper.toDto(partSupplier);

        int databaseSizeBeforeCreate = partSupplierRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restPartSupplierMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(partSupplierDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the PartSupplier in the database
        List<PartSupplier> partSupplierList = partSupplierRepository.findAll();
        assertThat(partSupplierList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkPriceIsRequired() throws Exception {
        int databaseSizeBeforeTest = partSupplierRepository.findAll().size();
        // set the field null
        partSupplier.setPrice(null);

        // Create the PartSupplier, which fails.
        PartSupplierDTO partSupplierDTO = partSupplierMapper.toDto(partSupplier);

        restPartSupplierMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(partSupplierDTO))
            )
            .andExpect(status().isBadRequest());

        List<PartSupplier> partSupplierList = partSupplierRepository.findAll();
        assertThat(partSupplierList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllPartSuppliers() throws Exception {
        // Initialize the database
        partSupplierRepository.saveAndFlush(partSupplier);

        // Get all the partSupplierList
        restPartSupplierMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].price").value(hasItem(DEFAULT_PRICE.doubleValue())));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllPartSuppliersWithEagerRelationshipsIsEnabled() throws Exception {
        when(partSupplierServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restPartSupplierMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(partSupplierServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllPartSuppliersWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(partSupplierServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restPartSupplierMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(partSupplierRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getPartSupplier() throws Exception {
        // Initialize the database
        partSupplierRepository.saveAndFlush(partSupplier);

        // Get the partSupplier
        restPartSupplierMockMvc
            .perform(
                get(
                    ENTITY_API_URL_ID,
                    "partId=" + partSupplier.getId().getPartId() + ";" + "supplierId=" + partSupplier.getId().getSupplierId()
                )
            )
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.price").value(DEFAULT_PRICE.doubleValue()));
    }

    @Test
    @Transactional
    void getAllPartSuppliersByPriceIsEqualToSomething() throws Exception {
        // Initialize the database
        partSupplierRepository.saveAndFlush(partSupplier);

        // Get all the partSupplierList where price equals to DEFAULT_PRICE
        defaultPartSupplierShouldBeFound("price.equals=" + DEFAULT_PRICE);

        // Get all the partSupplierList where price equals to UPDATED_PRICE
        defaultPartSupplierShouldNotBeFound("price.equals=" + UPDATED_PRICE);
    }

    @Test
    @Transactional
    void getAllPartSuppliersByPriceIsInShouldWork() throws Exception {
        // Initialize the database
        partSupplierRepository.saveAndFlush(partSupplier);

        // Get all the partSupplierList where price in DEFAULT_PRICE or UPDATED_PRICE
        defaultPartSupplierShouldBeFound("price.in=" + DEFAULT_PRICE + "," + UPDATED_PRICE);

        // Get all the partSupplierList where price equals to UPDATED_PRICE
        defaultPartSupplierShouldNotBeFound("price.in=" + UPDATED_PRICE);
    }

    @Test
    @Transactional
    void getAllPartSuppliersByPriceIsNullOrNotNull() throws Exception {
        // Initialize the database
        partSupplierRepository.saveAndFlush(partSupplier);

        // Get all the partSupplierList where price is not null
        defaultPartSupplierShouldBeFound("price.specified=true");

        // Get all the partSupplierList where price is null
        defaultPartSupplierShouldNotBeFound("price.specified=false");
    }

    @Test
    @Transactional
    void getAllPartSuppliersByPriceIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        partSupplierRepository.saveAndFlush(partSupplier);

        // Get all the partSupplierList where price is greater than or equal to DEFAULT_PRICE
        defaultPartSupplierShouldBeFound("price.greaterThanOrEqual=" + DEFAULT_PRICE);

        // Get all the partSupplierList where price is greater than or equal to UPDATED_PRICE
        defaultPartSupplierShouldNotBeFound("price.greaterThanOrEqual=" + UPDATED_PRICE);
    }

    @Test
    @Transactional
    void getAllPartSuppliersByPriceIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        partSupplierRepository.saveAndFlush(partSupplier);

        // Get all the partSupplierList where price is less than or equal to DEFAULT_PRICE
        defaultPartSupplierShouldBeFound("price.lessThanOrEqual=" + DEFAULT_PRICE);

        // Get all the partSupplierList where price is less than or equal to SMALLER_PRICE
        defaultPartSupplierShouldNotBeFound("price.lessThanOrEqual=" + SMALLER_PRICE);
    }

    @Test
    @Transactional
    void getAllPartSuppliersByPriceIsLessThanSomething() throws Exception {
        // Initialize the database
        partSupplierRepository.saveAndFlush(partSupplier);

        // Get all the partSupplierList where price is less than DEFAULT_PRICE
        defaultPartSupplierShouldNotBeFound("price.lessThan=" + DEFAULT_PRICE);

        // Get all the partSupplierList where price is less than UPDATED_PRICE
        defaultPartSupplierShouldBeFound("price.lessThan=" + UPDATED_PRICE);
    }

    @Test
    @Transactional
    void getAllPartSuppliersByPriceIsGreaterThanSomething() throws Exception {
        // Initialize the database
        partSupplierRepository.saveAndFlush(partSupplier);

        // Get all the partSupplierList where price is greater than DEFAULT_PRICE
        defaultPartSupplierShouldNotBeFound("price.greaterThan=" + DEFAULT_PRICE);

        // Get all the partSupplierList where price is greater than SMALLER_PRICE
        defaultPartSupplierShouldBeFound("price.greaterThan=" + SMALLER_PRICE);
    }

    @Test
    @Transactional
    void getAllPartSuppliersByPartIsEqualToSomething() throws Exception {
        // Get already existing entity
        Part part = partSupplier.getPart();
        partSupplierRepository.saveAndFlush(partSupplier);
        // Get all the partSupplierList where partId equals to part.getId()
        defaultPartSupplierShouldBeFound("partId.equals=" + part.getId());

        // Get all the partSupplierList where partId equals to (part.getId() + 1)
        defaultPartSupplierShouldNotBeFound("partId.equals=" + (part.getId() + 1));
    }

    @Test
    @Transactional
    void getAllPartSuppliersBySupplierIsEqualToSomething() throws Exception {
        // Get already existing entity
        Supplier supplier = partSupplier.getSupplier();
        partSupplierRepository.saveAndFlush(partSupplier);
        // Get all the partSupplierList where supplierId equals to supplier.getId()
        defaultPartSupplierShouldBeFound("supplierId.equals=" + supplier.getId());

        // Get all the partSupplierList where supplierId equals to (supplier.getId() + 1)
        defaultPartSupplierShouldNotBeFound("supplierId.equals=" + (supplier.getId() + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultPartSupplierShouldBeFound(String filter) throws Exception {
        restPartSupplierMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].price").value(hasItem(DEFAULT_PRICE.doubleValue())));

        // Check, that the count call also returns 1
        restPartSupplierMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultPartSupplierShouldNotBeFound(String filter) throws Exception {
        restPartSupplierMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restPartSupplierMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingPartSupplier() throws Exception {
        // Get the partSupplier
        restPartSupplierMockMvc
            .perform(
                get(
                    ENTITY_API_URL_ID,
                    "partId=" + partSupplier.getId().getPartId() + ";" + "supplierId=" + partSupplier.getId().getSupplierId()
                )
            )
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingPartSupplier() throws Exception {
        // Initialize the database
        partSupplierRepository.saveAndFlush(partSupplier);

        int databaseSizeBeforeUpdate = partSupplierRepository.findAll().size();

        // Update the partSupplier
        PartSupplier updatedPartSupplier = partSupplierRepository.findById(partSupplier.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedPartSupplier are not directly saved in db
        em.detach(updatedPartSupplier);
        updatedPartSupplier.price(UPDATED_PRICE);
        PartSupplierDTO partSupplierDTO = partSupplierMapper.toDto(updatedPartSupplier);

        restPartSupplierMockMvc
            .perform(
                put(
                    ENTITY_API_URL_ID,
                    "partId=" + partSupplier.getId().getPartId() + ";" + "supplierId=" + partSupplier.getId().getSupplierId()
                )
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(partSupplierDTO))
            )
            .andExpect(status().isOk());

        // Validate the PartSupplier in the database
        List<PartSupplier> partSupplierList = partSupplierRepository.findAll();
        assertThat(partSupplierList).hasSize(databaseSizeBeforeUpdate);
        PartSupplier testPartSupplier = partSupplierList.get(partSupplierList.size() - 1);
        assertThat(testPartSupplier.getPrice()).isEqualTo(UPDATED_PRICE);
    }

    @Test
    @Transactional
    void putNonExistingPartSupplier() throws Exception {
        int databaseSizeBeforeUpdate = partSupplierRepository.findAll().size();
        partSupplier.setId(new PartSupplierId(count.incrementAndGet(), count.incrementAndGet()));

        // Create the PartSupplier
        PartSupplierDTO partSupplierDTO = partSupplierMapper.toDto(partSupplier);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restPartSupplierMockMvc
            .perform(
                put(
                    ENTITY_API_URL_ID,
                    "partId=" + partSupplier.getId().getPartId() + ";" + "supplierId=" + partSupplier.getId().getSupplierId()
                )
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(partSupplierDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the PartSupplier in the database
        List<PartSupplier> partSupplierList = partSupplierRepository.findAll();
        assertThat(partSupplierList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchPartSupplier() throws Exception {
        int databaseSizeBeforeUpdate = partSupplierRepository.findAll().size();
        partSupplier.setId(new PartSupplierId(count.incrementAndGet(), count.incrementAndGet()));

        // Create the PartSupplier
        PartSupplierDTO partSupplierDTO = partSupplierMapper.toDto(partSupplier);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPartSupplierMockMvc
            .perform(
                put(ENTITY_API_URL_ID, "partId=" + count.incrementAndGet() + ";" + "supplierId=" + count.incrementAndGet())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(partSupplierDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the PartSupplier in the database
        List<PartSupplier> partSupplierList = partSupplierRepository.findAll();
        assertThat(partSupplierList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamPartSupplier() throws Exception {
        int databaseSizeBeforeUpdate = partSupplierRepository.findAll().size();
        partSupplier.setId(new PartSupplierId(count.incrementAndGet(), count.incrementAndGet()));

        // Create the PartSupplier
        PartSupplierDTO partSupplierDTO = partSupplierMapper.toDto(partSupplier);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPartSupplierMockMvc
            .perform(
                put(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(partSupplierDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the PartSupplier in the database
        List<PartSupplier> partSupplierList = partSupplierRepository.findAll();
        assertThat(partSupplierList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdatePartSupplierWithPatch() throws Exception {
        // Initialize the database
        partSupplierRepository.saveAndFlush(partSupplier);

        int databaseSizeBeforeUpdate = partSupplierRepository.findAll().size();

        // Update the partSupplier using partial update
        PartSupplier partialUpdatedPartSupplier = new PartSupplier();
        partialUpdatedPartSupplier.setId(partSupplier.getId());
        partialUpdatedPartSupplier.setPart(partSupplier.getPart());
        partialUpdatedPartSupplier.setSupplier(partSupplier.getSupplier());

        partialUpdatedPartSupplier.price(UPDATED_PRICE);

        restPartSupplierMockMvc
            .perform(
                patch(
                    ENTITY_API_URL_ID,
                    "partId=" +
                    partialUpdatedPartSupplier.getId().getPartId() +
                    ";" +
                    "supplierId=" +
                    partialUpdatedPartSupplier.getId().getSupplierId()
                )
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedPartSupplier))
            )
            .andExpect(status().isOk());

        // Validate the PartSupplier in the database
        List<PartSupplier> partSupplierList = partSupplierRepository.findAll();
        assertThat(partSupplierList).hasSize(databaseSizeBeforeUpdate);
        PartSupplier testPartSupplier = partSupplierList.get(partSupplierList.size() - 1);
        assertThat(testPartSupplier.getPrice()).isEqualTo(UPDATED_PRICE);
    }

    @Test
    @Transactional
    void fullUpdatePartSupplierWithPatch() throws Exception {
        // Initialize the database
        partSupplierRepository.saveAndFlush(partSupplier);

        int databaseSizeBeforeUpdate = partSupplierRepository.findAll().size();

        // Update the partSupplier using partial update
        PartSupplier partialUpdatedPartSupplier = new PartSupplier();
        partialUpdatedPartSupplier.setId(partSupplier.getId());
        partialUpdatedPartSupplier.setPart(partSupplier.getPart());
        partialUpdatedPartSupplier.setSupplier(partSupplier.getSupplier());

        partialUpdatedPartSupplier.price(UPDATED_PRICE);

        restPartSupplierMockMvc
            .perform(
                patch(
                    ENTITY_API_URL_ID,
                    "partId=" +
                    partialUpdatedPartSupplier.getId().getPartId() +
                    ";" +
                    "supplierId=" +
                    partialUpdatedPartSupplier.getId().getSupplierId()
                )
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedPartSupplier))
            )
            .andExpect(status().isOk());

        // Validate the PartSupplier in the database
        List<PartSupplier> partSupplierList = partSupplierRepository.findAll();
        assertThat(partSupplierList).hasSize(databaseSizeBeforeUpdate);
        PartSupplier testPartSupplier = partSupplierList.get(partSupplierList.size() - 1);
        assertThat(testPartSupplier.getPrice()).isEqualTo(UPDATED_PRICE);
    }

    @Test
    @Transactional
    void patchNonExistingPartSupplier() throws Exception {
        int databaseSizeBeforeUpdate = partSupplierRepository.findAll().size();
        partSupplier.setId(new PartSupplierId(count.incrementAndGet(), count.incrementAndGet()));

        // Create the PartSupplier
        PartSupplierDTO partSupplierDTO = partSupplierMapper.toDto(partSupplier);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restPartSupplierMockMvc
            .perform(
                patch(
                    ENTITY_API_URL_ID,
                    "partId=" + partSupplier.getId().getPartId() + ";" + "supplierId=" + partSupplier.getId().getSupplierId()
                )
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partSupplierDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the PartSupplier in the database
        List<PartSupplier> partSupplierList = partSupplierRepository.findAll();
        assertThat(partSupplierList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchPartSupplier() throws Exception {
        int databaseSizeBeforeUpdate = partSupplierRepository.findAll().size();
        partSupplier.setId(new PartSupplierId(count.incrementAndGet(), count.incrementAndGet()));

        // Create the PartSupplier
        PartSupplierDTO partSupplierDTO = partSupplierMapper.toDto(partSupplier);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPartSupplierMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, "partId=" + count.incrementAndGet() + ";" + "supplierId=" + count.incrementAndGet())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partSupplierDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the PartSupplier in the database
        List<PartSupplier> partSupplierList = partSupplierRepository.findAll();
        assertThat(partSupplierList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamPartSupplier() throws Exception {
        int databaseSizeBeforeUpdate = partSupplierRepository.findAll().size();
        partSupplier.setId(new PartSupplierId(count.incrementAndGet(), count.incrementAndGet()));

        // Create the PartSupplier
        PartSupplierDTO partSupplierDTO = partSupplierMapper.toDto(partSupplier);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPartSupplierMockMvc
            .perform(
                patch(ENTITY_API_URL)
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partSupplierDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the PartSupplier in the database
        List<PartSupplier> partSupplierList = partSupplierRepository.findAll();
        assertThat(partSupplierList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deletePartSupplier() throws Exception {
        // Initialize the database
        partSupplierRepository.saveAndFlush(partSupplier);

        int databaseSizeBeforeDelete = partSupplierRepository.findAll().size();

        // Delete the partSupplier
        restPartSupplierMockMvc
            .perform(
                delete(
                    ENTITY_API_URL_ID,
                    "partId=" + partSupplier.getId().getPartId() + ";" + "supplierId=" + partSupplier.getId().getSupplierId()
                )
                    .with(csrf())
                    .accept(MediaType.APPLICATION_JSON)
            )
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<PartSupplier> partSupplierList = partSupplierRepository.findAll();
        assertThat(partSupplierList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
