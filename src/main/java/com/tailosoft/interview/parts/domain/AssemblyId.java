package com.tailosoft.interview.parts.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.metamodel.StaticMetamodel;
import jakarta.validation.constraints.*;
import java.util.Objects;

@Embeddable
@StaticMetamodel(AssemblyId.class)
public class AssemblyId implements java.io.Serializable {

    @Column(name = "parent_id")
    private Long parentId;

    @Column(name = "child_id")
    private Long childId;

    public AssemblyId() {}

    public AssemblyId(Long parentId, Long childId) {
        this.parentId = parentId;
        this.childId = childId;
    }

    public Long getParentId() {
        return this.parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    public Long getChildId() {
        return this.childId;
    }

    public void setChildId(Long childId) {
        this.childId = childId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof AssemblyId)) {
            return false;
        }

        AssemblyId assemblyId = (AssemblyId) o;
        return Objects.equals(parentId, assemblyId.parentId) && Objects.equals(childId, assemblyId.childId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(parentId, childId);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "AssemblyId{" +
            ", parentId=" + getParentId() +
            ", childId=" + getChildId() +
            "}";
    }
}
