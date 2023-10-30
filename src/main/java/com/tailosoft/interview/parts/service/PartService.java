package com.tailosoft.interview.parts.service;

import com.tailosoft.interview.parts.domain.Part;
import com.tailosoft.interview.parts.repository.PartRepository;
import com.tailosoft.interview.parts.service.dto.PartDTO;
import com.tailosoft.interview.parts.service.mapper.PartMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link Part}.
 */
@Service
@Transactional
public class PartService {

    private final Logger log = LoggerFactory.getLogger(PartService.class);

    private final PartRepository partRepository;

    private final PartMapper partMapper;

    public PartService(PartRepository partRepository, PartMapper partMapper) {
        this.partRepository = partRepository;
        this.partMapper = partMapper;
    }

    /**
     * Save a part.
     *
     * @param partDTO the entity to save.
     * @return the persisted entity.
     */
    public PartDTO save(PartDTO partDTO) {
        log.debug("Request to save Part : {}", partDTO);
        Part part = partMapper.toEntity(partDTO);
        part = partRepository.save(part);
        return partMapper.toDto(part);
    }

    /**
     * Update a part.
     *
     * @param partDTO the entity to save.
     * @return the persisted entity.
     */
    public PartDTO update(PartDTO partDTO) {
        log.debug("Request to update Part : {}", partDTO);
        Part part = partMapper.toEntity(partDTO);
        part = partRepository.save(part);
        return partMapper.toDto(part);
    }

    /**
     * Partially update a part.
     *
     * @param partDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<PartDTO> partialUpdate(PartDTO partDTO) {
        log.debug("Request to partially update Part : {}", partDTO);

        return partRepository
            .findById(partDTO.getId())
            .map(existingPart -> {
                partMapper.partialUpdate(existingPart, partDTO);

                return existingPart;
            })
            .map(partRepository::save)
            .map(partMapper::toDto);
    }

    /**
     * Get all the parts.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<PartDTO> findAll(Pageable pageable) {
        log.debug("Request to get all Parts");
        return partRepository.findAll(pageable).map(partMapper::toDto);
    }

    /**
     * Get all the parts with eager load of many-to-many relationships.
     *
     * @return the list of entities.
     */
    public Page<PartDTO> findAllWithEagerRelationships(Pageable pageable) {
        return partRepository.findAllWithEagerRelationships(pageable).map(partMapper::toDto);
    }

    /**
     * Get one part by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<PartDTO> findOne(Long id) {
        log.debug("Request to get Part : {}", id);
        return partRepository.findOneWithEagerRelationships(id).map(partMapper::toDto);
    }

    /**
     * Delete the part by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete Part : {}", id);
        partRepository.deleteById(id);
    }
}
