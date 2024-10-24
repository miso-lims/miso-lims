package uk.ac.bbsrc.tgac.miso.core.data.impl.view.qc;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.hibernate.annotations.Immutable;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import uk.ac.bbsrc.tgac.miso.core.data.Boxable.EntityType;
import uk.ac.bbsrc.tgac.miso.core.util.LimsUtils;

@Entity
@Table(name = "LibraryAliquot")
@Immutable
public class LibraryAliquotQcNode extends DetailedQcNode {

  private static final long serialVersionUID = 1L;

  @Id
  @Column(name = "aliquotId")
  private long id;

  private Long libraryId;

  private Long parentAliquotId;

  @Transient
  private List<LibraryAliquotQcNode> childAliquots;

  @Transient
  private List<PoolQcNode> pools;

  @Override
  public Long getId() {
    return id;
  }

  @Override
  public void setId(long id) {
    this.id = id;
  }

  public Long getLibraryId() {
    return libraryId;
  }

  public void setLibraryId(Long libraryId) {
    this.libraryId = libraryId;
  }

  public Long getParentAliquotId() {
    return parentAliquotId;
  }

  public void setParentAliquotId(Long parentAliquotId) {
    this.parentAliquotId = parentAliquotId;
  }

  public List<LibraryAliquotQcNode> getChildAliquots() {
    if (childAliquots == null) {
      childAliquots = new ArrayList<>();
    }
    return childAliquots;
  }

  public void setChildAliquots(List<LibraryAliquotQcNode> childAliquots) {
    this.childAliquots = childAliquots;
  }

  public List<PoolQcNode> getPools() {
    if (pools == null) {
      pools = new ArrayList<>();
    }
    return pools;
  }

  public void setPools(List<PoolQcNode> pools) {
    this.pools = pools;
  }

  @Override
  public List<? extends QcNode> getChildren() {
    List<QcNode> all = new ArrayList<>();
    all.addAll(getChildAliquots());
    all.addAll(getPools());
    return all;
  }

  @Override
  public QcNodeType getEntityType() {
    return QcNodeType.LIBRARY_ALIQUOT;
  }

  @Override
  public String getTypeLabel() {
    return EntityType.LIBRARY_ALIQUOT.getLabel();
  }

  @Override
  public int hashCode() {
    return Objects.hash(id);
  }

  @Override
  public boolean equals(Object obj) {
    return LimsUtils.equals(this, obj, LibraryAliquotQcNode::getId);
  }

}
