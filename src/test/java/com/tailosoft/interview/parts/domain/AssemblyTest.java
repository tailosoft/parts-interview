package com.tailosoft.interview.parts.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.tailosoft.interview.parts.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class AssemblyTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Assembly.class);
        Assembly assembly1 = new Assembly();
        assembly1.setId(new AssemblyId(1L, 1L));
        Assembly assembly2 = new Assembly();
        assembly2.setId(assembly1.getId());
        assertThat(assembly1).isEqualTo(assembly2);
        assembly2.setId(new AssemblyId(2L, 2L));
        assertThat(assembly1).isNotEqualTo(assembly2);
        assembly1.setId(null);
        assertThat(assembly1).isNotEqualTo(assembly2);
    }
}
