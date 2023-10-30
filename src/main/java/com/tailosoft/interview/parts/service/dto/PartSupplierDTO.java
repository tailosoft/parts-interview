package com.tailosoft.interview.parts.service.dto;

import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the {@link com.tailosoft.interview.parts.domain.PartSupplier} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class PartSupplierDTO implements Serializable {

    @NotNull
    private Double price;

    private PartDTO part;

    private SupplierDTO supplier;

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public PartDTO getPart() {
        return part;
    }

    public void setPart(PartDTO part) {
        this.part = part;
    }

    public SupplierDTO getSupplier() {
        return supplier;
    }

    public void setSupplier(SupplierDTO supplier) {
        this.supplier = supplier;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof PartSupplierDTO)) {
            return false;
        }

        PartSupplierDTO partSupplierDTO = (PartSupplierDTO) o;
        if (this.part == null && this.supplier == null) {
            return false;
        }
        return Objects.equals(this.part, partSupplierDTO.part) && Objects.equals(this.supplier, partSupplierDTO.supplier);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.part, this.supplier);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "PartSupplierDTO{" +
            "price=" + getPrice() +
            ", part=" + getPart() +
            ", supplier=" + getSupplier() +
            "}";
    }
}
