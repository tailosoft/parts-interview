package com.tailosoft.interview.parts.service.mapper;

import com.tailosoft.interview.parts.domain.Part;
import com.tailosoft.interview.parts.domain.PartSupplier;
import com.tailosoft.interview.parts.domain.Supplier;
import com.tailosoft.interview.parts.service.dto.PartDTO;
import com.tailosoft.interview.parts.service.dto.PartSupplierDTO;
import com.tailosoft.interview.parts.service.dto.SupplierDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link PartSupplier} and its DTO {@link PartSupplierDTO}.
 */
@Mapper(componentModel = "spring")
public interface PartSupplierMapper extends EntityMapper<PartSupplierDTO, PartSupplier> {
    @Mapping(target = "part", source = "part", qualifiedByName = "partName")
    @Mapping(target = "supplier", source = "supplier", qualifiedByName = "supplierName")
    PartSupplierDTO toDto(PartSupplier s);

    @Mapping(target = "id.partId", source = "part.id")
    @Mapping(target = "id.supplierId", source = "supplier.id")
    PartSupplier toEntity(PartSupplierDTO partSupplierDTO);

    @Named("partName")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    PartDTO toDtoPartName(Part part);

    @Named("supplierName")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    SupplierDTO toDtoSupplierName(Supplier supplier);
}
