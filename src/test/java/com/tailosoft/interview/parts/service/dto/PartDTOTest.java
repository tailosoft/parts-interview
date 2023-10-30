package com.tailosoft.interview.parts.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.tailosoft.interview.parts.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class PartDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(PartDTO.class);
        PartDTO partDTO1 = new PartDTO();
        partDTO1.setId(1L);
        PartDTO partDTO2 = new PartDTO();
        assertThat(partDTO1).isNotEqualTo(partDTO2);
        partDTO2.setId(partDTO1.getId());
        assertThat(partDTO1).isEqualTo(partDTO2);
        PartDTO partDTO3 = new PartDTO();
        partDTO3.setId(3L);
        assertThat(partDTO1).isNotEqualTo(partDTO3);
        PartDTO partDTO4 = new PartDTO();
        partDTO4.setId(null);
        assertThat(partDTO1).isNotEqualTo(partDTO4);
    }
}
