package com.tailosoft.interview.parts.service;

import com.tailosoft.interview.parts.domain.*; // for static metamodels
import com.tailosoft.interview.parts.domain.Part;
import com.tailosoft.interview.parts.repository.PartRepository;
import com.tailosoft.interview.parts.service.criteria.PartCriteria;
import com.tailosoft.interview.parts.service.dto.PartDTO;
import com.tailosoft.interview.parts.service.mapper.PartMapper;
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
 * Service for executing complex queries for {@link Part} entities in the database.
 * The main input is a {@link PartCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link PartDTO} or a {@link Page} of {@link PartDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class PartQueryService extends QueryService<Part> {

    private final Logger log = LoggerFactory.getLogger(PartQueryService.class);

    private final PartRepository partRepository;

    private final PartMapper partMapper;

    public PartQueryService(PartRepository partRepository, PartMapper partMapper) {
        this.partRepository = partRepository;
        this.partMapper = partMapper;
    }

    /**
     * Return a {@link List} of {@link PartDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<PartDTO> findByCriteria(PartCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<Part> specification = createSpecification(criteria);
        return partMapper.toDto(partRepository.findAll(specification));
    }

    /**
     * Return a {@link Page} of {@link PartDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<PartDTO> findByCriteria(PartCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<Part> specification = createSpecification(criteria);
        return partRepository.findAll(specification, page).map(partMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(PartCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<Part> specification = createSpecification(criteria);
        return partRepository.count(specification);
    }

    /**
     * Function to convert {@link PartCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<Part> createSpecification(PartCriteria criteria) {
        Specification<Part> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            if (criteria.getDistinct() != null) {
                specification = specification.and(distinct(criteria.getDistinct()));
            }
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), Part_.id));
            }
            if (criteria.getName() != null) {
                specification = specification.and(buildStringSpecification(criteria.getName(), Part_.name));
            }
            if (criteria.getAssemblyCost() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getAssemblyCost(), Part_.assemblyCost));
            }
            if (criteria.getBestPrice() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getBestPrice(), Part_.bestPrice));
            }
            if (criteria.getBestSupplierId() != null) {
                specification =
                    specification.and(
                        buildSpecification(
                            criteria.getBestSupplierId(),
                            root -> root.join(Part_.bestSupplier, JoinType.LEFT).get(Supplier_.id)
                        )
                    );
            }
            if (criteria.getChildParentId() != null) {
                specification =
                    specification.and(
                        buildSpecification(
                            criteria.getChildParentId(),
                            root -> root.join(Part_.children, JoinType.LEFT).get(Assembly_.id).get(AssemblyId_.parentId)
                        )
                    );
            }
            if (criteria.getChildChildId() != null) {
                specification =
                    specification.and(
                        buildSpecification(
                            criteria.getChildChildId(),
                            root -> root.join(Part_.children, JoinType.LEFT).get(Assembly_.id).get(AssemblyId_.childId)
                        )
                    );
            }
            if (criteria.getParentParentId() != null) {
                specification =
                    specification.and(
                        buildSpecification(
                            criteria.getParentParentId(),
                            root -> root.join(Part_.parents, JoinType.LEFT).get(Assembly_.id).get(AssemblyId_.parentId)
                        )
                    );
            }
            if (criteria.getParentChildId() != null) {
                specification =
                    specification.and(
                        buildSpecification(
                            criteria.getParentChildId(),
                            root -> root.join(Part_.parents, JoinType.LEFT).get(Assembly_.id).get(AssemblyId_.childId)
                        )
                    );
            }
            if (criteria.getPartSupplierPartId() != null) {
                specification =
                    specification.and(
                        buildSpecification(
                            criteria.getPartSupplierPartId(),
                            root -> root.join(Part_.partSuppliers, JoinType.LEFT).get(PartSupplier_.id).get(PartSupplierId_.partId)
                        )
                    );
            }
            if (criteria.getPartSupplierSupplierId() != null) {
                specification =
                    specification.and(
                        buildSpecification(
                            criteria.getPartSupplierSupplierId(),
                            root -> root.join(Part_.partSuppliers, JoinType.LEFT).get(PartSupplier_.id).get(PartSupplierId_.supplierId)
                        )
                    );
            }
        }
        return specification;
    }
}
