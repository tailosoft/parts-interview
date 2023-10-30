package com.tailosoft.interview.parts.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.tailosoft.interview.parts.IntegrationTest;
import com.tailosoft.interview.parts.domain.Assembly;
import com.tailosoft.interview.parts.domain.Part;
import com.tailosoft.interview.parts.domain.PartSupplier;
import com.tailosoft.interview.parts.domain.Supplier;
import com.tailosoft.interview.parts.repository.PartRepository;
import com.tailosoft.interview.parts.service.PartService;
import com.tailosoft.interview.parts.service.criteria.PartCriteria;
import com.tailosoft.interview.parts.service.dto.PartDTO;
import com.tailosoft.interview.parts.service.mapper.PartMapper;
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
 * Integration tests for the {@link PartResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class PartResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final Double DEFAULT_ASSEMBLY_COST = 1D;
    private static final Double UPDATED_ASSEMBLY_COST = 2D;
    private static final Double SMALLER_ASSEMBLY_COST = 1D - 1D;

    private static final Double DEFAULT_BEST_PRICE = 1D;
    private static final Double UPDATED_BEST_PRICE = 2D;
    private static final Double SMALLER_BEST_PRICE = 1D - 1D;

    private static final String ENTITY_API_URL = "/api/parts";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private PartRepository partRepository;

    @Mock
    private PartRepository partRepositoryMock;

    @Autowired
    private PartMapper partMapper;

    @Mock
    private PartService partServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restPartMockMvc;

    private Part part;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Part createEntity(EntityManager em) {
        Part part = new Part().name(DEFAULT_NAME).assemblyCost(DEFAULT_ASSEMBLY_COST).bestPrice(DEFAULT_BEST_PRICE);
        return part;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Part createUpdatedEntity(EntityManager em) {
        Part part = new Part().name(UPDATED_NAME).assemblyCost(UPDATED_ASSEMBLY_COST).bestPrice(UPDATED_BEST_PRICE);
        return part;
    }

    @BeforeEach
    public void initTest() {
        part = createEntity(em);
    }

    @Test
    @Transactional
    void createPart() throws Exception {
        int databaseSizeBeforeCreate = partRepository.findAll().size();
        // Create the Part
        PartDTO partDTO = partMapper.toDto(part);
        restPartMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(partDTO))
            )
            .andExpect(status().isCreated());

        // Validate the Part in the database
        List<Part> partList = partRepository.findAll();
        assertThat(partList).hasSize(databaseSizeBeforeCreate + 1);
        Part testPart = partList.get(partList.size() - 1);
        assertThat(testPart.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testPart.getAssemblyCost()).isEqualTo(DEFAULT_ASSEMBLY_COST);
        assertThat(testPart.getBestPrice()).isEqualTo(DEFAULT_BEST_PRICE);
    }

    @Test
    @Transactional
    void createPartWithExistingId() throws Exception {
        // Create the Part with an existing ID
        part.setId(1L);
        PartDTO partDTO = partMapper.toDto(part);

        int databaseSizeBeforeCreate = partRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restPartMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(partDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Part in the database
        List<Part> partList = partRepository.findAll();
        assertThat(partList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = partRepository.findAll().size();
        // set the field null
        part.setName(null);

        // Create the Part, which fails.
        PartDTO partDTO = partMapper.toDto(part);

        restPartMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(partDTO))
            )
            .andExpect(status().isBadRequest());

        List<Part> partList = partRepository.findAll();
        assertThat(partList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllParts() throws Exception {
        // Initialize the database
        partRepository.saveAndFlush(part);

        // Get all the partList
        restPartMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(part.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].assemblyCost").value(hasItem(DEFAULT_ASSEMBLY_COST.doubleValue())))
            .andExpect(jsonPath("$.[*].bestPrice").value(hasItem(DEFAULT_BEST_PRICE.doubleValue())));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllPartsWithEagerRelationshipsIsEnabled() throws Exception {
        when(partServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restPartMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(partServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllPartsWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(partServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restPartMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(partRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getPart() throws Exception {
        // Initialize the database
        partRepository.saveAndFlush(part);

        // Get the part
        restPartMockMvc
            .perform(get(ENTITY_API_URL_ID, part.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(part.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.assemblyCost").value(DEFAULT_ASSEMBLY_COST.doubleValue()))
            .andExpect(jsonPath("$.bestPrice").value(DEFAULT_BEST_PRICE.doubleValue()));
    }

    @Test
    @Transactional
    void getPartsByIdFiltering() throws Exception {
        // Initialize the database
        partRepository.saveAndFlush(part);

        Long id = part.getId();

        defaultPartShouldBeFound("id.equals=" + id);
        defaultPartShouldNotBeFound("id.notEquals=" + id);

        defaultPartShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultPartShouldNotBeFound("id.greaterThan=" + id);

        defaultPartShouldBeFound("id.lessThanOrEqual=" + id);
        defaultPartShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllPartsByNameIsEqualToSomething() throws Exception {
        // Initialize the database
        partRepository.saveAndFlush(part);

        // Get all the partList where name equals to DEFAULT_NAME
        defaultPartShouldBeFound("name.equals=" + DEFAULT_NAME);

        // Get all the partList where name equals to UPDATED_NAME
        defaultPartShouldNotBeFound("name.equals=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllPartsByNameIsInShouldWork() throws Exception {
        // Initialize the database
        partRepository.saveAndFlush(part);

        // Get all the partList where name in DEFAULT_NAME or UPDATED_NAME
        defaultPartShouldBeFound("name.in=" + DEFAULT_NAME + "," + UPDATED_NAME);

        // Get all the partList where name equals to UPDATED_NAME
        defaultPartShouldNotBeFound("name.in=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllPartsByNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        partRepository.saveAndFlush(part);

        // Get all the partList where name is not null
        defaultPartShouldBeFound("name.specified=true");

        // Get all the partList where name is null
        defaultPartShouldNotBeFound("name.specified=false");
    }

    @Test
    @Transactional
    void getAllPartsByNameContainsSomething() throws Exception {
        // Initialize the database
        partRepository.saveAndFlush(part);

        // Get all the partList where name contains DEFAULT_NAME
        defaultPartShouldBeFound("name.contains=" + DEFAULT_NAME);

        // Get all the partList where name contains UPDATED_NAME
        defaultPartShouldNotBeFound("name.contains=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllPartsByNameNotContainsSomething() throws Exception {
        // Initialize the database
        partRepository.saveAndFlush(part);

        // Get all the partList where name does not contain DEFAULT_NAME
        defaultPartShouldNotBeFound("name.doesNotContain=" + DEFAULT_NAME);

        // Get all the partList where name does not contain UPDATED_NAME
        defaultPartShouldBeFound("name.doesNotContain=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllPartsByAssemblyCostIsEqualToSomething() throws Exception {
        // Initialize the database
        partRepository.saveAndFlush(part);

        // Get all the partList where assemblyCost equals to DEFAULT_ASSEMBLY_COST
        defaultPartShouldBeFound("assemblyCost.equals=" + DEFAULT_ASSEMBLY_COST);

        // Get all the partList where assemblyCost equals to UPDATED_ASSEMBLY_COST
        defaultPartShouldNotBeFound("assemblyCost.equals=" + UPDATED_ASSEMBLY_COST);
    }

    @Test
    @Transactional
    void getAllPartsByAssemblyCostIsInShouldWork() throws Exception {
        // Initialize the database
        partRepository.saveAndFlush(part);

        // Get all the partList where assemblyCost in DEFAULT_ASSEMBLY_COST or UPDATED_ASSEMBLY_COST
        defaultPartShouldBeFound("assemblyCost.in=" + DEFAULT_ASSEMBLY_COST + "," + UPDATED_ASSEMBLY_COST);

        // Get all the partList where assemblyCost equals to UPDATED_ASSEMBLY_COST
        defaultPartShouldNotBeFound("assemblyCost.in=" + UPDATED_ASSEMBLY_COST);
    }

    @Test
    @Transactional
    void getAllPartsByAssemblyCostIsNullOrNotNull() throws Exception {
        // Initialize the database
        partRepository.saveAndFlush(part);

        // Get all the partList where assemblyCost is not null
        defaultPartShouldBeFound("assemblyCost.specified=true");

        // Get all the partList where assemblyCost is null
        defaultPartShouldNotBeFound("assemblyCost.specified=false");
    }

    @Test
    @Transactional
    void getAllPartsByAssemblyCostIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        partRepository.saveAndFlush(part);

        // Get all the partList where assemblyCost is greater than or equal to DEFAULT_ASSEMBLY_COST
        defaultPartShouldBeFound("assemblyCost.greaterThanOrEqual=" + DEFAULT_ASSEMBLY_COST);

        // Get all the partList where assemblyCost is greater than or equal to UPDATED_ASSEMBLY_COST
        defaultPartShouldNotBeFound("assemblyCost.greaterThanOrEqual=" + UPDATED_ASSEMBLY_COST);
    }

    @Test
    @Transactional
    void getAllPartsByAssemblyCostIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        partRepository.saveAndFlush(part);

        // Get all the partList where assemblyCost is less than or equal to DEFAULT_ASSEMBLY_COST
        defaultPartShouldBeFound("assemblyCost.lessThanOrEqual=" + DEFAULT_ASSEMBLY_COST);

        // Get all the partList where assemblyCost is less than or equal to SMALLER_ASSEMBLY_COST
        defaultPartShouldNotBeFound("assemblyCost.lessThanOrEqual=" + SMALLER_ASSEMBLY_COST);
    }

    @Test
    @Transactional
    void getAllPartsByAssemblyCostIsLessThanSomething() throws Exception {
        // Initialize the database
        partRepository.saveAndFlush(part);

        // Get all the partList where assemblyCost is less than DEFAULT_ASSEMBLY_COST
        defaultPartShouldNotBeFound("assemblyCost.lessThan=" + DEFAULT_ASSEMBLY_COST);

        // Get all the partList where assemblyCost is less than UPDATED_ASSEMBLY_COST
        defaultPartShouldBeFound("assemblyCost.lessThan=" + UPDATED_ASSEMBLY_COST);
    }

    @Test
    @Transactional
    void getAllPartsByAssemblyCostIsGreaterThanSomething() throws Exception {
        // Initialize the database
        partRepository.saveAndFlush(part);

        // Get all the partList where assemblyCost is greater than DEFAULT_ASSEMBLY_COST
        defaultPartShouldNotBeFound("assemblyCost.greaterThan=" + DEFAULT_ASSEMBLY_COST);

        // Get all the partList where assemblyCost is greater than SMALLER_ASSEMBLY_COST
        defaultPartShouldBeFound("assemblyCost.greaterThan=" + SMALLER_ASSEMBLY_COST);
    }

    @Test
    @Transactional
    void getAllPartsByBestPriceIsEqualToSomething() throws Exception {
        // Initialize the database
        partRepository.saveAndFlush(part);

        // Get all the partList where bestPrice equals to DEFAULT_BEST_PRICE
        defaultPartShouldBeFound("bestPrice.equals=" + DEFAULT_BEST_PRICE);

        // Get all the partList where bestPrice equals to UPDATED_BEST_PRICE
        defaultPartShouldNotBeFound("bestPrice.equals=" + UPDATED_BEST_PRICE);
    }

    @Test
    @Transactional
    void getAllPartsByBestPriceIsInShouldWork() throws Exception {
        // Initialize the database
        partRepository.saveAndFlush(part);

        // Get all the partList where bestPrice in DEFAULT_BEST_PRICE or UPDATED_BEST_PRICE
        defaultPartShouldBeFound("bestPrice.in=" + DEFAULT_BEST_PRICE + "," + UPDATED_BEST_PRICE);

        // Get all the partList where bestPrice equals to UPDATED_BEST_PRICE
        defaultPartShouldNotBeFound("bestPrice.in=" + UPDATED_BEST_PRICE);
    }

    @Test
    @Transactional
    void getAllPartsByBestPriceIsNullOrNotNull() throws Exception {
        // Initialize the database
        partRepository.saveAndFlush(part);

        // Get all the partList where bestPrice is not null
        defaultPartShouldBeFound("bestPrice.specified=true");

        // Get all the partList where bestPrice is null
        defaultPartShouldNotBeFound("bestPrice.specified=false");
    }

    @Test
    @Transactional
    void getAllPartsByBestPriceIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        partRepository.saveAndFlush(part);

        // Get all the partList where bestPrice is greater than or equal to DEFAULT_BEST_PRICE
        defaultPartShouldBeFound("bestPrice.greaterThanOrEqual=" + DEFAULT_BEST_PRICE);

        // Get all the partList where bestPrice is greater than or equal to UPDATED_BEST_PRICE
        defaultPartShouldNotBeFound("bestPrice.greaterThanOrEqual=" + UPDATED_BEST_PRICE);
    }

    @Test
    @Transactional
    void getAllPartsByBestPriceIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        partRepository.saveAndFlush(part);

        // Get all the partList where bestPrice is less than or equal to DEFAULT_BEST_PRICE
        defaultPartShouldBeFound("bestPrice.lessThanOrEqual=" + DEFAULT_BEST_PRICE);

        // Get all the partList where bestPrice is less than or equal to SMALLER_BEST_PRICE
        defaultPartShouldNotBeFound("bestPrice.lessThanOrEqual=" + SMALLER_BEST_PRICE);
    }

    @Test
    @Transactional
    void getAllPartsByBestPriceIsLessThanSomething() throws Exception {
        // Initialize the database
        partRepository.saveAndFlush(part);

        // Get all the partList where bestPrice is less than DEFAULT_BEST_PRICE
        defaultPartShouldNotBeFound("bestPrice.lessThan=" + DEFAULT_BEST_PRICE);

        // Get all the partList where bestPrice is less than UPDATED_BEST_PRICE
        defaultPartShouldBeFound("bestPrice.lessThan=" + UPDATED_BEST_PRICE);
    }

    @Test
    @Transactional
    void getAllPartsByBestPriceIsGreaterThanSomething() throws Exception {
        // Initialize the database
        partRepository.saveAndFlush(part);

        // Get all the partList where bestPrice is greater than DEFAULT_BEST_PRICE
        defaultPartShouldNotBeFound("bestPrice.greaterThan=" + DEFAULT_BEST_PRICE);

        // Get all the partList where bestPrice is greater than SMALLER_BEST_PRICE
        defaultPartShouldBeFound("bestPrice.greaterThan=" + SMALLER_BEST_PRICE);
    }

    @Test
    @Transactional
    void getAllPartsByBestSupplierIsEqualToSomething() throws Exception {
        // Initialize the database

        em.persist(part);
        Supplier bestSupplier;
        if (TestUtil.findAll(em, Supplier.class).isEmpty()) {
            bestSupplier = SupplierResourceIT.createEntity(em);
        } else {
            bestSupplier = TestUtil.findAll(em, Supplier.class).get(0);
        }
        em.persist(bestSupplier);
        part.setBestSupplier(bestSupplier);
        partRepository.saveAndFlush(part);
        // Get all the partList where bestSupplierId equals to bestSupplier.getId()
        defaultPartShouldBeFound("bestSupplierId.equals=" + bestSupplier.getId());

        // Get all the partList where bestSupplierId equals to (bestSupplier.getId() + 1)
        defaultPartShouldNotBeFound("bestSupplierId.equals=" + (bestSupplier.getId() + 1));
    }

    @Test
    @Transactional
    void getAllPartsByChildIsEqualToSomething() throws Exception {
        // Initialize the database

        em.persist(part);
        Assembly child;
        if (TestUtil.findAll(em, Assembly.class).isEmpty()) {
            child = AssemblyResourceIT.createEntity(em);
        } else {
            child = TestUtil.findAll(em, Assembly.class).get(0);
        }
        em.persist(child);
        part.addChild(child);
        partRepository.saveAndFlush(part);
        // Get all the partList where childParentId equals to child.getId().getParentId()
        defaultPartShouldBeFound("childParentId.equals=" + child.getId().getParentId());

        // Get all the partList where childParentId equals to (child.getId().getParentId() + 1)
        defaultPartShouldNotBeFound("childParentId.equals=" + (child.getId().getParentId() + 1));
    }

    @Test
    @Transactional
    void getAllPartsByParentIsEqualToSomething() throws Exception {
        // Initialize the database

        em.persist(part);
        Assembly parent;
        if (TestUtil.findAll(em, Assembly.class).isEmpty()) {
            parent = AssemblyResourceIT.createEntity(em);
        } else {
            parent = TestUtil.findAll(em, Assembly.class).get(0);
        }
        em.persist(parent);
        part.addParent(parent);
        partRepository.saveAndFlush(part);
        // Get all the partList where parentParentId equals to parent.getId().getParentId()
        defaultPartShouldBeFound("parentParentId.equals=" + parent.getId().getParentId());

        // Get all the partList where parentParentId equals to (parent.getId().getParentId() + 1)
        defaultPartShouldNotBeFound("parentParentId.equals=" + (parent.getId().getParentId() + 1));
    }

    @Test
    @Transactional
    void getAllPartsByPartSupplierIsEqualToSomething() throws Exception {
        // Initialize the database

        em.persist(part);
        PartSupplier partSupplier;
        if (TestUtil.findAll(em, PartSupplier.class).isEmpty()) {
            partSupplier = PartSupplierResourceIT.createEntity(em);
        } else {
            partSupplier = TestUtil.findAll(em, PartSupplier.class).get(0);
        }
        em.persist(partSupplier);
        part.addPartSupplier(partSupplier);
        partRepository.saveAndFlush(part);
        // Get all the partList where partSupplierPartId equals to partSupplier.getId().getPartId()
        defaultPartShouldBeFound("partSupplierPartId.equals=" + partSupplier.getId().getPartId());

        // Get all the partList where partSupplierPartId equals to (partSupplier.getId().getPartId() + 1)
        defaultPartShouldNotBeFound("partSupplierPartId.equals=" + (partSupplier.getId().getPartId() + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultPartShouldBeFound(String filter) throws Exception {
        restPartMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(part.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].assemblyCost").value(hasItem(DEFAULT_ASSEMBLY_COST.doubleValue())))
            .andExpect(jsonPath("$.[*].bestPrice").value(hasItem(DEFAULT_BEST_PRICE.doubleValue())));

        // Check, that the count call also returns 1
        restPartMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultPartShouldNotBeFound(String filter) throws Exception {
        restPartMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restPartMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingPart() throws Exception {
        // Get the part
        restPartMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingPart() throws Exception {
        // Initialize the database
        partRepository.saveAndFlush(part);

        int databaseSizeBeforeUpdate = partRepository.findAll().size();

        // Update the part
        Part updatedPart = partRepository.findById(part.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedPart are not directly saved in db
        em.detach(updatedPart);
        updatedPart.name(UPDATED_NAME).assemblyCost(UPDATED_ASSEMBLY_COST).bestPrice(UPDATED_BEST_PRICE);
        PartDTO partDTO = partMapper.toDto(updatedPart);

        restPartMockMvc
            .perform(
                put(ENTITY_API_URL_ID, part.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(partDTO))
            )
            .andExpect(status().isOk());

        // Validate the Part in the database
        List<Part> partList = partRepository.findAll();
        assertThat(partList).hasSize(databaseSizeBeforeUpdate);
        Part testPart = partList.get(partList.size() - 1);
        assertThat(testPart.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testPart.getAssemblyCost()).isEqualTo(UPDATED_ASSEMBLY_COST);
        assertThat(testPart.getBestPrice()).isEqualTo(UPDATED_BEST_PRICE);
    }

    @Test
    @Transactional
    void putNonExistingPart() throws Exception {
        int databaseSizeBeforeUpdate = partRepository.findAll().size();
        part.setId(count.incrementAndGet());

        // Create the Part
        PartDTO partDTO = partMapper.toDto(part);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restPartMockMvc
            .perform(
                put(ENTITY_API_URL_ID, part.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(partDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Part in the database
        List<Part> partList = partRepository.findAll();
        assertThat(partList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchPart() throws Exception {
        int databaseSizeBeforeUpdate = partRepository.findAll().size();
        part.setId(count.incrementAndGet());

        // Create the Part
        PartDTO partDTO = partMapper.toDto(part);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPartMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(partDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Part in the database
        List<Part> partList = partRepository.findAll();
        assertThat(partList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamPart() throws Exception {
        int databaseSizeBeforeUpdate = partRepository.findAll().size();
        part.setId(count.incrementAndGet());

        // Create the Part
        PartDTO partDTO = partMapper.toDto(part);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPartMockMvc
            .perform(
                put(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(partDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Part in the database
        List<Part> partList = partRepository.findAll();
        assertThat(partList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdatePartWithPatch() throws Exception {
        // Initialize the database
        partRepository.saveAndFlush(part);

        int databaseSizeBeforeUpdate = partRepository.findAll().size();

        // Update the part using partial update
        Part partialUpdatedPart = new Part();
        partialUpdatedPart.setId(part.getId());

        partialUpdatedPart.name(UPDATED_NAME);

        restPartMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedPart.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedPart))
            )
            .andExpect(status().isOk());

        // Validate the Part in the database
        List<Part> partList = partRepository.findAll();
        assertThat(partList).hasSize(databaseSizeBeforeUpdate);
        Part testPart = partList.get(partList.size() - 1);
        assertThat(testPart.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testPart.getAssemblyCost()).isEqualTo(DEFAULT_ASSEMBLY_COST);
        assertThat(testPart.getBestPrice()).isEqualTo(DEFAULT_BEST_PRICE);
    }

    @Test
    @Transactional
    void fullUpdatePartWithPatch() throws Exception {
        // Initialize the database
        partRepository.saveAndFlush(part);

        int databaseSizeBeforeUpdate = partRepository.findAll().size();

        // Update the part using partial update
        Part partialUpdatedPart = new Part();
        partialUpdatedPart.setId(part.getId());

        partialUpdatedPart.name(UPDATED_NAME).assemblyCost(UPDATED_ASSEMBLY_COST).bestPrice(UPDATED_BEST_PRICE);

        restPartMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedPart.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedPart))
            )
            .andExpect(status().isOk());

        // Validate the Part in the database
        List<Part> partList = partRepository.findAll();
        assertThat(partList).hasSize(databaseSizeBeforeUpdate);
        Part testPart = partList.get(partList.size() - 1);
        assertThat(testPart.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testPart.getAssemblyCost()).isEqualTo(UPDATED_ASSEMBLY_COST);
        assertThat(testPart.getBestPrice()).isEqualTo(UPDATED_BEST_PRICE);
    }

    @Test
    @Transactional
    void patchNonExistingPart() throws Exception {
        int databaseSizeBeforeUpdate = partRepository.findAll().size();
        part.setId(count.incrementAndGet());

        // Create the Part
        PartDTO partDTO = partMapper.toDto(part);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restPartMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, part.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Part in the database
        List<Part> partList = partRepository.findAll();
        assertThat(partList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchPart() throws Exception {
        int databaseSizeBeforeUpdate = partRepository.findAll().size();
        part.setId(count.incrementAndGet());

        // Create the Part
        PartDTO partDTO = partMapper.toDto(part);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPartMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Part in the database
        List<Part> partList = partRepository.findAll();
        assertThat(partList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamPart() throws Exception {
        int databaseSizeBeforeUpdate = partRepository.findAll().size();
        part.setId(count.incrementAndGet());

        // Create the Part
        PartDTO partDTO = partMapper.toDto(part);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPartMockMvc
            .perform(
                patch(ENTITY_API_URL)
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Part in the database
        List<Part> partList = partRepository.findAll();
        assertThat(partList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deletePart() throws Exception {
        // Initialize the database
        partRepository.saveAndFlush(part);

        int databaseSizeBeforeDelete = partRepository.findAll().size();

        // Delete the part
        restPartMockMvc
            .perform(delete(ENTITY_API_URL_ID, part.getId()).with(csrf()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Part> partList = partRepository.findAll();
        assertThat(partList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
