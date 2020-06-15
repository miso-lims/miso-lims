/*
 * Copyright (c) 2012. The Genome Analysis Centre, Norwich, UK
 * MISO project contacts: Robert Davey @ TGAC
 * *********************************************************************
 *
 * This file is part of MISO.
 *
 * MISO is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * MISO is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with MISO.  If not, see <http://www.gnu.org/licenses/>.
 *
 * *********************************************************************
 */

package uk.ac.bbsrc.tgac.miso.core.data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

import uk.ac.bbsrc.tgac.miso.core.data.type.PlatformType;

@Entity
@Table(name = "IndexFamily")
public class IndexFamily implements Deletable, Nameable, Serializable {

  private static final long serialVersionUID = 1L;

  private static final long UNSAVED_ID = 0L;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long indexFamilyId = UNSAVED_ID;

  @OneToMany(targetEntity = Index.class, mappedBy = "family", cascade = CascadeType.REMOVE)
  @OrderBy("position, name")
  private List<Index> indices;

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

  public List<Index> getIndices() {
    if (indices == null) {
      indices = new ArrayList<>();
    }
    return indices;
  }

  public Iterable<Index> getIndicesForPosition(int position) {
    List<Index> selected = new ArrayList<>();
    for (Index index : getIndices()) {
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
    if (this == obj) return true;
    if (obj == null) return false;
    if (getClass() != obj.getClass()) return false;
    IndexFamily other = (IndexFamily) obj;
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
    return "Index Family";
  }

  @Override
  public String getDeleteDescription() {
    return getName();
  }

}
