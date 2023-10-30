package com.tailosoft.interview.parts.service.mapper;

import com.tailosoft.interview.parts.domain.Part;
import com.tailosoft.interview.parts.domain.Supplier;
import com.tailosoft.interview.parts.service.dto.PartDTO;
import com.tailosoft.interview.parts.service.dto.SupplierDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Part} and its DTO {@link PartDTO}.
 */
@Mapper(componentModel = "spring")
public interface PartMapper extends EntityMapper<PartDTO, Part> {
    @Mapping(target = "bestSupplier", source = "bestSupplier", qualifiedByName = "supplierName")
    PartDTO toDto(Part s);

    @Named("supplierName")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    SupplierDTO toDtoSupplierName(Supplier supplier);
}
