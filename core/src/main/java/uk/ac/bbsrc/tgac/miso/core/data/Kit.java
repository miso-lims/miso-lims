package uk.ac.bbsrc.tgac.miso.core.data;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Collection;

import com.eaglegenomics.simlims.core.Note;

import uk.ac.bbsrc.tgac.miso.core.data.impl.kit.KitDescriptor;

/**
 * A Kit represents a consumable that can be used as part of a lab procedure, whereby its type is
 * described by a {@link KitDescriptor} and its actual existence by a lot number. {@link Note}
 * objects can be added to Kits.
 * 
 * @author Rob Davey
 * @since 0.0.2
 */
public interface Kit extends Comparable<Kit>, Barcodable, Locatable, Nameable, Serializable {

  /**
   * Returns the lotNumber of this Kit object.
   * 
   * @return String lotNumber.
   */
  String getLotNumber();

  /**
   * Sets the lotNumber of this Kit object.
   * 
   * @param lotNumber the Kit lot number.
   * 
   */
  void setLotNumber(String lotNumber);

  /**
   * Returns the kitDate of this Kit object.
   * 
   * @return Date kitDate.
   */
  LocalDate getKitDate();

  /**
   * Sets the kitDate of this Kit object.
   * 
   * @param kitDate kitDate.
   * 
   */
  void setKitDate(LocalDate kitDate);

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
   * @param kd kitDescriptor.
   * 
   */
  void setKitDescriptor(KitDescriptor kd);

  /**
   * Sets the notes of this Kit object.
   * 
   * @param notes notes.
   * 
   */
  void setNotes(Collection<Note> notes);

  /**
   * Add a note to this Kit
   * 
   * @param note of type {@link Note}
   */
  void addNote(Note note);
}
