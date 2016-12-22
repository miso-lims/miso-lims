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

//import com.fasterxml.jackson.annotation.*;
//import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.codehaus.jackson.annotate.JsonTypeInfo;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.joda.time.LocalDate;

import com.eaglegenomics.simlims.core.Note;

import uk.ac.bbsrc.tgac.miso.core.data.impl.kit.KitDescriptor;

/**
 * A KitComponent represents a consumable that can be used as part of a lab procedure, whereby its type is described by a
 * {@link KitDescriptor} and its actual existence by a lot number. {@link Note} objects can be added to Kits.
 *
 * @author Rob Davey
 * @since 0.0.2
 */
@JsonSerialize(typing = JsonSerialize.Typing.STATIC, include = JsonSerialize.Inclusion.NON_NULL)
// @JsonIdentityInfo(generator = ObjectIdGenerators.IntSequenceGenerator.class, property = "@id")
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "@class")
public interface KitComponent extends Comparable, Barcodable, Locatable {
  /**
   * Returns the kitId of this KitComponent object.
   *
   * @return Long kitId.
   */
  @Deprecated
  Long getKitComponentId();

  /**
   * Sets the kitId of this KitComponent object.
   *
   * @param kitId the id of this KitComponent object.
   *
   */
  @Deprecated
  void setKitComponentId(Long kitId);

  void setId(long kitId);

  /**
   * Returns the lotNumber of this KitComponent object.
   *
   * @return String lotNumber.
   */
  String getLotNumber();

  /**
   * Sets the lotNumber of this KitComponent object.
   *
   * @param lotNumber the KitComponent lot number.
   *
   */
  void setLotNumber(String lotNumber);

  /**
   * Returns the kitDate of this KitComponent object.
   *
   * @return LocalDate kitDate.
   */
  LocalDate getKitReceivedDate();

  /**
   * Sets the kitDate of this KitComponent object.
   *
   * @param kitReceivedDate kitDate.
   *
   */
  void setKitReceivedDate(LocalDate kitReceivedDate);

  /**
   * Returns the notes of this KitComponent object.
   *
   * @return Collection<Note> notes.
   */

  /**
   * Returns the kitExpiryDate of this KitComponent object.
   *
   * @return Date kitExpiryDate.
   */
  LocalDate getKitExpiryDate();

  /**
   * Sets the kitExpiryDate of this KitComponent object.
   *
   * @param kitExpiryDate kitExpiryDate.
   *
   */
  void setKitExpiryDate(LocalDate kitExpiryDate);

  /**
   * Returns the notes of this KitComponent object.
   *
   * @return Collection<Note> notes.
   */
  Collection<Note> getNotes();

  /**
   * Returns the kitDescriptor of this KitComponent object.
   *
   * @return {@link KitDescriptor} kitDescriptor.
   */
  void setNotes(Collection<Note> notes);

  /**
   * Add a note to this KitComponent
   *
   * @param note of type {@link Note}
   */
  void addNote(Note note);

  /**
   * Sets this KitComponent as exhausted/not exhausted
   *
   * @param exhausted (true/false)
   */
  void setExhausted(boolean exhausted);

  /**
   * Returns the current exhaustion state of this KitComponent
   *
   * @return true if exhausted, false if not exhausted
   */
  boolean isExhausted();

  /**
   * Returns the KitComponentDescriptor assigned to this KitComponent
   *
   * @return KitComponentDescriptor KitComponentDescriptor
   */
  KitComponentDescriptor getKitComponentDescriptor();

  /**
   * Sets the kitComponentDescriptor of this kitComponent
   *
   * @param kitComponentDescriptor kitComponentDescriptor
   */
  void setKitComponentDescriptor(KitComponentDescriptor kitComponentDescriptor);
}