package com.tailosoft.interview.parts.web.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tailosoft.interview.parts.domain.AssemblyId;
import com.tailosoft.interview.parts.repository.AssemblyRepository;
import com.tailosoft.interview.parts.service.AssemblyQueryService;
import com.tailosoft.interview.parts.service.AssemblyService;
import com.tailosoft.interview.parts.service.criteria.AssemblyCriteria;
import com.tailosoft.interview.parts.service.dto.AssemblyDTO;
import com.tailosoft.interview.parts.service.mapper.AssemblyMapper;
import com.tailosoft.interview.parts.web.rest.errors.BadRequestAlertException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;
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
 * REST controller for managing {@link com.tailosoft.interview.parts.domain.Assembly}.
 */
@RestController
@RequestMapping("/api")
public class AssemblyResource {

    private final Logger log = LoggerFactory.getLogger(AssemblyResource.class);

    private final ObjectMapper mapper = new ObjectMapper();

    private static final String ENTITY_NAME = "assembly";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final AssemblyService assemblyService;

    private final AssemblyRepository assemblyRepository;

    private final AssemblyMapper assemblyMapper;

    private final AssemblyQueryService assemblyQueryService;

    public AssemblyResource(
        AssemblyService assemblyService,
        AssemblyRepository assemblyRepository,
        AssemblyMapper assemblyMapper,
        AssemblyQueryService assemblyQueryService
    ) {
        this.assemblyService = assemblyService;
        this.assemblyRepository = assemblyRepository;
        this.assemblyMapper = assemblyMapper;
        this.assemblyQueryService = assemblyQueryService;
    }

    /**
     * {@code POST  /assemblies} : Create a new assembly.
     *
     * @param assemblyDTO the assemblyDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new assemblyDTO, or with status {@code 400 (Bad Request)} if the assembly has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/assemblies")
    public ResponseEntity<AssemblyDTO> createAssembly(@Valid @RequestBody AssemblyDTO assemblyDTO) throws URISyntaxException {
        log.debug("REST request to save Assembly : {}", assemblyDTO);
        AssemblyId id = assemblyMapper.toEntity(assemblyDTO).getId();
        if (id == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (assemblyService.findOne(id).isPresent()) {
            throw new BadRequestAlertException("This assembly already exists", ENTITY_NAME, "idduplicate");
        }
        AssemblyDTO result = assemblyService.save(assemblyDTO);
        return ResponseEntity
            .created(new URI("/api/assemblies/" + "parentId=" + result.getParent().getId() + ";" + "childId=" + result.getChild().getId()))
            .headers(
                HeaderUtil.createEntityCreationAlert(
                    applicationName,
                    true,
                    ENTITY_NAME,
                    "parentId=" + result.getParent().getId() + ";" + "childId=" + result.getChild().getId().toString()
                )
            )
            .body(result);
    }

    /**
     * {@code PUT  /assemblies/:id} : Updates an existing assembly.
     *
     * @param idMap a Map representation of the id of the assemblyDTO to save.
     * @param assemblyDTO the assemblyDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated assemblyDTO,
     * or with status {@code 400 (Bad Request)} if the assemblyDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the assemblyDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/assemblies/{id}")
    public ResponseEntity<AssemblyDTO> updateAssembly(
        @MatrixVariable(pathVar = "id") Map<String, String> idMap,
        @Valid @RequestBody AssemblyDTO assemblyDTO
    ) throws URISyntaxException {
        final AssemblyId id = mapper.convertValue(idMap, AssemblyId.class);
        log.debug("REST request to update Assembly : {}, {}", id, assemblyDTO);
        if (!Objects.equals(id, assemblyMapper.toEntity(assemblyDTO).getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!assemblyRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        AssemblyDTO result = assemblyService.update(assemblyDTO);
        return ResponseEntity
            .ok()
            .headers(
                HeaderUtil.createEntityUpdateAlert(
                    applicationName,
                    true,
                    ENTITY_NAME,
                    "parentId=" + assemblyDTO.getParent().getId() + ";" + "childId=" + assemblyDTO.getChild().getId().toString()
                )
            )
            .body(result);
    }

    /**
     * {@code PATCH  /assemblies/:id} : Partial updates given fields of an existing assembly, field will ignore if it is null
     *
     * @param idMap a Map representation of the id of the assemblyDTO to save.
     * @param assemblyDTO the assemblyDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated assemblyDTO,
     * or with status {@code 400 (Bad Request)} if the assemblyDTO is not valid,
     * or with status {@code 404 (Not Found)} if the assemblyDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the assemblyDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/assemblies/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<AssemblyDTO> partialUpdateAssembly(
        @MatrixVariable(pathVar = "id") Map<String, String> idMap,
        @NotNull @RequestBody AssemblyDTO assemblyDTO
    ) throws URISyntaxException {
        final AssemblyId id = mapper.convertValue(idMap, AssemblyId.class);
        log.debug("REST request to partial update Assembly partially : {}, {}", id, assemblyDTO);
        if (!Objects.equals(id, assemblyMapper.toEntity(assemblyDTO).getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!assemblyRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<AssemblyDTO> result = assemblyService.partialUpdate(assemblyDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(
                applicationName,
                true,
                ENTITY_NAME,
                "parentId=" + assemblyDTO.getParent().getId() + ";" + "childId=" + assemblyDTO.getChild().getId().toString()
            )
        );
    }

    /**
     * {@code GET  /assemblies} : get all the assemblies.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of assemblies in body.
     */
    @GetMapping("/assemblies")
    public ResponseEntity<List<AssemblyDTO>> getAllAssemblies(
        AssemblyCriteria criteria,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        log.debug("REST request to get Assemblies by criteria: {}", criteria);

        Page<AssemblyDTO> page = assemblyQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /assemblies/count} : count all the assemblies.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/assemblies/count")
    public ResponseEntity<Long> countAssemblies(AssemblyCriteria criteria) {
        log.debug("REST request to count Assemblies by criteria: {}", criteria);
        return ResponseEntity.ok().body(assemblyQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /assemblies/:id} : get the "id" assembly.
     *
     * @param idMap a Map representation of the id of the assemblyDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the assemblyDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/assemblies/{id}")
    public ResponseEntity<AssemblyDTO> getAssembly(@MatrixVariable(pathVar = "id") Map<String, String> idMap) {
        final AssemblyId id = mapper.convertValue(idMap, AssemblyId.class);
        log.debug("REST request to get Assembly : {}", id);
        Optional<AssemblyDTO> assemblyDTO = assemblyService.findOne(id);
        return ResponseUtil.wrapOrNotFound(assemblyDTO);
    }

    /**
     * {@code DELETE  /assemblies/:id} : delete the "id" assembly.
     *
     * @param idMap a Map representation of the id of the assemblyDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/assemblies/{id}")
    public ResponseEntity<Void> deleteAssembly(@MatrixVariable(pathVar = "id") Map<String, String> idMap) {
        final AssemblyId id = mapper.convertValue(idMap, AssemblyId.class);
        log.debug("REST request to delete Assembly : {}", id);
        assemblyService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
