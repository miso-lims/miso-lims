package uk.ac.bbsrc.tgac.miso.core.data.impl.view.qc;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.Immutable;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

@Entity
@Table(name = "Library")
@Immutable
public class LibraryQcNode extends DetailedQcNode {

  private static final long serialVersionUID = 1L;

  @Id
  @Column(name = "libraryId")
  private long id;

  @Column(name = "sample_sampleId")
  private long sampleId;

  @Transient
  private List<LibraryAliquotQcNode> aliquots;

  @Override
  public Long getId() {
    return id;
  }

  @Override
  public void setId(long id) {
    this.id = id;
  }

  public long getSampleId() {
    return sampleId;
  }

  public void setSampleId(long sampleId) {
    this.sampleId = sampleId;
  }

  public List<LibraryAliquotQcNode> getAliquots() {
    if (aliquots == null) {
      aliquots = new ArrayList<>();
    }
    return aliquots;
  }

  public void setAliquots(List<LibraryAliquotQcNode> aliquots) {
    this.aliquots = aliquots;
  }

  @Override
  public List<? extends QcNode> getChildren() {
    return getAliquots();
  }

  @Override
  public QcNodeType getEntityType() {
    return QcNodeType.LIBRARY;
  }

  @Override
  public String getTypeLabel() {
    return QcNodeType.LIBRARY.getLabel();
  }

}
