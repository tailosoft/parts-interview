package com.tailosoft.interview.parts.service;

import com.tailosoft.interview.parts.domain.*; // for static metamodels
import com.tailosoft.interview.parts.domain.PartSupplier;
import com.tailosoft.interview.parts.repository.PartSupplierRepository;
import com.tailosoft.interview.parts.service.criteria.PartSupplierCriteria;
import com.tailosoft.interview.parts.service.dto.PartSupplierDTO;
import com.tailosoft.interview.parts.service.mapper.PartSupplierMapper;
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
 * Service for executing complex queries for {@link PartSupplier} entities in the database.
 * The main input is a {@link PartSupplierCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link PartSupplierDTO} or a {@link Page} of {@link PartSupplierDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class PartSupplierQueryService extends QueryService<PartSupplier> {

    private final Logger log = LoggerFactory.getLogger(PartSupplierQueryService.class);

    private final PartSupplierRepository partSupplierRepository;

    private final PartSupplierMapper partSupplierMapper;

    public PartSupplierQueryService(PartSupplierRepository partSupplierRepository, PartSupplierMapper partSupplierMapper) {
        this.partSupplierRepository = partSupplierRepository;
        this.partSupplierMapper = partSupplierMapper;
    }

    /**
     * Return a {@link List} of {@link PartSupplierDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<PartSupplierDTO> findByCriteria(PartSupplierCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<PartSupplier> specification = createSpecification(criteria);
        return partSupplierMapper.toDto(partSupplierRepository.findAll(specification));
    }

    /**
     * Return a {@link Page} of {@link PartSupplierDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<PartSupplierDTO> findByCriteria(PartSupplierCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<PartSupplier> specification = createSpecification(criteria);
        return partSupplierRepository.findAll(specification, page).map(partSupplierMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(PartSupplierCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<PartSupplier> specification = createSpecification(criteria);
        return partSupplierRepository.count(specification);
    }

    /**
     * Function to convert {@link PartSupplierCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<PartSupplier> createSpecification(PartSupplierCriteria criteria) {
        Specification<PartSupplier> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            if (criteria.getDistinct() != null) {
                specification = specification.and(distinct(criteria.getDistinct()));
            }
            if (criteria.getPrice() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getPrice(), PartSupplier_.price));
            }
            if (criteria.getPartId() != null) {
                specification =
                    specification.and(
                        buildSpecification(criteria.getPartId(), root -> root.join(PartSupplier_.part, JoinType.LEFT).get(Part_.id))
                    );
            }
            if (criteria.getSupplierId() != null) {
                specification =
                    specification.and(
                        buildSpecification(
                            criteria.getSupplierId(),
                            root -> root.join(PartSupplier_.supplier, JoinType.LEFT).get(Supplier_.id)
                        )
                    );
            }
        }
        return specification;
    }
}
