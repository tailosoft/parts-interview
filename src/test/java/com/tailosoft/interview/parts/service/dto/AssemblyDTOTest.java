package com.tailosoft.interview.parts.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.tailosoft.interview.parts.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class AssemblyDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(AssemblyDTO.class);
        AssemblyDTO assemblyDTO1 = new AssemblyDTO();
        PartDTO partDTO1 = new PartDTO();
        partDTO1.setId(1L);
        assemblyDTO1.setParent(partDTO1);
        PartDTO partDTO1 = new PartDTO();
        partDTO1.setId(1L);
        assemblyDTO1.setChild(partDTO1);
        AssemblyDTO assemblyDTO2 = new AssemblyDTO();
        assertThat(assemblyDTO1).isNotEqualTo(assemblyDTO2);
        assemblyDTO2.setParent(partDTO1);
        assemblyDTO2.setChild(partDTO1);
        assertThat(assemblyDTO1).isEqualTo(assemblyDTO2);
        AssemblyDTO assemblyDTO3 = new AssemblyDTO();
        PartDTO partDTO3 = new PartDTO();
        partDTO3.setId(3L);
        assemblyDTO3.setParent(partDTO3);
        PartDTO partDTO3 = new PartDTO();
        partDTO3.setId(3L);
        assemblyDTO3.setChild(partDTO3);
        assertThat(assemblyDTO1).isNotEqualTo(assemblyDTO3);
        AssemblyDTO assemblyDTO4 = new AssemblyDTO();
        assemblyDTO4.setParent(null);
        assemblyDTO4.setChild(null);
        assertThat(assemblyDTO1).isNotEqualTo(assemblyDTO4);
    }
}
