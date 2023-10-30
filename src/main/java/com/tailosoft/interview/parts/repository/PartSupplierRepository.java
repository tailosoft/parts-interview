package com.tailosoft.interview.parts.repository;

import com.tailosoft.interview.parts.domain.PartSupplier;
import com.tailosoft.interview.parts.domain.PartSupplierId;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the PartSupplier entity.
 */
@Repository
public interface PartSupplierRepository extends JpaRepository<PartSupplier, PartSupplierId>, JpaSpecificationExecutor<PartSupplier> {
    default Optional<PartSupplier> findOneWithEagerRelationships(PartSupplierId id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<PartSupplier> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<PartSupplier> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select partSupplier from PartSupplier partSupplier left join fetch partSupplier.part left join fetch partSupplier.supplier",
        countQuery = "select count(partSupplier) from PartSupplier partSupplier"
    )
    Page<PartSupplier> findAllWithToOneRelationships(Pageable pageable);

    @Query("select partSupplier from PartSupplier partSupplier left join fetch partSupplier.part left join fetch partSupplier.supplier")
    List<PartSupplier> findAllWithToOneRelationships();

    @Query(
        "select partSupplier from PartSupplier partSupplier left join fetch partSupplier.part left join fetch partSupplier.supplier where partSupplier.id =:id"
    )
    Optional<PartSupplier> findOneWithToOneRelationships(@Param("id") PartSupplierId id);
}
