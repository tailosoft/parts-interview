package com.tailosoft.interview.parts.service;

import com.tailosoft.interview.parts.domain.*; // for static metamodels
import com.tailosoft.interview.parts.domain.Assembly;
import com.tailosoft.interview.parts.repository.AssemblyRepository;
import com.tailosoft.interview.parts.service.criteria.AssemblyCriteria;
import com.tailosoft.interview.parts.service.dto.AssemblyDTO;
import com.tailosoft.interview.parts.service.mapper.AssemblyMapper;
import jakarta.persistence.criteria.JoinType;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.jhipster.service.QueryService;

/**
 * Service for executing complex queries for {@link Assembly} entities in the database.
 * The main input is a {@link AssemblyCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link AssemblyDTO} or a {@link Page} of {@link AssemblyDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class AssemblyQueryService extends QueryService<Assembly> {

    private final Logger log = LoggerFactory.getLogger(AssemblyQueryService.class);

    private final AssemblyRepository assemblyRepository;

    private final AssemblyMapper assemblyMapper;

    public AssemblyQueryService(AssemblyRepository assemblyRepository, AssemblyMapper assemblyMapper) {
        this.assemblyRepository = assemblyRepository;
        this.assemblyMapper = assemblyMapper;
    }

    /**
     * Return a {@link List} of {@link AssemblyDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<AssemblyDTO> findByCriteria(AssemblyCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<Assembly> specification = createSpecification(criteria);
        return assemblyMapper.toDto(assemblyRepository.findAll(specification));
    }

    /**
     * Return a {@link Page} of {@link AssemblyDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<AssemblyDTO> findByCriteria(AssemblyCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<Assembly> specification = createSpecification(criteria);
        return assemblyRepository.findAll(specification, page).map(assemblyMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(AssemblyCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<Assembly> specification = createSpecification(criteria);
        return assemblyRepository.count(specification);
    }

    /**
     * Function to convert {@link AssemblyCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<Assembly> createSpecification(AssemblyCriteria criteria) {
        Specification<Assembly> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            if (criteria.getDistinct() != null) {
                specification = specification.and(distinct(criteria.getDistinct()));
            }
            if (criteria.getQuantity() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getQuantity(), Assembly_.quantity));
            }
            if (criteria.getParentId() != null) {
                specification =
                    specification.and(
                        buildSpecification(criteria.getParentId(), root -> root.join(Assembly_.parent, JoinType.LEFT).get(Part_.id))
                    );
            }
            if (criteria.getChildId() != null) {
                specification =
                    specification.and(
                        buildSpecification(criteria.getChildId(), root -> root.join(Assembly_.child, JoinType.LEFT).get(Part_.id))
                    );
            }
        }
        return specification;
    }
}
