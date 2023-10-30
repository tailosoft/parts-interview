package com.tailosoft.interview.parts.service.mapper;

import com.tailosoft.interview.parts.domain.Supplier;
import com.tailosoft.interview.parts.service.dto.SupplierDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Supplier} and its DTO {@link SupplierDTO}.
 */
@Mapper(componentModel = "spring")
public interface SupplierMapper extends EntityMapper<SupplierDTO, Supplier> {}
