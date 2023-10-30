package com.tailosoft.interview.parts.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A Supplier.
 */
@Entity
@Table(name = "supplier")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Supplier implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "name", nullable = false)
    private String name;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "supplier")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "part", "supplier" }, allowSetters = true)
    private Set<PartSupplier> partSuppliers = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Supplier id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public Supplier name(String name) {
        this.setName(name);
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<PartSupplier> getPartSuppliers() {
        return this.partSuppliers;
    }

    public void setPartSuppliers(Set<PartSupplier> partSuppliers) {
        if (this.partSuppliers != null) {
            this.partSuppliers.forEach(i -> i.setSupplier(null));
        }
        if (partSuppliers != null) {
            partSuppliers.forEach(i -> i.setSupplier(this));
        }
        this.partSuppliers = partSuppliers;
    }

    public Supplier partSuppliers(Set<PartSupplier> partSuppliers) {
        this.setPartSuppliers(partSuppliers);
        return this;
    }

    public Supplier addPartSupplier(PartSupplier partSupplier) {
        this.partSuppliers.add(partSupplier);
        partSupplier.setSupplier(this);
        return this;
    }

    public Supplier removePartSupplier(PartSupplier partSupplier) {
        this.partSuppliers.remove(partSupplier);
        partSupplier.setSupplier(null);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Supplier)) {
            return false;
        }
        return id != null && id.equals(((Supplier) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Supplier{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            "}";
    }
}
