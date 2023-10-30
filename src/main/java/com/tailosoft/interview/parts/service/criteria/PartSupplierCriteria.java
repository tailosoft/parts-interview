package com.tailosoft.interview.parts.service.criteria;

import java.io.Serializable;
import java.util.Objects;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link com.tailosoft.interview.parts.domain.PartSupplier} entity. This class is used
 * in {@link com.tailosoft.interview.parts.web.rest.PartSupplierResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /part-suppliers?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class PartSupplierCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private DoubleFilter price;

    private LongFilter partId;

    private LongFilter supplierId;

    private Boolean distinct;

    public PartSupplierCriteria() {}

    public PartSupplierCriteria(PartSupplierCriteria other) {
        this.price = other.price == null ? null : other.price.copy();
        this.partId = other.partId == null ? null : other.partId.copy();
        this.supplierId = other.supplierId == null ? null : other.supplierId.copy();
        this.distinct = other.distinct;
    }

    @Override
    public PartSupplierCriteria copy() {
        return new PartSupplierCriteria(this);
    }

    public DoubleFilter getPrice() {
        return price;
    }

    public DoubleFilter price() {
        if (price == null) {
            price = new DoubleFilter();
        }
        return price;
    }

    public void setPrice(DoubleFilter price) {
        this.price = price;
    }

    public LongFilter getPartId() {
        return partId;
    }

    public LongFilter partId() {
        if (partId == null) {
            partId = new LongFilter();
        }
        return partId;
    }

    public void setPartId(LongFilter partId) {
        this.partId = partId;
    }

    public LongFilter getSupplierId() {
        return supplierId;
    }

    public LongFilter supplierId() {
        if (supplierId == null) {
            supplierId = new LongFilter();
        }
        return supplierId;
    }

    public void setSupplierId(LongFilter supplierId) {
        this.supplierId = supplierId;
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
        final PartSupplierCriteria that = (PartSupplierCriteria) o;
        return (
            Objects.equals(price, that.price) &&
            Objects.equals(partId, that.partId) &&
            Objects.equals(supplierId, that.supplierId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(price, partId, supplierId, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "PartSupplierCriteria{" +
            (price != null ? "price=" + price + ", " : "") +
            (partId != null ? "partId=" + partId + ", " : "") +
            (supplierId != null ? "supplierId=" + supplierId + ", " : "") +
            (distinct != null ? "distinct=" + distinct + ", " : "") +
            "}";
    }
}
