package com.tailosoft.interview.parts.service.dto;

import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the {@link com.tailosoft.interview.parts.domain.Part} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class PartDTO implements Serializable {

    private Long id;

    @NotNull
    private String name;

    private Double assemblyCost;

    private Double bestPrice;

    private SupplierDTO bestSupplier;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getAssemblyCost() {
        return assemblyCost;
    }

    public void setAssemblyCost(Double assemblyCost) {
        this.assemblyCost = assemblyCost;
    }

    public Double getBestPrice() {
        return bestPrice;
    }

    public void setBestPrice(Double bestPrice) {
        this.bestPrice = bestPrice;
    }

    public SupplierDTO getBestSupplier() {
        return bestSupplier;
    }

    public void setBestSupplier(SupplierDTO bestSupplier) {
        this.bestSupplier = bestSupplier;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof PartDTO)) {
            return false;
        }

        PartDTO partDTO = (PartDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, partDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "PartDTO{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", assemblyCost=" + getAssemblyCost() +
            ", bestPrice=" + getBestPrice() +
            ", bestSupplier=" + getBestSupplier() +
            "}";
    }
}
