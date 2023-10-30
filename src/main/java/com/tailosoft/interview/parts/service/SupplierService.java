package com.tailosoft.interview.parts.service;

import com.tailosoft.interview.parts.domain.Supplier;
import com.tailosoft.interview.parts.repository.SupplierRepository;
import com.tailosoft.interview.parts.service.dto.SupplierDTO;
import com.tailosoft.interview.parts.service.mapper.SupplierMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link Supplier}.
 */
@Service
@Transactional
public class SupplierService {

    private final Logger log = LoggerFactory.getLogger(SupplierService.class);

    private final SupplierRepository supplierRepository;

    private final SupplierMapper supplierMapper;

    public SupplierService(SupplierRepository supplierRepository, SupplierMapper supplierMapper) {
        this.supplierRepository = supplierRepository;
        this.supplierMapper = supplierMapper;
    }

    /**
     * Save a supplier.
     *
     * @param supplierDTO the entity to save.
     * @return the persisted entity.
     */
    public SupplierDTO save(SupplierDTO supplierDTO) {
        log.debug("Request to save Supplier : {}", supplierDTO);
        Supplier supplier = supplierMapper.toEntity(supplierDTO);
        supplier = supplierRepository.save(supplier);
        return supplierMapper.toDto(supplier);
    }

    /**
     * Update a supplier.
     *
     * @param supplierDTO the entity to save.
     * @return the persisted entity.
     */
    public SupplierDTO update(SupplierDTO supplierDTO) {
        log.debug("Request to update Supplier : {}", supplierDTO);
        Supplier supplier = supplierMapper.toEntity(supplierDTO);
        supplier = supplierRepository.save(supplier);
        return supplierMapper.toDto(supplier);
    }

    /**
     * Partially update a supplier.
     *
     * @param supplierDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<SupplierDTO> partialUpdate(SupplierDTO supplierDTO) {
        log.debug("Request to partially update Supplier : {}", supplierDTO);

        return supplierRepository
            .findById(supplierDTO.getId())
            .map(existingSupplier -> {
                supplierMapper.partialUpdate(existingSupplier, supplierDTO);

                return existingSupplier;
            })
            .map(supplierRepository::save)
            .map(supplierMapper::toDto);
    }

    /**
     * Get all the suppliers.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<SupplierDTO> findAll(Pageable pageable) {
        log.debug("Request to get all Suppliers");
        return supplierRepository.findAll(pageable).map(supplierMapper::toDto);
    }

    /**
     * Get one supplier by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<SupplierDTO> findOne(Long id) {
        log.debug("Request to get Supplier : {}", id);
        return supplierRepository.findById(id).map(supplierMapper::toDto);
    }

    /**
     * Delete the supplier by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete Supplier : {}", id);
        supplierRepository.deleteById(id);
    }
}
