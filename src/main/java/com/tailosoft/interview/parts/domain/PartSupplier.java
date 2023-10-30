package com.tailosoft.interview.parts.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A PartSupplier.
 */
@Entity
@Table(name = "part_supplier")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class PartSupplier implements Serializable {

    private static final long serialVersionUID = 1L;

    @EmbeddedId
    private PartSupplierId id;

    @NotNull
    @Column(name = "price", nullable = false)
    private Double price;

    @ManyToOne(optional = false)
    @NotNull
    @JoinColumn(insertable = false, updatable = false)
    @JsonIgnoreProperties(value = { "bestSupplier", "children", "parents", "partSuppliers" }, allowSetters = true)
    private Part part;

    @ManyToOne(optional = false)
    @NotNull
    @JoinColumn(insertable = false, updatable = false)
    @JsonIgnoreProperties(value = { "partSuppliers" }, allowSetters = true)
    private Supplier supplier;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public PartSupplierId getId() {
        return this.id;
    }

    public PartSupplier id(PartSupplierId id) {
        this.setId(id);
        return this;
    }

    public void setId(PartSupplierId id) {
        this.id = id;
    }

    public Double getPrice() {
        return this.price;
    }

    public PartSupplier price(Double price) {
        this.setPrice(price);
        return this;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Part getPart() {
        return this.part;
    }

    public void setPart(Part part) {
        this.part = part;
    }

    public PartSupplier part(Part part) {
        this.setPart(part);
        return this;
    }

    public Supplier getSupplier() {
        return this.supplier;
    }

    public void setSupplier(Supplier supplier) {
        this.supplier = supplier;
    }

    public PartSupplier supplier(Supplier supplier) {
        this.setSupplier(supplier);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof PartSupplier)) {
            return false;
        }
        return id != null && id.equals(((PartSupplier) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "PartSupplier{" +
            "id=" + getId() +
            ", price=" + getPrice() +
            "}";
    }
}
