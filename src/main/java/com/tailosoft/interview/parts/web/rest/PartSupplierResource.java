package com.tailosoft.interview.parts.web.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tailosoft.interview.parts.domain.PartSupplierId;
import com.tailosoft.interview.parts.repository.PartSupplierRepository;
import com.tailosoft.interview.parts.service.PartSupplierQueryService;
import com.tailosoft.interview.parts.service.PartSupplierService;
import com.tailosoft.interview.parts.service.criteria.PartSupplierCriteria;
import com.tailosoft.interview.parts.service.dto.PartSupplierDTO;
import com.tailosoft.interview.parts.service.mapper.PartSupplierMapper;
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
 * REST controller for managing {@link com.tailosoft.interview.parts.domain.PartSupplier}.
 */
@RestController
@RequestMapping("/api")
public class PartSupplierResource {

    private final Logger log = LoggerFactory.getLogger(PartSupplierResource.class);

    private final ObjectMapper mapper = new ObjectMapper();

    private static final String ENTITY_NAME = "partSupplier";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final PartSupplierService partSupplierService;

    private final PartSupplierRepository partSupplierRepository;

    private final PartSupplierMapper partSupplierMapper;

    private final PartSupplierQueryService partSupplierQueryService;

    public PartSupplierResource(
        PartSupplierService partSupplierService,
        PartSupplierRepository partSupplierRepository,
        PartSupplierMapper partSupplierMapper,
        PartSupplierQueryService partSupplierQueryService
    ) {
        this.partSupplierService = partSupplierService;
        this.partSupplierRepository = partSupplierRepository;
        this.partSupplierMapper = partSupplierMapper;
        this.partSupplierQueryService = partSupplierQueryService;
    }

    /**
     * {@code POST  /part-suppliers} : Create a new partSupplier.
     *
     * @param partSupplierDTO the partSupplierDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new partSupplierDTO, or with status {@code 400 (Bad Request)} if the partSupplier has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/part-suppliers")
    public ResponseEntity<PartSupplierDTO> createPartSupplier(@Valid @RequestBody PartSupplierDTO partSupplierDTO)
        throws URISyntaxException {
        log.debug("REST request to save PartSupplier : {}", partSupplierDTO);
        PartSupplierId id = partSupplierMapper.toEntity(partSupplierDTO).getId();
        if (id == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (partSupplierService.findOne(id).isPresent()) {
            throw new BadRequestAlertException("This partSupplier already exists", ENTITY_NAME, "idduplicate");
        }
        PartSupplierDTO result = partSupplierService.save(partSupplierDTO);
        return ResponseEntity
            .created(
                new URI("/api/part-suppliers/" + "partId=" + result.getPart().getId() + ";" + "supplierId=" + result.getSupplier().getId())
            )
            .headers(
                HeaderUtil.createEntityCreationAlert(
                    applicationName,
                    true,
                    ENTITY_NAME,
                    "partId=" + result.getPart().getId() + ";" + "supplierId=" + result.getSupplier().getId().toString()
                )
            )
            .body(result);
    }

    /**
     * {@code PUT  /part-suppliers/:id} : Updates an existing partSupplier.
     *
     * @param idMap a Map representation of the id of the partSupplierDTO to save.
     * @param partSupplierDTO the partSupplierDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated partSupplierDTO,
     * or with status {@code 400 (Bad Request)} if the partSupplierDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the partSupplierDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/part-suppliers/{id}")
    public ResponseEntity<PartSupplierDTO> updatePartSupplier(
        @MatrixVariable(pathVar = "id") Map<String, String> idMap,
        @Valid @RequestBody PartSupplierDTO partSupplierDTO
    ) throws URISyntaxException {
        final PartSupplierId id = mapper.convertValue(idMap, PartSupplierId.class);
        log.debug("REST request to update PartSupplier : {}, {}", id, partSupplierDTO);
        if (!Objects.equals(id, partSupplierMapper.toEntity(partSupplierDTO).getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!partSupplierRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        PartSupplierDTO result = partSupplierService.update(partSupplierDTO);
        return ResponseEntity
            .ok()
            .headers(
                HeaderUtil.createEntityUpdateAlert(
                    applicationName,
                    true,
                    ENTITY_NAME,
                    "partId=" + partSupplierDTO.getPart().getId() + ";" + "supplierId=" + partSupplierDTO.getSupplier().getId().toString()
                )
            )
            .body(result);
    }

    /**
     * {@code PATCH  /part-suppliers/:id} : Partial updates given fields of an existing partSupplier, field will ignore if it is null
     *
     * @param idMap a Map representation of the id of the partSupplierDTO to save.
     * @param partSupplierDTO the partSupplierDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated partSupplierDTO,
     * or with status {@code 400 (Bad Request)} if the partSupplierDTO is not valid,
     * or with status {@code 404 (Not Found)} if the partSupplierDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the partSupplierDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/part-suppliers/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<PartSupplierDTO> partialUpdatePartSupplier(
        @MatrixVariable(pathVar = "id") Map<String, String> idMap,
        @NotNull @RequestBody PartSupplierDTO partSupplierDTO
    ) throws URISyntaxException {
        final PartSupplierId id = mapper.convertValue(idMap, PartSupplierId.class);
        log.debug("REST request to partial update PartSupplier partially : {}, {}", id, partSupplierDTO);
        if (!Objects.equals(id, partSupplierMapper.toEntity(partSupplierDTO).getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!partSupplierRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<PartSupplierDTO> result = partSupplierService.partialUpdate(partSupplierDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(
                applicationName,
                true,
                ENTITY_NAME,
                "partId=" + partSupplierDTO.getPart().getId() + ";" + "supplierId=" + partSupplierDTO.getSupplier().getId().toString()
            )
        );
    }

    /**
     * {@code GET  /part-suppliers} : get all the partSuppliers.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of partSuppliers in body.
     */
    @GetMapping("/part-suppliers")
    public ResponseEntity<List<PartSupplierDTO>> getAllPartSuppliers(
        PartSupplierCriteria criteria,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        log.debug("REST request to get PartSuppliers by criteria: {}", criteria);

        Page<PartSupplierDTO> page = partSupplierQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /part-suppliers/count} : count all the partSuppliers.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/part-suppliers/count")
    public ResponseEntity<Long> countPartSuppliers(PartSupplierCriteria criteria) {
        log.debug("REST request to count PartSuppliers by criteria: {}", criteria);
        return ResponseEntity.ok().body(partSupplierQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /part-suppliers/:id} : get the "id" partSupplier.
     *
     * @param idMap a Map representation of the id of the partSupplierDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the partSupplierDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/part-suppliers/{id}")
    public ResponseEntity<PartSupplierDTO> getPartSupplier(@MatrixVariable(pathVar = "id") Map<String, String> idMap) {
        final PartSupplierId id = mapper.convertValue(idMap, PartSupplierId.class);
        log.debug("REST request to get PartSupplier : {}", id);
        Optional<PartSupplierDTO> partSupplierDTO = partSupplierService.findOne(id);
        return ResponseUtil.wrapOrNotFound(partSupplierDTO);
    }

    /**
     * {@code DELETE  /part-suppliers/:id} : delete the "id" partSupplier.
     *
     * @param idMap a Map representation of the id of the partSupplierDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/part-suppliers/{id}")
    public ResponseEntity<Void> deletePartSupplier(@MatrixVariable(pathVar = "id") Map<String, String> idMap) {
        final PartSupplierId id = mapper.convertValue(idMap, PartSupplierId.class);
        log.debug("REST request to delete PartSupplier : {}", id);
        partSupplierService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
