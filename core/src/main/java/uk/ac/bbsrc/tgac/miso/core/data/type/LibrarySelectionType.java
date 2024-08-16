package uk.ac.bbsrc.tgac.miso.core.data.type;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import uk.ac.bbsrc.tgac.miso.core.data.Deletable;
import uk.ac.bbsrc.tgac.miso.core.data.Identifiable;

/**
 * Provides model access to the underlying MISO LibrarySelectionType lookup table. These types
 * should match the ENA submission schema for Library selection types.
 * <p/>
 * See:
 * 
 * @author Rob Davey
 * @since 0.0.2
 */
@Entity
@Table(name = "LibrarySelectionType")
public class LibrarySelectionType implements Comparable<LibrarySelectionType>, Deletable, Identifiable, Serializable {

  private static final long serialVersionUID = 1L;

  private static final long UNSAVED_ID = 0L;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long librarySelectionTypeId = LibrarySelectionType.UNSAVED_ID;

  @Column(nullable = false, unique = true)
  private String name;

  @Column(nullable = false)
  private String description;

  /**
   * Returns the librarySelectionTypeId of this LibrarySelectionType object.
   * 
   * @return Long librarySelectionTypeId.
   */
  @Override
  public long getId() {
    return librarySelectionTypeId;
  }

  /**
   * Sets the librarySelectionTypeId of this LibrarySelectionType object.
   * 
   * @param librarySelectionTypeId librarySelectionTypeId.
   * 
   */
  @Override
  public void setId(long librarySelectionTypeId) {
    this.librarySelectionTypeId = librarySelectionTypeId;
  }

  /**
   * Returns the name of this LibrarySelectionType object.
   * 
   * @return String name.
   */
  public String getName() {
    return name;
  }

  /**
   * Sets the name of this LibrarySelectionType object.
   * 
   * @param name name.
   */
  public void setName(String name) {
    this.name = name;
  }

  /**
   * Returns the description of this LibrarySelectionType object.
   * 
   * @return String description.
   */
  public String getDescription() {
    return description;
  }

  /**
   * Sets the description of this LibrarySelectionType object.
   * 
   * @param description description.
   */
  public void setDescription(String description) {
    this.description = description;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null)
      return false;
    if (obj == this)
      return true;
    if (!(obj instanceof LibrarySelectionType))
      return false;
    LibrarySelectionType them = (LibrarySelectionType) obj;
    // If not saved, then compare resolved actual objects. Otherwise
    // just compare IDs.
    return getName().equals(them.getName());
  }

  @Override
  public int hashCode() {
    if (getId() != UNSAVED_ID) {
      return Long.valueOf(getId()).intValue();
    } else {
      int hashcode = -1;
      if (getName() != null)
        hashcode = 37 * hashcode + getName().hashCode();
      if (getDescription() != null)
        hashcode = 37 * hashcode + getDescription().hashCode();
      return hashcode;
    }
  }

  @Override
  public int compareTo(LibrarySelectionType t) {
    int name = getName().compareTo(t.getName());
    if (name != 0)
      return name;

    if (getId() < t.getId())
      return -1;
    if (getId() > t.getId())
      return 1;
    return 0;
  }

  @Override
  public boolean isSaved() {
    return getId() != UNSAVED_ID;
  }

  @Override
  public String getDeleteType() {
    return "Library Selection Type";
  }

  @Override
  public String getDeleteDescription() {
    return getName();
  }
}
