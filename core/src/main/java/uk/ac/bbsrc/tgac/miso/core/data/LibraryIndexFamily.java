package uk.ac.bbsrc.tgac.miso.core.data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import uk.ac.bbsrc.tgac.miso.core.data.type.PlatformType;

@Entity
public class LibraryIndexFamily implements Deletable, Nameable, Serializable {

  private static final long serialVersionUID = 1L;

  private static final long UNSAVED_ID = 0L;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long indexFamilyId = UNSAVED_ID;

  @OneToMany(targetEntity = LibraryIndex.class, mappedBy = "family", cascade = CascadeType.REMOVE)
  @OrderBy("position, name")
  private List<LibraryIndex> indices;

  @Column(nullable = false)
  private String name;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private PlatformType platformType;

  private boolean fakeSequence;
  private boolean uniqueDualIndex;
  private Boolean archived;

  public Boolean getArchived() {
    return archived;
  }

  public List<LibraryIndex> getIndices() {
    if (indices == null) {
      indices = new ArrayList<>();
    }
    return indices;
  }

  public Iterable<LibraryIndex> getIndicesForPosition(int position) {
    List<LibraryIndex> selected = new ArrayList<>();
    for (LibraryIndex index : getIndices()) {
      if (index.getPosition() == position) {
        selected.add(index);
      }
    }
    return selected;
  }

  @Override
  public long getId() {
    return indexFamilyId;
  }

  @Override
  public String getName() {
    return name;
  }

  public PlatformType getPlatformType() {
    return platformType;
  }

  public boolean hasFakeSequence() {
    return fakeSequence;
  }

  public void setArchived(Boolean archived) {
    this.archived = archived;
  }

  @Override
  public void setId(long id) {
    indexFamilyId = id;
  }

  public void setName(String name) {
    this.name = name;
  }

  public void setPlatformType(PlatformType platform) {
    this.platformType = platform;
  }

  public void setFake(boolean fake) {
    this.fakeSequence = fake;
  }

  public void setUniqueDualIndex(boolean uniqueDualIndex) {
    this.uniqueDualIndex = uniqueDualIndex;
  }

  public boolean isUniqueDualIndex() {
    return uniqueDualIndex;
  }

  @Override
  public int hashCode() {
    return new HashCodeBuilder(11, 41)
        .append(archived)
        .append(name)
        .append(platformType)
        .toHashCode();
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    LibraryIndexFamily other = (LibraryIndexFamily) obj;
    return new EqualsBuilder()
        .append(archived, other.archived)
        .append(name, other.name)
        .append(platformType, other.platformType)
        .isEquals();
  }

  @Override
  public boolean isSaved() {
    return getId() != UNSAVED_ID;
  }

  @Override
  public String getDeleteType() {
    return "Library Index Family";
  }

  @Override
  public String getDeleteDescription() {
    return getName();
  }

}
