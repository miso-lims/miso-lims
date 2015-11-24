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

import java.util.Collection;
import java.util.Date;

import org.codehaus.jackson.annotate.JsonTypeInfo;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import com.eaglegenomics.simlims.core.Note;

import uk.ac.bbsrc.tgac.miso.core.data.impl.kit.KitDescriptor;

/**
 * A Kit represents a consumable that can be used as part of a lab procedure, whereby its type is described by a {@link KitDescriptor} and
 * its actual existence by a lot number. {@link Note} objects can be added to Kits.
 * 
 * @author Rob Davey
 * @since 0.0.2
 */
@JsonSerialize(typing = JsonSerialize.Typing.STATIC, include = JsonSerialize.Inclusion.NON_NULL)
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "@class")
public interface Kit extends Comparable, Barcodable, Locatable {
  /**
   * Returns the kitId of this Kit object.
   * 
   * @return Long kitId.
   */
  @Deprecated
  Long getKitId();

  /**
   * Sets the kitId of this Kit object.
   * 
   * @param kitId
   *          the id of this Kit object.
   * 
   */
  @Deprecated
  void setKitId(Long kitId);

  public void setId(long id);

  /**
   * Returns the lotNumber of this Kit object.
   * 
   * @return String lotNumber.
   */
  String getLotNumber();

  /**
   * Sets the lotNumber of this Kit object.
   * 
   * @param lotNumber
   *          the Kit lot number.
   * 
   */
  void setLotNumber(String lotNumber);

  /**
   * Returns the kitDate of this Kit object.
   * 
   * @return Date kitDate.
   */
  Date getKitDate();

  /**
   * Sets the kitDate of this Kit object.
   * 
   * @param kitDate
   *          kitDate.
   * 
   */
  void setKitDate(Date kitDate);

  /**
   * Returns the notes of this Kit object.
   * 
   * @return Collection<Note> notes.
   */
  Collection<Note> getNotes();

  /**
   * Returns the kitDescriptor of this Kit object.
   * 
   * @return {@link KitDescriptor} kitDescriptor.
   */
  KitDescriptor getKitDescriptor();

  /**
   * Sets the kitDescriptor of this Kit object.
   * 
   * @param kd
   *          kitDescriptor.
   * 
   */
  void setKitDescriptor(KitDescriptor kd);

  /**
   * Sets the notes of this Kit object.
   * 
   * @param notes
   *          notes.
   * 
   */
  void setNotes(Collection<Note> notes);

  /**
   * Add a note to this Kit
   * 
   * @param note
   *          of type {@link Note}
   */
  void addNote(Note note);
}
