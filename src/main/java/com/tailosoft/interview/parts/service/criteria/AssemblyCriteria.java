package com.tailosoft.interview.parts.service.criteria;

import java.io.Serializable;
import java.util.Objects;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link com.tailosoft.interview.parts.domain.Assembly} entity. This class is used
 * in {@link com.tailosoft.interview.parts.web.rest.AssemblyResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /assemblies?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class AssemblyCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private IntegerFilter quantity;

    private LongFilter parentId;

    private LongFilter childId;

    private Boolean distinct;

    public AssemblyCriteria() {}

    public AssemblyCriteria(AssemblyCriteria other) {
        this.quantity = other.quantity == null ? null : other.quantity.copy();
        this.parentId = other.parentId == null ? null : other.parentId.copy();
        this.childId = other.childId == null ? null : other.childId.copy();
        this.distinct = other.distinct;
    }

    @Override
    public AssemblyCriteria copy() {
        return new AssemblyCriteria(this);
    }

    public IntegerFilter getQuantity() {
        return quantity;
    }

    public IntegerFilter quantity() {
        if (quantity == null) {
            quantity = new IntegerFilter();
        }
        return quantity;
    }

    public void setQuantity(IntegerFilter quantity) {
        this.quantity = quantity;
    }

    public LongFilter getParentId() {
        return parentId;
    }

    public LongFilter parentId() {
        if (parentId == null) {
            parentId = new LongFilter();
        }
        return parentId;
    }

    public void setParentId(LongFilter parentId) {
        this.parentId = parentId;
    }

    public LongFilter getChildId() {
        return childId;
    }

    public LongFilter childId() {
        if (childId == null) {
            childId = new LongFilter();
        }
        return childId;
    }

    public void setChildId(LongFilter childId) {
        this.childId = childId;
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
        final AssemblyCriteria that = (AssemblyCriteria) o;
        return (
            Objects.equals(quantity, that.quantity) &&
            Objects.equals(parentId, that.parentId) &&
            Objects.equals(childId, that.childId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(quantity, parentId, childId, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "AssemblyCriteria{" +
            (quantity != null ? "quantity=" + quantity + ", " : "") +
            (parentId != null ? "parentId=" + parentId + ", " : "") +
            (childId != null ? "childId=" + childId + ", " : "") +
            (distinct != null ? "distinct=" + distinct + ", " : "") +
            "}";
    }
}
