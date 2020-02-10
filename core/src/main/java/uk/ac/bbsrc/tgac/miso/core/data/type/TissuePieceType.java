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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with MISO. If not, see <http://www.gnu.org/licenses/>.
 *
 * *********************************************************************
 */

package uk.ac.bbsrc.tgac.miso.core.data.type;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import uk.ac.bbsrc.tgac.miso.core.data.Deletable;
import uk.ac.bbsrc.tgac.miso.core.data.Identifiable;

@Entity
@Table(name = "TissuePieceType")
public class TissuePieceType implements Deletable, Identifiable, Serializable {

  private static final long serialVersionUID = 1L;

  private static final Long UNSAVED_ID = 0L;

  @Column(nullable = false)
  private String abbreviation;

  private String v2NamingCode;

  @Column(nullable = false)
  private boolean archived = false;

  @Column(nullable = false)
  private String name;

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private long tissuePieceTypeId = TissuePieceType.UNSAVED_ID;

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj == null) return false;
    if (getClass() != obj.getClass()) return false;
    TissuePieceType other = (TissuePieceType) obj;
    if (abbreviation == null) {
      if (other.abbreviation != null) return false;
    } else if (!abbreviation.equals(other.abbreviation)) return false;
    if (v2NamingCode == null) {
      if (other.v2NamingCode != null) return false;
    } else if (!v2NamingCode.equals(other.v2NamingCode)) return false;
    if (archived != other.archived) return false;
    if (name == null) {
      if (other.name != null) return false;
    } else if (!name.equals(other.name)) return false;
    if (tissuePieceTypeId != other.tissuePieceTypeId) return false;
    return true;
  }

  public String getAbbreviation() {
    return abbreviation;
  }

  public boolean getArchived() {
    return archived;
  }

  @Override
  public String getDeleteDescription() {
    return name;
  }

  @Override
  public String getDeleteType() {
    return "Tissue Piece Type";
  }

  @Override
  public long getId() {
    return tissuePieceTypeId;
  }

  public String getName() {
    return name;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((abbreviation == null) ? 0 : abbreviation.hashCode());
    result = prime * result + ((v2NamingCode == null) ? 0 : v2NamingCode.hashCode());
    result = prime * result + (archived ? 1231 : 1237);
    result = prime * result + ((name == null) ? 0 : name.hashCode());
    result = prime * result + (int) (tissuePieceTypeId ^ (tissuePieceTypeId >>> 32));
    return result;
  }

  @Override
  public boolean isSaved() {
    return getId() != UNSAVED_ID;
  }

  public void setAbbreviation(String abbreviation) {
    this.abbreviation = abbreviation;
  }

  public void setArchived(boolean archived) {
    this.archived = archived;
  }

  @Override
  public void setId(long libraryTypeId) {
    this.tissuePieceTypeId = libraryTypeId;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getV2NamingCode() {
    return v2NamingCode;
  }

  public void setV2NamingCode(String v2NamingCode) {
    this.v2NamingCode = v2NamingCode;
  }

  @Override
  public String toString() {
    return "TissuePieceType [abbreviation=" + abbreviation + ", archived=" + archived + ", v2NamingCode=" + v2NamingCode
        + ", tissuePieceTypeId=" + tissuePieceTypeId + ", name=" + name + "]";
  }

}
