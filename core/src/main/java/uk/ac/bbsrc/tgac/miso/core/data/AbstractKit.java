/*
 * Copyright (c) 2012. The Genome Analysis Centre, Norwich, UK
 * MISO project contacts: Robert Davey, Mario Caccamo @ TGAC
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

import com.eaglegenomics.simlims.core.Note;
import uk.ac.bbsrc.tgac.miso.core.data.impl.kit.KitDescriptor;
import javax.persistence.*;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;

/**
 * Skeleton implementation of a Kit
 *
 * @author Rob Davey
 * @since 0.0.2
 */
public abstract class AbstractKit implements Kit {
  public static final Long UNSAVED_ID = 0L;
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private long kitId = AbstractKit.UNSAVED_ID;
  private String identificationBarcode;
  private String locationBarcode;

  @Transient
  @Enumerated(EnumType.STRING)
  private Collection<Note> notes = new HashSet<Note>();
  private String lotNumber;
  private Date kitDate;
  private KitDescriptor kitDescriptor;

  @Deprecated
  public Long getKitId() {
    return kitId;
  }

  @Deprecated
  public void setKitId(Long kitId) {
    this.kitId = kitId;
  }

  @Override
  public long getId() {
    return kitId;
  }

  public void setId(long id) {
    this.kitId = id;
  }

  public String getLotNumber() {
    return lotNumber;
  }

  public void setLotNumber(String lotNumber) {
    this.lotNumber = lotNumber;
  }

  public Date getKitDate() {
    return kitDate;
  }

  public void setKitDate(Date kitDate) {
    this.kitDate = kitDate;
  }

  public Collection<Note> getNotes() {
    return notes;
  }

  public KitDescriptor getKitDescriptor() {
    return kitDescriptor;
  }

  public void setKitDescriptor(KitDescriptor kitDescriptor) {
    this.kitDescriptor = kitDescriptor;
  }

  public void setNotes(Collection<Note> notes) {
    this.notes = notes;
  }

  public void addNote(Note note) {
    this.notes.add(note);
  }

  public String getIdentificationBarcode() {
    return identificationBarcode;
  }

  public void setIdentificationBarcode(String identificationBarcode) {
    this.identificationBarcode = identificationBarcode;
  }

  public String getLocationBarcode() {
    return locationBarcode;
  }

  public void setLocationBarcode(String locationBarcode) {
    this.locationBarcode = locationBarcode;
  }

  @Override
  public String getName() {
    return getKitDescriptor().getName();
  }

  public String getLabelText() {
    return getLotNumber();
  }

  @Override
  public int compareTo(Object o) {
    Kit t = (Kit)o;
    if (getId() < t.getId()) return -1;
    if (getId() > t.getId()) return 1;
    return 0;
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append(getId());
    sb.append(" : ");
    sb.append(getLotNumber());
    sb.append(" : ");
    sb.append(getIdentificationBarcode());
    sb.append(" : ");
    sb.append(getKitDate());
    return sb.toString();
  }
}
