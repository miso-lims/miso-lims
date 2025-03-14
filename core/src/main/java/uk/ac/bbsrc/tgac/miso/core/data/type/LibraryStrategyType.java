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
 * Provides model access to the underlying MISO LibraryStrategyType lookup table. These types should
 * match the ENA submission schema for Library strategy types.
 * <p/>
 * See:
 * 
 * @author Rob Davey
 * @since 0.0.2
 */
@Entity
@Table(name = "LibraryStrategyType")
public class LibraryStrategyType implements Comparable<LibraryStrategyType>, Deletable, Identifiable, Serializable {

  private static final long serialVersionUID = 1L;

  private static final Long UNSAVED_ID = 0L;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long libraryStrategyTypeId = LibraryStrategyType.UNSAVED_ID;

  @Column(nullable = false, unique = true)
  private String name;

  @Column(nullable = false)
  private String description;

  /**
   * Returns the libraryStrategyTypeId of this LibraryStrategyType object.
   * 
   * @return Long libraryStrategyTypeId.
   */
  @Override
  public long getId() {
    return libraryStrategyTypeId;
  }

  /**
   * Sets the libraryStrategyTypeId of this LibraryStrategyType object.
   * 
   * @param libraryStrategyTypeId libraryStrategyTypeId.
   */
  @Override
  public void setId(long libraryStrategyTypeId) {
    this.libraryStrategyTypeId = libraryStrategyTypeId;
  }

  /**
   * Returns the name of this LibraryStrategyType object.
   * 
   * @return String name.
   */
  public String getName() {
    return name;
  }

  /**
   * Sets the name of this LibraryStrategyType object.
   * 
   * @param name name.
   */
  public void setName(String name) {
    this.name = name;
  }

  /**
   * Returns the description of this LibraryStrategyType object.
   * 
   * @return String description.
   */
  public String getDescription() {
    return description;
  }

  /**
   * Sets the description of this LibraryStrategyType object.
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
    if (!(obj instanceof LibraryStrategyType))
      return false;
    LibraryStrategyType them = (LibraryStrategyType) obj;
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
  public int compareTo(LibraryStrategyType t) {
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
    return "Library Strategy Type";
  }

  @Override
  public String getDeleteDescription() {
    return getName();
  }
}
