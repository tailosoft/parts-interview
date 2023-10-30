package com.tailosoft.interview.parts.service;

import com.tailosoft.interview.parts.domain.PartSupplier;
import com.tailosoft.interview.parts.domain.PartSupplierId;
import com.tailosoft.interview.parts.repository.PartSupplierRepository;
import com.tailosoft.interview.parts.service.dto.PartSupplierDTO;
import com.tailosoft.interview.parts.service.mapper.PartSupplierMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link PartSupplier}.
 */
@Service
@Transactional
public class PartSupplierService {

    private final Logger log = LoggerFactory.getLogger(PartSupplierService.class);

    private final PartSupplierRepository partSupplierRepository;

    private final PartSupplierMapper partSupplierMapper;

    public PartSupplierService(PartSupplierRepository partSupplierRepository, PartSupplierMapper partSupplierMapper) {
        this.partSupplierRepository = partSupplierRepository;
        this.partSupplierMapper = partSupplierMapper;
    }

    /**
     * Save a partSupplier.
     *
     * @param partSupplierDTO the entity to save.
     * @return the persisted entity.
     */
    public PartSupplierDTO save(PartSupplierDTO partSupplierDTO) {
        log.debug("Request to save PartSupplier : {}", partSupplierDTO);
        PartSupplier partSupplier = partSupplierMapper.toEntity(partSupplierDTO);
        partSupplier = partSupplierRepository.save(partSupplier);
        return partSupplierMapper.toDto(partSupplier);
    }

    /**
     * Update a partSupplier.
     *
     * @param partSupplierDTO the entity to save.
     * @return the persisted entity.
     */
    public PartSupplierDTO update(PartSupplierDTO partSupplierDTO) {
        log.debug("Request to update PartSupplier : {}", partSupplierDTO);
        PartSupplier partSupplier = partSupplierMapper.toEntity(partSupplierDTO);
        partSupplier = partSupplierRepository.save(partSupplier);
        return partSupplierMapper.toDto(partSupplier);
    }

    /**
     * Partially update a partSupplier.
     *
     * @param partSupplierDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<PartSupplierDTO> partialUpdate(PartSupplierDTO partSupplierDTO) {
        log.debug("Request to partially update PartSupplier : {}", partSupplierDTO);

        return partSupplierRepository
            .findById(partSupplierMapper.toEntity(partSupplierDTO).getId())
            .map(existingPartSupplier -> {
                partSupplierMapper.partialUpdate(existingPartSupplier, partSupplierDTO);

                return existingPartSupplier;
            })
            .map(partSupplierRepository::save)
            .map(partSupplierMapper::toDto);
    }

    /**
     * Get all the partSuppliers.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<PartSupplierDTO> findAll(Pageable pageable) {
        log.debug("Request to get all PartSuppliers");
        return partSupplierRepository.findAll(pageable).map(partSupplierMapper::toDto);
    }

    /**
     * Get all the partSuppliers with eager load of many-to-many relationships.
     *
     * @return the list of entities.
     */
    public Page<PartSupplierDTO> findAllWithEagerRelationships(Pageable pageable) {
        return partSupplierRepository.findAllWithEagerRelationships(pageable).map(partSupplierMapper::toDto);
    }

    /**
     * Get one partSupplier by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<PartSupplierDTO> findOne(PartSupplierId id) {
        log.debug("Request to get PartSupplier : {}", id);
        return partSupplierRepository.findOneWithEagerRelationships(id).map(partSupplierMapper::toDto);
    }

    /**
     * Delete the partSupplier by id.
     *
     * @param id the id of the entity.
     */
    public void delete(PartSupplierId id) {
        log.debug("Request to delete PartSupplier : {}", id);
        partSupplierRepository.deleteById(id);
    }
}
