package com.tailosoft.interview.parts.service;

import com.tailosoft.interview.parts.domain.Assembly;
import com.tailosoft.interview.parts.domain.AssemblyId;
import com.tailosoft.interview.parts.repository.AssemblyRepository;
import com.tailosoft.interview.parts.service.dto.AssemblyDTO;
import com.tailosoft.interview.parts.service.mapper.AssemblyMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link Assembly}.
 */
@Service
@Transactional
public class AssemblyService {

    private final Logger log = LoggerFactory.getLogger(AssemblyService.class);

    private final AssemblyRepository assemblyRepository;

    private final AssemblyMapper assemblyMapper;

    public AssemblyService(AssemblyRepository assemblyRepository, AssemblyMapper assemblyMapper) {
        this.assemblyRepository = assemblyRepository;
        this.assemblyMapper = assemblyMapper;
    }

    /**
     * Save a assembly.
     *
     * @param assemblyDTO the entity to save.
     * @return the persisted entity.
     */
    public AssemblyDTO save(AssemblyDTO assemblyDTO) {
        log.debug("Request to save Assembly : {}", assemblyDTO);
        Assembly assembly = assemblyMapper.toEntity(assemblyDTO);
        assembly = assemblyRepository.save(assembly);
        return assemblyMapper.toDto(assembly);
    }

    /**
     * Update a assembly.
     *
     * @param assemblyDTO the entity to save.
     * @return the persisted entity.
     */
    public AssemblyDTO update(AssemblyDTO assemblyDTO) {
        log.debug("Request to update Assembly : {}", assemblyDTO);
        Assembly assembly = assemblyMapper.toEntity(assemblyDTO);
        assembly = assemblyRepository.save(assembly);
        return assemblyMapper.toDto(assembly);
    }

    /**
     * Partially update a assembly.
     *
     * @param assemblyDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<AssemblyDTO> partialUpdate(AssemblyDTO assemblyDTO) {
        log.debug("Request to partially update Assembly : {}", assemblyDTO);

        return assemblyRepository
            .findById(assemblyMapper.toEntity(assemblyDTO).getId())
            .map(existingAssembly -> {
                assemblyMapper.partialUpdate(existingAssembly, assemblyDTO);

                return existingAssembly;
            })
            .map(assemblyRepository::save)
            .map(assemblyMapper::toDto);
    }

    /**
     * Get all the assemblies.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<AssemblyDTO> findAll(Pageable pageable) {
        log.debug("Request to get all Assemblies");
        return assemblyRepository.findAll(pageable).map(assemblyMapper::toDto);
    }

    /**
     * Get all the assemblies with eager load of many-to-many relationships.
     *
     * @return the list of entities.
     */
    public Page<AssemblyDTO> findAllWithEagerRelationships(Pageable pageable) {
        return assemblyRepository.findAllWithEagerRelationships(pageable).map(assemblyMapper::toDto);
    }

    /**
     * Get one assembly by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<AssemblyDTO> findOne(AssemblyId id) {
        log.debug("Request to get Assembly : {}", id);
        return assemblyRepository.findOneWithEagerRelationships(id).map(assemblyMapper::toDto);
    }

    /**
     * Delete the assembly by id.
     *
     * @param id the id of the entity.
     */
    public void delete(AssemblyId id) {
        log.debug("Request to delete Assembly : {}", id);
        assemblyRepository.deleteById(id);
    }
}
