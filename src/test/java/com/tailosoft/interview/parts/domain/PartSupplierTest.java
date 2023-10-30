package com.tailosoft.interview.parts.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.tailosoft.interview.parts.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class PartSupplierTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(PartSupplier.class);
        PartSupplier partSupplier1 = new PartSupplier();
        partSupplier1.setId(new PartSupplierId(1L, 1L));
        PartSupplier partSupplier2 = new PartSupplier();
        partSupplier2.setId(partSupplier1.getId());
        assertThat(partSupplier1).isEqualTo(partSupplier2);
        partSupplier2.setId(new PartSupplierId(2L, 2L));
        assertThat(partSupplier1).isNotEqualTo(partSupplier2);
        partSupplier1.setId(null);
        assertThat(partSupplier1).isNotEqualTo(partSupplier2);
    }
}
