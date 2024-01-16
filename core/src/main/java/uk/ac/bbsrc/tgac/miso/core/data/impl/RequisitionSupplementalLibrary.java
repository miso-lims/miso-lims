package uk.ac.bbsrc.tgac.miso.core.data.impl;

import java.io.Serializable;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import uk.ac.bbsrc.tgac.miso.core.data.Library;
import uk.ac.bbsrc.tgac.miso.core.data.impl.RequisitionSupplementalLibrary.RequisitionSupplementalLibraryId;
import uk.ac.bbsrc.tgac.miso.core.util.LimsUtils;

/**
 * This class exists mainly as a Hibernate optimization. With this, we can easily retrieve and
 * modify which supplemental libraries are linked to the requisition. Excluding them from the
 * Requisition model itself eliminates the chance of performance impacts from inadvertent retrieval
 */
@Entity
@Table(name = "Requisition_SupplementalLibrary")
@IdClass(RequisitionSupplementalLibraryId.class)
public class RequisitionSupplementalLibrary implements Serializable {

  private static final long serialVersionUID = 1L;

  public static class RequisitionSupplementalLibraryId implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long requisitionId;

    private Library library;

    public Long getRequisitionId() {
      return requisitionId;
    }

    public void setRequisitionId(Long requisitionId) {
      this.requisitionId = requisitionId;
    }

    public Library getLibrary() {
      return library;
    }

    public void setLibrary(Library library) {
      this.library = library;
    }

    private Long getLibraryId() {
      return library == null ? null : library.getId();
    }

    @Override
    public int hashCode() {
      return Objects.hash(requisitionId, library);
    }

    @Override
    public boolean equals(Object obj) {
      return LimsUtils.equals(this, obj,
          RequisitionSupplementalLibraryId::getRequisitionId,
          RequisitionSupplementalLibraryId::getLibraryId);
    }

  }

  @Id
  @Column(nullable = false, updatable = false)
  private Long requisitionId;

  @Id
  @ManyToOne(targetEntity = LibraryImpl.class)
  @JoinColumn(name = "libraryId", nullable = false, updatable = false)
  private Library library;

  public RequisitionSupplementalLibrary() {
    // default constructor
  }

  public RequisitionSupplementalLibrary(long requisitionId, Library library) {
    this.requisitionId = requisitionId;
    this.library = library;
  }

  public Long getRequisitionId() {
    return requisitionId;
  }

  public void setRequisitionId(Long requisitionId) {
    this.requisitionId = requisitionId;
  }

  public Library getLibrary() {
    return library;
  }

  public void setLibrary(Library library) {
    this.library = library;
  }

  @Override
  public int hashCode() {
    return Objects.hash(requisitionId, library);
  }

  @Override
  public boolean equals(Object obj) {
    return LimsUtils.equals(this, obj,
        RequisitionSupplementalLibrary::getRequisitionId,
        RequisitionSupplementalLibrary::getLibrary);
  }

}
