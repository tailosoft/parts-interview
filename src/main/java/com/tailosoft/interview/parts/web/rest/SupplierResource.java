package com.tailosoft.interview.parts.web.rest;

import com.tailosoft.interview.parts.repository.SupplierRepository;
import com.tailosoft.interview.parts.service.SupplierQueryService;
import com.tailosoft.interview.parts.service.SupplierService;
import com.tailosoft.interview.parts.service.criteria.SupplierCriteria;
import com.tailosoft.interview.parts.service.dto.SupplierDTO;
import com.tailosoft.interview.parts.web.rest.errors.BadRequestAlertException;
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
 * REST controller for managing {@link com.tailosoft.interview.parts.domain.Supplier}.
 */
@RestController
@RequestMapping("/api")
public class SupplierResource {

    private final Logger log = LoggerFactory.getLogger(SupplierResource.class);

    private static final String ENTITY_NAME = "supplier";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final SupplierService supplierService;

    private final SupplierRepository supplierRepository;

    private final SupplierQueryService supplierQueryService;

    public SupplierResource(
        SupplierService supplierService,
        SupplierRepository supplierRepository,
        SupplierQueryService supplierQueryService
    ) {
        this.supplierService = supplierService;
        this.supplierRepository = supplierRepository;
        this.supplierQueryService = supplierQueryService;
    }

    /**
     * {@code POST  /suppliers} : Create a new supplier.
     *
     * @param supplierDTO the supplierDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new supplierDTO, or with status {@code 400 (Bad Request)} if the supplier has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/suppliers")
    public ResponseEntity<SupplierDTO> createSupplier(@Valid @RequestBody SupplierDTO supplierDTO) throws URISyntaxException {
        log.debug("REST request to save Supplier : {}", supplierDTO);
        if (supplierDTO.getId() != null) {
            throw new BadRequestAlertException("A new supplier cannot already have an ID", ENTITY_NAME, "idexists");
        }
        SupplierDTO result = supplierService.save(supplierDTO);
        return ResponseEntity
            .created(new URI("/api/suppliers/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /suppliers/:id} : Updates an existing supplier.
     *
     * @param id the id of the supplierDTO to save.
     * @param supplierDTO the supplierDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated supplierDTO,
     * or with status {@code 400 (Bad Request)} if the supplierDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the supplierDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/suppliers/{id}")
    public ResponseEntity<SupplierDTO> updateSupplier(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody SupplierDTO supplierDTO
    ) throws URISyntaxException {
        log.debug("REST request to update Supplier : {}, {}", id, supplierDTO);
        if (supplierDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, supplierDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!supplierRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        SupplierDTO result = supplierService.update(supplierDTO);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, supplierDTO.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /suppliers/:id} : Partial updates given fields of an existing supplier, field will ignore if it is null
     *
     * @param id the id of the supplierDTO to save.
     * @param supplierDTO the supplierDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated supplierDTO,
     * or with status {@code 400 (Bad Request)} if the supplierDTO is not valid,
     * or with status {@code 404 (Not Found)} if the supplierDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the supplierDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/suppliers/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<SupplierDTO> partialUpdateSupplier(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody SupplierDTO supplierDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update Supplier partially : {}, {}", id, supplierDTO);
        if (supplierDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, supplierDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!supplierRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<SupplierDTO> result = supplierService.partialUpdate(supplierDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, supplierDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /suppliers} : get all the suppliers.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of suppliers in body.
     */
    @GetMapping("/suppliers")
    public ResponseEntity<List<SupplierDTO>> getAllSuppliers(
        SupplierCriteria criteria,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        log.debug("REST request to get Suppliers by criteria: {}", criteria);

        Page<SupplierDTO> page = supplierQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /suppliers/count} : count all the suppliers.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/suppliers/count")
    public ResponseEntity<Long> countSuppliers(SupplierCriteria criteria) {
        log.debug("REST request to count Suppliers by criteria: {}", criteria);
        return ResponseEntity.ok().body(supplierQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /suppliers/:id} : get the "id" supplier.
     *
     * @param id the id of the supplierDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the supplierDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/suppliers/{id}")
    public ResponseEntity<SupplierDTO> getSupplier(@PathVariable Long id) {
        log.debug("REST request to get Supplier : {}", id);
        Optional<SupplierDTO> supplierDTO = supplierService.findOne(id);
        return ResponseUtil.wrapOrNotFound(supplierDTO);
    }

    /**
     * {@code DELETE  /suppliers/:id} : delete the "id" supplier.
     *
     * @param id the id of the supplierDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/suppliers/{id}")
    public ResponseEntity<Void> deleteSupplier(@PathVariable Long id) {
        log.debug("REST request to delete Supplier : {}", id);
        supplierService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
