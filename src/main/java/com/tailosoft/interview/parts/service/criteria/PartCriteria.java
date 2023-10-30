package com.tailosoft.interview.parts.service.criteria;

import java.io.Serializable;
import java.util.Objects;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link com.tailosoft.interview.parts.domain.Part} entity. This class is used
 * in {@link com.tailosoft.interview.parts.web.rest.PartResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /parts?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class PartCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter name;

    private DoubleFilter assemblyCost;

    private DoubleFilter bestPrice;

    private LongFilter bestSupplierId;

    private LongFilter childParentId;

    private LongFilter childChildId;

    private LongFilter parentParentId;

    private LongFilter parentChildId;

    private LongFilter partSupplierPartId;

    private LongFilter partSupplierSupplierId;

    private Boolean distinct;

    public PartCriteria() {}

    public PartCriteria(PartCriteria other) {
        this.id = other.id == null ? null : other.id.copy();
        this.name = other.name == null ? null : other.name.copy();
        this.assemblyCost = other.assemblyCost == null ? null : other.assemblyCost.copy();
        this.bestPrice = other.bestPrice == null ? null : other.bestPrice.copy();
        this.bestSupplierId = other.bestSupplierId == null ? null : other.bestSupplierId.copy();
        this.childParentId = other.childParentId == null ? null : other.childParentId.copy();
        this.childChildId = other.childChildId == null ? null : other.childChildId.copy();
        this.parentParentId = other.parentParentId == null ? null : other.parentParentId.copy();
        this.parentChildId = other.parentChildId == null ? null : other.parentChildId.copy();
        this.partSupplierPartId = other.partSupplierPartId == null ? null : other.partSupplierPartId.copy();
        this.partSupplierSupplierId = other.partSupplierSupplierId == null ? null : other.partSupplierSupplierId.copy();
        this.distinct = other.distinct;
    }

    @Override
    public PartCriteria copy() {
        return new PartCriteria(this);
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

    public DoubleFilter getAssemblyCost() {
        return assemblyCost;
    }

    public DoubleFilter assemblyCost() {
        if (assemblyCost == null) {
            assemblyCost = new DoubleFilter();
        }
        return assemblyCost;
    }

    public void setAssemblyCost(DoubleFilter assemblyCost) {
        this.assemblyCost = assemblyCost;
    }

    public DoubleFilter getBestPrice() {
        return bestPrice;
    }

    public DoubleFilter bestPrice() {
        if (bestPrice == null) {
            bestPrice = new DoubleFilter();
        }
        return bestPrice;
    }

    public void setBestPrice(DoubleFilter bestPrice) {
        this.bestPrice = bestPrice;
    }

    public LongFilter getBestSupplierId() {
        return bestSupplierId;
    }

    public LongFilter bestSupplierId() {
        if (bestSupplierId == null) {
            bestSupplierId = new LongFilter();
        }
        return bestSupplierId;
    }

    public void setBestSupplierId(LongFilter bestSupplierId) {
        this.bestSupplierId = bestSupplierId;
    }

    public LongFilter getChildParentId() {
        return childParentId;
    }

    public LongFilter childParentId() {
        if (childParentId == null) {
            childParentId = new LongFilter();
        }
        return childParentId;
    }

    public void setChildParentId(LongFilter childParentId) {
        this.childParentId = childParentId;
    }

    public LongFilter getChildChildId() {
        return childChildId;
    }

    public LongFilter childChildId() {
        if (childChildId == null) {
            childChildId = new LongFilter();
        }
        return childChildId;
    }

    public void setChildChildId(LongFilter childChildId) {
        this.childChildId = childChildId;
    }

    public LongFilter getParentParentId() {
        return parentParentId;
    }

    public LongFilter parentParentId() {
        if (parentParentId == null) {
            parentParentId = new LongFilter();
        }
        return parentParentId;
    }

    public void setParentParentId(LongFilter parentParentId) {
        this.parentParentId = parentParentId;
    }

    public LongFilter getParentChildId() {
        return parentChildId;
    }

    public LongFilter parentChildId() {
        if (parentChildId == null) {
            parentChildId = new LongFilter();
        }
        return parentChildId;
    }

    public void setParentChildId(LongFilter parentChildId) {
        this.parentChildId = parentChildId;
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
        final PartCriteria that = (PartCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(name, that.name) &&
            Objects.equals(assemblyCost, that.assemblyCost) &&
            Objects.equals(bestPrice, that.bestPrice) &&
            Objects.equals(bestSupplierId, that.bestSupplierId) &&
            Objects.equals(childParentId, that.childParentId) &&
            Objects.equals(childChildId, that.childChildId) &&
            Objects.equals(parentParentId, that.parentParentId) &&
            Objects.equals(parentChildId, that.parentChildId) &&
            Objects.equals(partSupplierPartId, that.partSupplierPartId) &&
            Objects.equals(partSupplierSupplierId, that.partSupplierSupplierId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(
            id,
            name,
            assemblyCost,
            bestPrice,
            bestSupplierId,
            childParentId,
            childChildId,
            parentParentId,
            parentChildId,
            partSupplierPartId,
            partSupplierSupplierId,
            distinct
        );
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "PartCriteria{" +
            (id != null ? "id=" + id + ", " : "") +
            (name != null ? "name=" + name + ", " : "") +
            (assemblyCost != null ? "assemblyCost=" + assemblyCost + ", " : "") +
            (bestPrice != null ? "bestPrice=" + bestPrice + ", " : "") +
            (bestSupplierId != null ? "bestSupplierId=" + bestSupplierId + ", " : "") +
            (childParentId != null ? "childParentId=" + childParentId + ", " : "") +
            (childChildId != null ? "childChildId=" + childChildId + ", " : "") +
            (parentParentId != null ? "parentParentId=" + parentParentId + ", " : "") +
            (parentChildId != null ? "parentChildId=" + parentChildId + ", " : "") +
            (partSupplierPartId != null ? "partSupplierPartId=" + partSupplierPartId + ", " : "") +
            (partSupplierSupplierId != null ? "partSupplierSupplierId=" + partSupplierSupplierId + ", " : "") +
            (distinct != null ? "distinct=" + distinct + ", " : "") +
            "}";
    }
}
