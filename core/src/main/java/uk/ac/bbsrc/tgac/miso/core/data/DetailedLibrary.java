package uk.ac.bbsrc.tgac.miso.core.data;

import org.codehaus.jackson.annotate.JsonBackReference;

import uk.ac.bbsrc.tgac.miso.core.data.impl.kit.KitDescriptor;

public interface DetailedLibrary extends Library {

  @JsonBackReference
  Library getLibrary();

  void setLibrary(Library library);

  KitDescriptor getKitDescriptor();

  void setKitDescriptor(KitDescriptor prepKit);

  Boolean getArchived();

  void setArchived(Boolean archived);

  LibraryDesign getLibraryDesign();

  void setLibraryDesign(LibraryDesign rule);

  /**
   * True if the library's alias does not pass alias validation but cannot be changed (usually for historical reasons). Setting this to true
   * means the library will skip alias validation (and uniqueness validation, if enabled) during save.
   */
  boolean hasNonStandardAlias();

  void setNonStandardAlias(boolean nonStandardAlias);
  
  /**
   * @return the old LIMS' ID for this library prior to being migrated to MISO
   */
  Long getPreMigrationId();

  void setPreMigrationId(Long preMigrationId);

  LibraryDesignCode getLibraryDesignCode();

  void setLibraryDesignCode(LibraryDesignCode libraryDesignCode);

}
