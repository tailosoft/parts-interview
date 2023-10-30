package com.tailosoft.interview.parts.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.metamodel.StaticMetamodel;
import jakarta.validation.constraints.*;
import java.util.Objects;

@Embeddable
@StaticMetamodel(PartSupplierId.class)
public class PartSupplierId implements java.io.Serializable {

    @Column(name = "part_id")
    private Long partId;

    @Column(name = "supplier_id")
    private Long supplierId;

    public PartSupplierId() {}

    public PartSupplierId(Long partId, Long supplierId) {
        this.partId = partId;
        this.supplierId = supplierId;
    }

    public Long getPartId() {
        return this.partId;
    }

    public void setPartId(Long partId) {
        this.partId = partId;
    }

    public Long getSupplierId() {
        return this.supplierId;
    }

    public void setSupplierId(Long supplierId) {
        this.supplierId = supplierId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof PartSupplierId)) {
            return false;
        }

        PartSupplierId partSupplierId = (PartSupplierId) o;
        return Objects.equals(partId, partSupplierId.partId) && Objects.equals(supplierId, partSupplierId.supplierId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(partId, supplierId);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "PartSupplierId{" +
            ", partId=" + getPartId() +
            ", supplierId=" + getSupplierId() +
            "}";
    }
}
