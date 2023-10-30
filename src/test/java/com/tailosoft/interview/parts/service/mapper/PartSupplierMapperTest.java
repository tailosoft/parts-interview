package com.tailosoft.interview.parts.service.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class PartSupplierMapperTest {

    private PartSupplierMapper partSupplierMapper;

    @BeforeEach
    public void setUp() {
        partSupplierMapper = new PartSupplierMapperImpl();
    }
}
