package com.tailosoft.interview.parts.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.tailosoft.interview.parts.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class PartSupplierDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(PartSupplierDTO.class);
        PartSupplierDTO partSupplierDTO1 = new PartSupplierDTO();
        PartDTO partDTO1 = new PartDTO();
        partDTO1.setId(1L);
        partSupplierDTO1.setPart(partDTO1);
        SupplierDTO supplierDTO1 = new SupplierDTO();
        supplierDTO1.setId(1L);
        partSupplierDTO1.setSupplier(supplierDTO1);
        PartSupplierDTO partSupplierDTO2 = new PartSupplierDTO();
        assertThat(partSupplierDTO1).isNotEqualTo(partSupplierDTO2);
        partSupplierDTO2.setPart(partDTO1);
        partSupplierDTO2.setSupplier(supplierDTO1);
        assertThat(partSupplierDTO1).isEqualTo(partSupplierDTO2);
        PartSupplierDTO partSupplierDTO3 = new PartSupplierDTO();
        PartDTO partDTO3 = new PartDTO();
        partDTO3.setId(3L);
        partSupplierDTO3.setPart(partDTO3);
        SupplierDTO supplierDTO3 = new SupplierDTO();
        supplierDTO3.setId(3L);
        partSupplierDTO3.setSupplier(supplierDTO3);
        assertThat(partSupplierDTO1).isNotEqualTo(partSupplierDTO3);
        PartSupplierDTO partSupplierDTO4 = new PartSupplierDTO();
        partSupplierDTO4.setPart(null);
        partSupplierDTO4.setSupplier(null);
        assertThat(partSupplierDTO1).isNotEqualTo(partSupplierDTO4);
    }
}
