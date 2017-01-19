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
package uk.ac.bbsrc.tgac.miso.core.data.impl.kit;

import java.util.Collection;
import java.util.HashSet;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Transient;

import org.joda.time.LocalDate;
import org.springframework.format.annotation.DateTimeFormat;

import com.eaglegenomics.simlims.core.Note;

import uk.ac.bbsrc.tgac.miso.core.data.KitComponent;
import uk.ac.bbsrc.tgac.miso.core.data.KitComponentDescriptor;
import uk.ac.bbsrc.tgac.miso.core.util.DateUtils;

/**
 * Skeleton implementation of a KitComponent
 *
 * @author Rob Davey, Michal Zak
 * @since 0.0.2
 */
public class KitComponentImpl implements KitComponent {
  public static final Long UNSAVED_ID = 0L;
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private long kitComponentId = KitComponentImpl.UNSAVED_ID;
  private String identificationBarcode;
  private String locationBarcode;

  @Transient
  @Enumerated(EnumType.STRING)
  private Collection<Note> notes = new HashSet<>();
  private String lotNumber;
  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
  private LocalDate kitReceivedDate;
  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
  private LocalDate kitExpiryDate;
  private boolean exhausted;

  private KitComponentDescriptor kitComponentDescriptor;

  @Override
  @Deprecated
  public Long getKitComponentId() {
    return kitComponentId;
  }

  @Override
  @Deprecated
  public void setKitComponentId(Long kitComponentId) {
    this.kitComponentId = kitComponentId;
  }

  @Override
  public long getId() {
    return kitComponentId;
  }

  @Override
  public void setId(long id) {
    this.kitComponentId = id;
  }

  // TODO: NOT SURE ABOUT THIS ONE
  @Override
  public String getName() {
    // it should be KitDescriptor.name + kitComponentDescriptor.name
    return getKitComponentDescriptor().getName();
  }

  @Override
  public String getLotNumber() {
    return lotNumber;
  }

  @Override
  public LocalDate getKitExpiryDate() {
    return kitExpiryDate;
  }

  @Override
  public void setKitExpiryDate(LocalDate kitExpiryDate) {
    this.kitExpiryDate = kitExpiryDate;
  }

  @Override
  public void setLotNumber(String lotNumber) {
    this.lotNumber = lotNumber;
  }

  @Override
  public LocalDate getKitReceivedDate() {
    return kitReceivedDate;
  }

  @Override
  public void setKitReceivedDate(LocalDate kitReceivedDate) {
    this.kitReceivedDate = kitReceivedDate;
  }

  @Override
  public void setExhausted(boolean exhausted) {
    this.exhausted = exhausted;
  }

  @Override
  public boolean isExhausted() {
    return exhausted;
  }

  // STRING OVERLOADS FOR FRONT-END
  public void setKitExpiryDate(String kitExpiryDate) {
    this.kitExpiryDate = DateUtils.asLocalDate(kitExpiryDate);
  }

  public void setKitReceivedDate(String kitReceivedDate) {
    this.kitReceivedDate = DateUtils.asLocalDate(kitReceivedDate);
  }

  @Override
  public Collection<Note> getNotes() {
    return notes;
  }

  @Override
  public KitComponentDescriptor getKitComponentDescriptor() {
    return kitComponentDescriptor;
  }

  @Override
  public void setKitComponentDescriptor(KitComponentDescriptor kitComponentDescriptor) {
    this.kitComponentDescriptor = kitComponentDescriptor;
  }

  @Override
  public void setNotes(Collection<Note> notes) {
    this.notes = notes;
  }

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

  @Override
  public String getLabelText() {
    return getLotNumber();
  }

  @Override
  public int compareTo(Object o) {
    KitComponent t = (KitComponent) o;
    if (getId() < t.getId()) return -1;
    if (getId() > t.getId()) return 1;
    return 0;
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append(getId());
    sb.append(" : ");
    sb.append(getIdentificationBarcode());
    sb.append(" : ");
    sb.append(getLocationBarcode());
    sb.append(" : ");
    sb.append(getKitReceivedDate());
    sb.append(" : ");
    sb.append(getKitExpiryDate());
    sb.append(" : ");
    sb.append(isExhausted());
    sb.append(" : ");
    sb.append(getIdentificationBarcode());
    sb.append(" : ");
    sb.append(getKitReceivedDate());
    sb.append("  : ");
    sb.append(getKitComponentDescriptor().getId());

    return sb.toString();
  }
}