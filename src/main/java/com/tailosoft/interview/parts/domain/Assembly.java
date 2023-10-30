package com.tailosoft.interview.parts.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A Assembly.
 */
@Entity
@Table(name = "assembly")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Assembly implements Serializable {

    private static final long serialVersionUID = 1L;

    @EmbeddedId
    private AssemblyId id;

    @NotNull
    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    @ManyToOne(optional = false)
    @NotNull
    @JoinColumn(insertable = false, updatable = false)
    @JsonIgnoreProperties(value = { "bestSupplier", "children", "parents", "partSuppliers" }, allowSetters = true)
    private Part parent;

    @ManyToOne(optional = false)
    @NotNull
    @JoinColumn(insertable = false, updatable = false)
    @JsonIgnoreProperties(value = { "bestSupplier", "children", "parents", "partSuppliers" }, allowSetters = true)
    private Part child;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public AssemblyId getId() {
        return this.id;
    }

    public Assembly id(AssemblyId id) {
        this.setId(id);
        return this;
    }

    public void setId(AssemblyId id) {
        this.id = id;
    }

    public Integer getQuantity() {
        return this.quantity;
    }

    public Assembly quantity(Integer quantity) {
        this.setQuantity(quantity);
        return this;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Part getParent() {
        return this.parent;
    }

    public void setParent(Part part) {
        this.parent = part;
    }

    public Assembly parent(Part part) {
        this.setParent(part);
        return this;
    }

    public Part getChild() {
        return this.child;
    }

    public void setChild(Part part) {
        this.child = part;
    }

    public Assembly child(Part part) {
        this.setChild(part);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Assembly)) {
            return false;
        }
        return id != null && id.equals(((Assembly) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Assembly{" +
            "id=" + getId() +
            ", quantity=" + getQuantity() +
            "}";
    }
}
