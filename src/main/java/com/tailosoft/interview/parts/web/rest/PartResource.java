package com.tailosoft.interview.parts.web.rest;

import com.tailosoft.interview.parts.repository.PartRepository;
import com.tailosoft.interview.parts.service.PartQueryService;
import com.tailosoft.interview.parts.service.PartService;
import com.tailosoft.interview.parts.service.criteria.PartCriteria;
import com.tailosoft.interview.parts.service.dto.PartDTO;
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
 * REST controller for managing {@link com.tailosoft.interview.parts.domain.Part}.
 */
@RestController
@RequestMapping("/api")
public class PartResource {

    private final Logger log = LoggerFactory.getLogger(PartResource.class);

    private static final String ENTITY_NAME = "part";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final PartService partService;

    private final PartRepository partRepository;

    private final PartQueryService partQueryService;

    public PartResource(PartService partService, PartRepository partRepository, PartQueryService partQueryService) {
        this.partService = partService;
        this.partRepository = partRepository;
        this.partQueryService = partQueryService;
    }

    /**
     * {@code POST  /parts} : Create a new part.
     *
     * @param partDTO the partDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new partDTO, or with status {@code 400 (Bad Request)} if the part has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/parts")
    public ResponseEntity<PartDTO> createPart(@Valid @RequestBody PartDTO partDTO) throws URISyntaxException {
        log.debug("REST request to save Part : {}", partDTO);
        if (partDTO.getId() != null) {
            throw new BadRequestAlertException("A new part cannot already have an ID", ENTITY_NAME, "idexists");
        }
        PartDTO result = partService.save(partDTO);
        return ResponseEntity
            .created(new URI("/api/parts/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /parts/:id} : Updates an existing part.
     *
     * @param id the id of the partDTO to save.
     * @param partDTO the partDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated partDTO,
     * or with status {@code 400 (Bad Request)} if the partDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the partDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/parts/{id}")
    public ResponseEntity<PartDTO> updatePart(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody PartDTO partDTO
    ) throws URISyntaxException {
        log.debug("REST request to update Part : {}, {}", id, partDTO);
        if (partDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, partDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!partRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        PartDTO result = partService.update(partDTO);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, partDTO.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /parts/:id} : Partial updates given fields of an existing part, field will ignore if it is null
     *
     * @param id the id of the partDTO to save.
     * @param partDTO the partDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated partDTO,
     * or with status {@code 400 (Bad Request)} if the partDTO is not valid,
     * or with status {@code 404 (Not Found)} if the partDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the partDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/parts/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<PartDTO> partialUpdatePart(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody PartDTO partDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update Part partially : {}, {}", id, partDTO);
        if (partDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, partDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!partRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<PartDTO> result = partService.partialUpdate(partDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, partDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /parts} : get all the parts.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of parts in body.
     */
    @GetMapping("/parts")
    public ResponseEntity<List<PartDTO>> getAllParts(
        PartCriteria criteria,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        log.debug("REST request to get Parts by criteria: {}", criteria);

        Page<PartDTO> page = partQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /parts/count} : count all the parts.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/parts/count")
    public ResponseEntity<Long> countParts(PartCriteria criteria) {
        log.debug("REST request to count Parts by criteria: {}", criteria);
        return ResponseEntity.ok().body(partQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /parts/:id} : get the "id" part.
     *
     * @param id the id of the partDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the partDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/parts/{id}")
    public ResponseEntity<PartDTO> getPart(@PathVariable Long id) {
        log.debug("REST request to get Part : {}", id);
        Optional<PartDTO> partDTO = partService.findOne(id);
        return ResponseUtil.wrapOrNotFound(partDTO);
    }

    /**
     * {@code DELETE  /parts/:id} : delete the "id" part.
     *
     * @param id the id of the partDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/parts/{id}")
    public ResponseEntity<Void> deletePart(@PathVariable Long id) {
        log.debug("REST request to delete Part : {}", id);
        partService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
