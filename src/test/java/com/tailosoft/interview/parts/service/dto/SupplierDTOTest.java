package com.tailosoft.interview.parts.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.tailosoft.interview.parts.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class SupplierDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(SupplierDTO.class);
        SupplierDTO supplierDTO1 = new SupplierDTO();
        supplierDTO1.setId(1L);
        SupplierDTO supplierDTO2 = new SupplierDTO();
        assertThat(supplierDTO1).isNotEqualTo(supplierDTO2);
        supplierDTO2.setId(supplierDTO1.getId());
        assertThat(supplierDTO1).isEqualTo(supplierDTO2);
        SupplierDTO supplierDTO3 = new SupplierDTO();
        supplierDTO3.setId(3L);
        assertThat(supplierDTO1).isNotEqualTo(supplierDTO3);
        SupplierDTO supplierDTO4 = new SupplierDTO();
        supplierDTO4.setId(null);
        assertThat(supplierDTO1).isNotEqualTo(supplierDTO4);
    }
}
