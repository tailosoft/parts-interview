package com.tailosoft.interview.parts.service.dto;

import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the {@link com.tailosoft.interview.parts.domain.Assembly} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class AssemblyDTO implements Serializable {

    @NotNull
    private Integer quantity;

    private PartDTO parent;

    private PartDTO child;

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public PartDTO getParent() {
        return parent;
    }

    public void setParent(PartDTO parent) {
        this.parent = parent;
    }

    public PartDTO getChild() {
        return child;
    }

    public void setChild(PartDTO child) {
        this.child = child;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof AssemblyDTO)) {
            return false;
        }

        AssemblyDTO assemblyDTO = (AssemblyDTO) o;
        if (this.parent == null && this.child == null) {
            return false;
        }
        return Objects.equals(this.parent, assemblyDTO.parent) && Objects.equals(this.child, assemblyDTO.child);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.parent, this.child);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "AssemblyDTO{" +
            "quantity=" + getQuantity() +
            ", parent=" + getParent() +
            ", child=" + getChild() +
            "}";
    }
}
