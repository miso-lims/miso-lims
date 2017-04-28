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
import java.util.Collections;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import uk.ac.bbsrc.tgac.miso.core.data.type.PlatformType;

@Entity
@Table(name = "IndexFamily")
public class IndexFamily implements Serializable {

  private static final long serialVersionUID = 1L;
  // TODO: this is intended to be immutable, but it is not
  public static final IndexFamily NULL = new IndexFamily();

  static {
    NULL.setId(0L);
    NULL.setName("No index");
    NULL.setArchived(false);
    Index index = new Index();
    index.setFamily(NULL);
    index.setId(0L);
    index.setName("No index");
    index.setPosition(1);
    index.setSequence("");
    NULL.setIndices(Collections.singletonList(index));
  }

  private Boolean archived;
  @OneToMany(targetEntity = Index.class, fetch = FetchType.EAGER, mappedBy = "family")
  @OrderBy("position, name")
  @JsonManagedReference
  private List<Index> indices;
  @Column(nullable = false)
  private String name;
  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private PlatformType platformType;
  @Id
  private Long indexFamilyId;

  public Boolean getArchived() {
    return archived;
  }

  public List<Index> getIndices() {
    return indices;
  }

  public Iterable<Index> getIndicesForPosition(int position) {
    List<Index> selected = new ArrayList<>();
    for (Index index : indices) {
      if (index.getPosition() == position) {
        selected.add(index);
      }
    }
    return selected;
  }

  public Long getId() {
    return indexFamilyId;
  }

  public int getMaximumNumber() {
    int max = 0;
    for (Index index : indices) {
      if (index.getPosition() > max) {
        max = index.getPosition();
      }
    }
    return max;
  }

  public String getName() {
    return name;
  }

  public PlatformType getPlatformType() {
    return platformType;
  }

  public void setArchived(Boolean archived) {
    this.archived = archived;
  }

  public void setIndices(List<Index> indices) {
    this.indices = indices;
  }

  public void setId(Long id) {
    indexFamilyId = id;
  }

  public void setName(String name) {
    this.name = name;
  }

  public void setPlatformType(PlatformType platform) {
    this.platformType = platform;
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

}
