package com.tailosoft.interview.parts.service.criteria;

import java.io.Serializable;
import java.util.Objects;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link com.tailosoft.interview.parts.domain.Supplier} entity. This class is used
 * in {@link com.tailosoft.interview.parts.web.rest.SupplierResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /suppliers?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class SupplierCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter name;

    private LongFilter partSupplierPartId;

    private LongFilter partSupplierSupplierId;

    private Boolean distinct;

    public SupplierCriteria() {}

    public SupplierCriteria(SupplierCriteria other) {
        this.id = other.id == null ? null : other.id.copy();
        this.name = other.name == null ? null : other.name.copy();
        this.partSupplierPartId = other.partSupplierPartId == null ? null : other.partSupplierPartId.copy();
        this.partSupplierSupplierId = other.partSupplierSupplierId == null ? null : other.partSupplierSupplierId.copy();
        this.distinct = other.distinct;
    }

    @Override
    public SupplierCriteria copy() {
        return new SupplierCriteria(this);
    }

    public LongFilter getId() {
        return id;
    }

    public LongFilter id() {
        if (id == null) {
            id = new LongFilter();
        }
        return id;
    }

    public void setId(LongFilter id) {
        this.id = id;
    }

    public StringFilter getName() {
        return name;
    }

    public StringFilter name() {
        if (name == null) {
            name = new StringFilter();
        }
        return name;
    }

    public void setName(StringFilter name) {
        this.name = name;
    }

    public LongFilter getPartSupplierPartId() {
        return partSupplierPartId;
    }

    public LongFilter partSupplierPartId() {
        if (partSupplierPartId == null) {
            partSupplierPartId = new LongFilter();
        }
        return partSupplierPartId;
    }

    public void setPartSupplierPartId(LongFilter partSupplierPartId) {
        this.partSupplierPartId = partSupplierPartId;
    }

    public LongFilter getPartSupplierSupplierId() {
        return partSupplierSupplierId;
    }

    public LongFilter partSupplierSupplierId() {
        if (partSupplierSupplierId == null) {
            partSupplierSupplierId = new LongFilter();
        }
        return partSupplierSupplierId;
    }

    public void setPartSupplierSupplierId(LongFilter partSupplierSupplierId) {
        this.partSupplierSupplierId = partSupplierSupplierId;
    }

    public Boolean getDistinct() {
        return distinct;
    }

    public void setDistinct(Boolean distinct) {
        this.distinct = distinct;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final SupplierCriteria that = (SupplierCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(name, that.name) &&
            Objects.equals(partSupplierPartId, that.partSupplierPartId) &&
            Objects.equals(partSupplierSupplierId, that.partSupplierSupplierId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, partSupplierPartId, partSupplierSupplierId, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "SupplierCriteria{" +
            (id != null ? "id=" + id + ", " : "") +
            (name != null ? "name=" + name + ", " : "") +
            (partSupplierPartId != null ? "partSupplierPartId=" + partSupplierPartId + ", " : "") +
            (partSupplierSupplierId != null ? "partSupplierSupplierId=" + partSupplierSupplierId + ", " : "") +
            (distinct != null ? "distinct=" + distinct + ", " : "") +
            "}";
    }
}
