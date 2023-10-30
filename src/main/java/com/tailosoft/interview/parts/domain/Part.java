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
 * A Part.
 */
@Entity
@Table(name = "part")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Part implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "assembly_cost")
    private Double assemblyCost;

    @Column(name = "best_price")
    private Double bestPrice;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "partSuppliers" }, allowSetters = true)
    private Supplier bestSupplier;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "parent")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "parent", "child" }, allowSetters = true)
    private Set<Assembly> children = new HashSet<>();

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "child")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "parent", "child" }, allowSetters = true)
    private Set<Assembly> parents = new HashSet<>();

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "part")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "part", "supplier" }, allowSetters = true)
    private Set<PartSupplier> partSuppliers = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Part id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public Part name(String name) {
        this.setName(name);
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getAssemblyCost() {
        return this.assemblyCost;
    }

    public Part assemblyCost(Double assemblyCost) {
        this.setAssemblyCost(assemblyCost);
        return this;
    }

    public void setAssemblyCost(Double assemblyCost) {
        this.assemblyCost = assemblyCost;
    }

    public Double getBestPrice() {
        return this.bestPrice;
    }

    public Part bestPrice(Double bestPrice) {
        this.setBestPrice(bestPrice);
        return this;
    }

    public void setBestPrice(Double bestPrice) {
        this.bestPrice = bestPrice;
    }

    public Supplier getBestSupplier() {
        return this.bestSupplier;
    }

    public void setBestSupplier(Supplier supplier) {
        this.bestSupplier = supplier;
    }

    public Part bestSupplier(Supplier supplier) {
        this.setBestSupplier(supplier);
        return this;
    }

    public Set<Assembly> getChildren() {
        return this.children;
    }

    public void setChildren(Set<Assembly> assemblies) {
        if (this.children != null) {
            this.children.forEach(i -> i.setParent(null));
        }
        if (assemblies != null) {
            assemblies.forEach(i -> i.setParent(this));
        }
        this.children = assemblies;
    }

    public Part children(Set<Assembly> assemblies) {
        this.setChildren(assemblies);
        return this;
    }

    public Part addChild(Assembly assembly) {
        this.children.add(assembly);
        assembly.setParent(this);
        return this;
    }

    public Part removeChild(Assembly assembly) {
        this.children.remove(assembly);
        assembly.setParent(null);
        return this;
    }

    public Set<Assembly> getParents() {
        return this.parents;
    }

    public void setParents(Set<Assembly> assemblies) {
        if (this.parents != null) {
            this.parents.forEach(i -> i.setChild(null));
        }
        if (assemblies != null) {
            assemblies.forEach(i -> i.setChild(this));
        }
        this.parents = assemblies;
    }

    public Part parents(Set<Assembly> assemblies) {
        this.setParents(assemblies);
        return this;
    }

    public Part addParent(Assembly assembly) {
        this.parents.add(assembly);
        assembly.setChild(this);
        return this;
    }

    public Part removeParent(Assembly assembly) {
        this.parents.remove(assembly);
        assembly.setChild(null);
        return this;
    }

    public Set<PartSupplier> getPartSuppliers() {
        return this.partSuppliers;
    }

    public void setPartSuppliers(Set<PartSupplier> partSuppliers) {
        if (this.partSuppliers != null) {
            this.partSuppliers.forEach(i -> i.setPart(null));
        }
        if (partSuppliers != null) {
            partSuppliers.forEach(i -> i.setPart(this));
        }
        this.partSuppliers = partSuppliers;
    }

    public Part partSuppliers(Set<PartSupplier> partSuppliers) {
        this.setPartSuppliers(partSuppliers);
        return this;
    }

    public Part addPartSupplier(PartSupplier partSupplier) {
        this.partSuppliers.add(partSupplier);
        partSupplier.setPart(this);
        return this;
    }

    public Part removePartSupplier(PartSupplier partSupplier) {
        this.partSuppliers.remove(partSupplier);
        partSupplier.setPart(null);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Part)) {
            return false;
        }
        return id != null && id.equals(((Part) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Part{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", assemblyCost=" + getAssemblyCost() +
            ", bestPrice=" + getBestPrice() +
            "}";
    }
}
