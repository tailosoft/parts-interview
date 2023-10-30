package com.tailosoft.interview.parts.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.tailosoft.interview.parts.IntegrationTest;
import com.tailosoft.interview.parts.domain.Assembly;
import com.tailosoft.interview.parts.domain.AssemblyId;
import com.tailosoft.interview.parts.domain.Part;
import com.tailosoft.interview.parts.repository.AssemblyRepository;
import com.tailosoft.interview.parts.service.AssemblyService;
import com.tailosoft.interview.parts.service.criteria.AssemblyCriteria;
import com.tailosoft.interview.parts.service.dto.AssemblyDTO;
import com.tailosoft.interview.parts.service.mapper.AssemblyMapper;
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
 * Integration tests for the {@link AssemblyResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class AssemblyResourceIT {

    private static final Integer DEFAULT_QUANTITY = 1;
    private static final Integer UPDATED_QUANTITY = 2;
    private static final Integer SMALLER_QUANTITY = 1 - 1;

    private static final String ENTITY_API_URL = "/api/assemblies";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private AssemblyRepository assemblyRepository;

    @Mock
    private AssemblyRepository assemblyRepositoryMock;

    @Autowired
    private AssemblyMapper assemblyMapper;

    @Mock
    private AssemblyService assemblyServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restAssemblyMockMvc;

    private Assembly assembly;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Assembly createEntity(EntityManager em) {
        Assembly assembly = new Assembly().quantity(DEFAULT_QUANTITY);
        // Add required entity
        Part part;
        if (TestUtil.findAll(em, Part.class).isEmpty()) {
            part = PartResourceIT.createEntity(em);
            em.persist(part);
            em.flush();
        } else {
            part = TestUtil.findAll(em, Part.class).get(0);
        }
        assembly.setParent(part);
        // Add required entity
        assembly.setChild(part);
        assembly.setId(new AssemblyId(assembly.getParent().getId(), assembly.getChild().getId()));
        return assembly;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Assembly createUpdatedEntity(EntityManager em) {
        Assembly assembly = new Assembly().quantity(UPDATED_QUANTITY);
        // Add required entity
        Part part;
        if (TestUtil.findAll(em, Part.class).isEmpty()) {
            part = PartResourceIT.createUpdatedEntity(em);
            em.persist(part);
            em.flush();
        } else {
            part = TestUtil.findAll(em, Part.class).get(0);
        }
        assembly.setParent(part);
        // Add required entity
        assembly.setChild(part);
        assembly.setId(new AssemblyId(assembly.getParent().getId(), assembly.getChild().getId()));
        return assembly;
    }

    @BeforeEach
    public void initTest() {
        assembly = createEntity(em);
    }

    @Test
    @Transactional
    void createAssembly() throws Exception {
        int databaseSizeBeforeCreate = assemblyRepository.findAll().size();
        // Create the Assembly
        AssemblyDTO assemblyDTO = assemblyMapper.toDto(assembly);
        restAssemblyMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(assemblyDTO))
            )
            .andExpect(status().isCreated());

        // Validate the Assembly in the database
        List<Assembly> assemblyList = assemblyRepository.findAll();
        assertThat(assemblyList).hasSize(databaseSizeBeforeCreate + 1);
        Assembly testAssembly = assemblyList.get(assemblyList.size() - 1);
        assertThat(testAssembly.getQuantity()).isEqualTo(DEFAULT_QUANTITY);
    }

    @Test
    @Transactional
    void createAssemblyWithExistingId() throws Exception {
        // Create the Assembly with an existing ID
        assemblyRepository.saveAndFlush(assembly);
        AssemblyDTO assemblyDTO = assemblyMapper.toDto(assembly);

        int databaseSizeBeforeCreate = assemblyRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restAssemblyMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(assemblyDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Assembly in the database
        List<Assembly> assemblyList = assemblyRepository.findAll();
        assertThat(assemblyList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkQuantityIsRequired() throws Exception {
        int databaseSizeBeforeTest = assemblyRepository.findAll().size();
        // set the field null
        assembly.setQuantity(null);

        // Create the Assembly, which fails.
        AssemblyDTO assemblyDTO = assemblyMapper.toDto(assembly);

        restAssemblyMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(assemblyDTO))
            )
            .andExpect(status().isBadRequest());

        List<Assembly> assemblyList = assemblyRepository.findAll();
        assertThat(assemblyList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllAssemblies() throws Exception {
        // Initialize the database
        assemblyRepository.saveAndFlush(assembly);

        // Get all the assemblyList
        restAssemblyMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].quantity").value(hasItem(DEFAULT_QUANTITY)));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllAssembliesWithEagerRelationshipsIsEnabled() throws Exception {
        when(assemblyServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restAssemblyMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(assemblyServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllAssembliesWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(assemblyServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restAssemblyMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(assemblyRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getAssembly() throws Exception {
        // Initialize the database
        assemblyRepository.saveAndFlush(assembly);

        // Get the assembly
        restAssemblyMockMvc
            .perform(
                get(ENTITY_API_URL_ID, "parentId=" + assembly.getId().getParentId() + ";" + "childId=" + assembly.getId().getChildId())
            )
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.quantity").value(DEFAULT_QUANTITY));
    }

    @Test
    @Transactional
    void getAllAssembliesByQuantityIsEqualToSomething() throws Exception {
        // Initialize the database
        assemblyRepository.saveAndFlush(assembly);

        // Get all the assemblyList where quantity equals to DEFAULT_QUANTITY
        defaultAssemblyShouldBeFound("quantity.equals=" + DEFAULT_QUANTITY);

        // Get all the assemblyList where quantity equals to UPDATED_QUANTITY
        defaultAssemblyShouldNotBeFound("quantity.equals=" + UPDATED_QUANTITY);
    }

    @Test
    @Transactional
    void getAllAssembliesByQuantityIsInShouldWork() throws Exception {
        // Initialize the database
        assemblyRepository.saveAndFlush(assembly);

        // Get all the assemblyList where quantity in DEFAULT_QUANTITY or UPDATED_QUANTITY
        defaultAssemblyShouldBeFound("quantity.in=" + DEFAULT_QUANTITY + "," + UPDATED_QUANTITY);

        // Get all the assemblyList where quantity equals to UPDATED_QUANTITY
        defaultAssemblyShouldNotBeFound("quantity.in=" + UPDATED_QUANTITY);
    }

    @Test
    @Transactional
    void getAllAssembliesByQuantityIsNullOrNotNull() throws Exception {
        // Initialize the database
        assemblyRepository.saveAndFlush(assembly);

        // Get all the assemblyList where quantity is not null
        defaultAssemblyShouldBeFound("quantity.specified=true");

        // Get all the assemblyList where quantity is null
        defaultAssemblyShouldNotBeFound("quantity.specified=false");
    }

    @Test
    @Transactional
    void getAllAssembliesByQuantityIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        assemblyRepository.saveAndFlush(assembly);

        // Get all the assemblyList where quantity is greater than or equal to DEFAULT_QUANTITY
        defaultAssemblyShouldBeFound("quantity.greaterThanOrEqual=" + DEFAULT_QUANTITY);

        // Get all the assemblyList where quantity is greater than or equal to UPDATED_QUANTITY
        defaultAssemblyShouldNotBeFound("quantity.greaterThanOrEqual=" + UPDATED_QUANTITY);
    }

    @Test
    @Transactional
    void getAllAssembliesByQuantityIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        assemblyRepository.saveAndFlush(assembly);

        // Get all the assemblyList where quantity is less than or equal to DEFAULT_QUANTITY
        defaultAssemblyShouldBeFound("quantity.lessThanOrEqual=" + DEFAULT_QUANTITY);

        // Get all the assemblyList where quantity is less than or equal to SMALLER_QUANTITY
        defaultAssemblyShouldNotBeFound("quantity.lessThanOrEqual=" + SMALLER_QUANTITY);
    }

    @Test
    @Transactional
    void getAllAssembliesByQuantityIsLessThanSomething() throws Exception {
        // Initialize the database
        assemblyRepository.saveAndFlush(assembly);

        // Get all the assemblyList where quantity is less than DEFAULT_QUANTITY
        defaultAssemblyShouldNotBeFound("quantity.lessThan=" + DEFAULT_QUANTITY);

        // Get all the assemblyList where quantity is less than UPDATED_QUANTITY
        defaultAssemblyShouldBeFound("quantity.lessThan=" + UPDATED_QUANTITY);
    }

    @Test
    @Transactional
    void getAllAssembliesByQuantityIsGreaterThanSomething() throws Exception {
        // Initialize the database
        assemblyRepository.saveAndFlush(assembly);

        // Get all the assemblyList where quantity is greater than DEFAULT_QUANTITY
        defaultAssemblyShouldNotBeFound("quantity.greaterThan=" + DEFAULT_QUANTITY);

        // Get all the assemblyList where quantity is greater than SMALLER_QUANTITY
        defaultAssemblyShouldBeFound("quantity.greaterThan=" + SMALLER_QUANTITY);
    }

    @Test
    @Transactional
    void getAllAssembliesByParentIsEqualToSomething() throws Exception {
        // Get already existing entity
        Part parent = assembly.getParent();
        assemblyRepository.saveAndFlush(assembly);
        // Get all the assemblyList where parentId equals to parent.getId()
        defaultAssemblyShouldBeFound("parentId.equals=" + parent.getId());

        // Get all the assemblyList where parentId equals to (parent.getId() + 1)
        defaultAssemblyShouldNotBeFound("parentId.equals=" + (parent.getId() + 1));
    }

    @Test
    @Transactional
    void getAllAssembliesByChildIsEqualToSomething() throws Exception {
        // Get already existing entity
        Part child = assembly.getChild();
        assemblyRepository.saveAndFlush(assembly);
        // Get all the assemblyList where childId equals to child.getId()
        defaultAssemblyShouldBeFound("childId.equals=" + child.getId());

        // Get all the assemblyList where childId equals to (child.getId() + 1)
        defaultAssemblyShouldNotBeFound("childId.equals=" + (child.getId() + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultAssemblyShouldBeFound(String filter) throws Exception {
        restAssemblyMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].quantity").value(hasItem(DEFAULT_QUANTITY)));

        // Check, that the count call also returns 1
        restAssemblyMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultAssemblyShouldNotBeFound(String filter) throws Exception {
        restAssemblyMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restAssemblyMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingAssembly() throws Exception {
        // Get the assembly
        restAssemblyMockMvc
            .perform(
                get(ENTITY_API_URL_ID, "parentId=" + assembly.getId().getParentId() + ";" + "childId=" + assembly.getId().getChildId())
            )
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingAssembly() throws Exception {
        // Initialize the database
        assemblyRepository.saveAndFlush(assembly);

        int databaseSizeBeforeUpdate = assemblyRepository.findAll().size();

        // Update the assembly
        Assembly updatedAssembly = assemblyRepository.findById(assembly.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedAssembly are not directly saved in db
        em.detach(updatedAssembly);
        updatedAssembly.quantity(UPDATED_QUANTITY);
        AssemblyDTO assemblyDTO = assemblyMapper.toDto(updatedAssembly);

        restAssemblyMockMvc
            .perform(
                put(ENTITY_API_URL_ID, "parentId=" + assembly.getId().getParentId() + ";" + "childId=" + assembly.getId().getChildId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(assemblyDTO))
            )
            .andExpect(status().isOk());

        // Validate the Assembly in the database
        List<Assembly> assemblyList = assemblyRepository.findAll();
        assertThat(assemblyList).hasSize(databaseSizeBeforeUpdate);
        Assembly testAssembly = assemblyList.get(assemblyList.size() - 1);
        assertThat(testAssembly.getQuantity()).isEqualTo(UPDATED_QUANTITY);
    }

    @Test
    @Transactional
    void putNonExistingAssembly() throws Exception {
        int databaseSizeBeforeUpdate = assemblyRepository.findAll().size();
        assembly.setId(new AssemblyId(count.incrementAndGet(), count.incrementAndGet()));

        // Create the Assembly
        AssemblyDTO assemblyDTO = assemblyMapper.toDto(assembly);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restAssemblyMockMvc
            .perform(
                put(ENTITY_API_URL_ID, "parentId=" + assembly.getId().getParentId() + ";" + "childId=" + assembly.getId().getChildId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(assemblyDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Assembly in the database
        List<Assembly> assemblyList = assemblyRepository.findAll();
        assertThat(assemblyList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchAssembly() throws Exception {
        int databaseSizeBeforeUpdate = assemblyRepository.findAll().size();
        assembly.setId(new AssemblyId(count.incrementAndGet(), count.incrementAndGet()));

        // Create the Assembly
        AssemblyDTO assemblyDTO = assemblyMapper.toDto(assembly);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAssemblyMockMvc
            .perform(
                put(ENTITY_API_URL_ID, "parentId=" + count.incrementAndGet() + ";" + "childId=" + count.incrementAndGet())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(assemblyDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Assembly in the database
        List<Assembly> assemblyList = assemblyRepository.findAll();
        assertThat(assemblyList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamAssembly() throws Exception {
        int databaseSizeBeforeUpdate = assemblyRepository.findAll().size();
        assembly.setId(new AssemblyId(count.incrementAndGet(), count.incrementAndGet()));

        // Create the Assembly
        AssemblyDTO assemblyDTO = assemblyMapper.toDto(assembly);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAssemblyMockMvc
            .perform(
                put(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(assemblyDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Assembly in the database
        List<Assembly> assemblyList = assemblyRepository.findAll();
        assertThat(assemblyList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateAssemblyWithPatch() throws Exception {
        // Initialize the database
        assemblyRepository.saveAndFlush(assembly);

        int databaseSizeBeforeUpdate = assemblyRepository.findAll().size();

        // Update the assembly using partial update
        Assembly partialUpdatedAssembly = new Assembly();
        partialUpdatedAssembly.setId(assembly.getId());
        partialUpdatedAssembly.setParent(assembly.getParent());
        partialUpdatedAssembly.setChild(assembly.getChild());

        restAssemblyMockMvc
            .perform(
                patch(
                    ENTITY_API_URL_ID,
                    "parentId=" +
                    partialUpdatedAssembly.getId().getParentId() +
                    ";" +
                    "childId=" +
                    partialUpdatedAssembly.getId().getChildId()
                )
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedAssembly))
            )
            .andExpect(status().isOk());

        // Validate the Assembly in the database
        List<Assembly> assemblyList = assemblyRepository.findAll();
        assertThat(assemblyList).hasSize(databaseSizeBeforeUpdate);
        Assembly testAssembly = assemblyList.get(assemblyList.size() - 1);
        assertThat(testAssembly.getQuantity()).isEqualTo(DEFAULT_QUANTITY);
    }

    @Test
    @Transactional
    void fullUpdateAssemblyWithPatch() throws Exception {
        // Initialize the database
        assemblyRepository.saveAndFlush(assembly);

        int databaseSizeBeforeUpdate = assemblyRepository.findAll().size();

        // Update the assembly using partial update
        Assembly partialUpdatedAssembly = new Assembly();
        partialUpdatedAssembly.setId(assembly.getId());
        partialUpdatedAssembly.setParent(assembly.getParent());
        partialUpdatedAssembly.setChild(assembly.getChild());

        partialUpdatedAssembly.quantity(UPDATED_QUANTITY);

        restAssemblyMockMvc
            .perform(
                patch(
                    ENTITY_API_URL_ID,
                    "parentId=" +
                    partialUpdatedAssembly.getId().getParentId() +
                    ";" +
                    "childId=" +
                    partialUpdatedAssembly.getId().getChildId()
                )
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedAssembly))
            )
            .andExpect(status().isOk());

        // Validate the Assembly in the database
        List<Assembly> assemblyList = assemblyRepository.findAll();
        assertThat(assemblyList).hasSize(databaseSizeBeforeUpdate);
        Assembly testAssembly = assemblyList.get(assemblyList.size() - 1);
        assertThat(testAssembly.getQuantity()).isEqualTo(UPDATED_QUANTITY);
    }

    @Test
    @Transactional
    void patchNonExistingAssembly() throws Exception {
        int databaseSizeBeforeUpdate = assemblyRepository.findAll().size();
        assembly.setId(new AssemblyId(count.incrementAndGet(), count.incrementAndGet()));

        // Create the Assembly
        AssemblyDTO assemblyDTO = assemblyMapper.toDto(assembly);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restAssemblyMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, "parentId=" + assembly.getId().getParentId() + ";" + "childId=" + assembly.getId().getChildId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(assemblyDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Assembly in the database
        List<Assembly> assemblyList = assemblyRepository.findAll();
        assertThat(assemblyList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchAssembly() throws Exception {
        int databaseSizeBeforeUpdate = assemblyRepository.findAll().size();
        assembly.setId(new AssemblyId(count.incrementAndGet(), count.incrementAndGet()));

        // Create the Assembly
        AssemblyDTO assemblyDTO = assemblyMapper.toDto(assembly);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAssemblyMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, "parentId=" + count.incrementAndGet() + ";" + "childId=" + count.incrementAndGet())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(assemblyDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Assembly in the database
        List<Assembly> assemblyList = assemblyRepository.findAll();
        assertThat(assemblyList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamAssembly() throws Exception {
        int databaseSizeBeforeUpdate = assemblyRepository.findAll().size();
        assembly.setId(new AssemblyId(count.incrementAndGet(), count.incrementAndGet()));

        // Create the Assembly
        AssemblyDTO assemblyDTO = assemblyMapper.toDto(assembly);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAssemblyMockMvc
            .perform(
                patch(ENTITY_API_URL)
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(assemblyDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Assembly in the database
        List<Assembly> assemblyList = assemblyRepository.findAll();
        assertThat(assemblyList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteAssembly() throws Exception {
        // Initialize the database
        assemblyRepository.saveAndFlush(assembly);

        int databaseSizeBeforeDelete = assemblyRepository.findAll().size();

        // Delete the assembly
        restAssemblyMockMvc
            .perform(
                delete(ENTITY_API_URL_ID, "parentId=" + assembly.getId().getParentId() + ";" + "childId=" + assembly.getId().getChildId())
                    .with(csrf())
                    .accept(MediaType.APPLICATION_JSON)
            )
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Assembly> assemblyList = assemblyRepository.findAll();
        assertThat(assemblyList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
