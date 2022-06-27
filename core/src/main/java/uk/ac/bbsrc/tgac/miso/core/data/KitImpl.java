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

package uk.ac.bbsrc.tgac.miso.core.data;

import java.util.Collection;
import java.util.Date;
import java.util.HashSet;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.eaglegenomics.simlims.core.Note;

import uk.ac.bbsrc.tgac.miso.core.data.impl.kit.KitDescriptor;
import uk.ac.bbsrc.tgac.miso.core.util.CoverageIgnore;

/**
 * Skeleton implementation of a Kit
 * 
 * @author Rob Davey
 * @since 0.0.2
 */
@Entity
@Table(name = "Kit")
public class KitImpl implements Kit {

  private static final long serialVersionUID = 1L;
  public static final Long UNSAVED_ID = 0L;
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long kitId = KitImpl.UNSAVED_ID;
  private String identificationBarcode;
  private String locationBarcode;

  @OneToMany(targetEntity = Note.class, cascade = CascadeType.ALL)
  @JoinTable(name = "Kit_Note", joinColumns = {
      @JoinColumn(name = "kit_kitId") }, inverseJoinColumns = {
          @JoinColumn(name = "notes_noteId") })
  private Collection<Note> notes = new HashSet<>();

  private String lotNumber;

  @Temporal(TemporalType.DATE)
  private Date kitDate;

  @ManyToOne(targetEntity = KitDescriptor.class)
  @JoinColumn(name = "kitDescriptorId", nullable = false)
  private KitDescriptor kitDescriptor;

  @Override
  public long getId() {
    return kitId;
  }

  @Override
  public void setId(long id) {
    this.kitId = id;
  }

  @Override
  public String getLotNumber() {
    return lotNumber;
  }

  @Override
  public void setLotNumber(String lotNumber) {
    this.lotNumber = lotNumber;
  }

  @Override
  public Date getKitDate() {
    return kitDate;
  }

  @Override
  public void setKitDate(Date kitDate) {
    this.kitDate = kitDate;
  }

  @Override
  public Collection<Note> getNotes() {
    return notes;
  }

  @Override
  public KitDescriptor getKitDescriptor() {
    return kitDescriptor;
  }

  @Override
  public void setKitDescriptor(KitDescriptor kitDescriptor) {
    this.kitDescriptor = kitDescriptor;
  }

  @Override
  public void setNotes(Collection<Note> notes) {
    this.notes = notes;
  }

  @CoverageIgnore
  @Override
  public void addNote(Note note) {
    this.notes.add(note);
  }

  @Override
  public String getIdentificationBarcode() {
    return identificationBarcode;
  }

  @Override
  public void setIdentificationBarcode(String identificationBarcode) {
    this.identificationBarcode = identificationBarcode;
  }

  @Override
  public String getLocationBarcode() {
    return locationBarcode;
  }

  @Override
  public void setLocationBarcode(String locationBarcode) {
    this.locationBarcode = locationBarcode;
  }

  @CoverageIgnore
  @Override
  public String getName() {
    return getKitDescriptor().getName();
  }

  @Override
  @CoverageIgnore
  public String getLabelText() {
    return getLotNumber();
  }

  @CoverageIgnore
  @Override
  public int compareTo(Kit t) {
    if (getId() < t.getId()) return -1;
    if (getId() > t.getId()) return 1;
    return 0;
  }

  @CoverageIgnore
  @Override
  public String toString() {
    final StringBuilder sb = new StringBuilder();
    sb.append(getId());
    sb.append(" : ");
    sb.append(getLotNumber());
    sb.append(" : ");
    sb.append(getIdentificationBarcode());
    sb.append(" : ");
    sb.append(getKitDate());
    return sb.toString();
  }

  @Override
  public Date getBarcodeDate() {
    return getKitDate();
  }


  @Override
  public boolean isSaved() {
    return getId() != UNSAVED_ID;
  }

  @Override
  public <T> T visit(BarcodableVisitor<T> visitor) {
    return visitor.visitKit(this);
  }
}
