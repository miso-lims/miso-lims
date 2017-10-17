package uk.ac.bbsrc.tgac.miso.core.data;

public interface DetailedLibrary extends Library {

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
  @Override
  Long getPreMigrationId();

  void setPreMigrationId(Long preMigrationId);

  LibraryDesignCode getLibraryDesignCode();

  void setLibraryDesignCode(LibraryDesignCode libraryDesignCode);

  String getGroupId();

  void setGroupId(String groupId);

  String getGroupDescription();

  void setGroupDescription(String groupDescription);

}
