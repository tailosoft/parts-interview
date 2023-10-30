package com.tailosoft.interview.parts.service.mapper;

import com.tailosoft.interview.parts.domain.Assembly;
import com.tailosoft.interview.parts.domain.Part;
import com.tailosoft.interview.parts.service.dto.AssemblyDTO;
import com.tailosoft.interview.parts.service.dto.PartDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Assembly} and its DTO {@link AssemblyDTO}.
 */
@Mapper(componentModel = "spring")
public interface AssemblyMapper extends EntityMapper<AssemblyDTO, Assembly> {
    @Mapping(target = "parent", source = "parent", qualifiedByName = "partName")
    @Mapping(target = "child", source = "child", qualifiedByName = "partName")
    AssemblyDTO toDto(Assembly s);

    @Mapping(target = "id.parentId", source = "parent.id")
    @Mapping(target = "id.childId", source = "child.id")
    Assembly toEntity(AssemblyDTO assemblyDTO);

    @Named("partName")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    PartDTO toDtoPartName(Part part);
}
