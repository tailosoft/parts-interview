package com.tailosoft.interview.parts.repository;

import com.tailosoft.interview.parts.domain.Assembly;
import com.tailosoft.interview.parts.domain.AssemblyId;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Assembly entity.
 */
@Repository
public interface AssemblyRepository extends JpaRepository<Assembly, AssemblyId>, JpaSpecificationExecutor<Assembly> {
    default Optional<Assembly> findOneWithEagerRelationships(AssemblyId id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<Assembly> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<Assembly> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select assembly from Assembly assembly left join fetch assembly.parent left join fetch assembly.child",
        countQuery = "select count(assembly) from Assembly assembly"
    )
    Page<Assembly> findAllWithToOneRelationships(Pageable pageable);

    @Query("select assembly from Assembly assembly left join fetch assembly.parent left join fetch assembly.child")
    List<Assembly> findAllWithToOneRelationships();

    @Query("select assembly from Assembly assembly left join fetch assembly.parent left join fetch assembly.child where assembly.id =:id")
    Optional<Assembly> findOneWithToOneRelationships(@Param("id") AssemblyId id);
}
