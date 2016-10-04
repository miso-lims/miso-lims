package uk.ac.bbsrc.tgac.miso.core.data;

import java.util.Date;

import org.codehaus.jackson.annotate.JsonBackReference;

import com.eaglegenomics.simlims.core.User;

import uk.ac.bbsrc.tgac.miso.core.data.impl.kit.KitDescriptor;

public interface LibraryAdditionalInfo {

  Long getLibraryId();

  void setLibraryId(Long libraryid);

  @JsonBackReference
  Library getLibrary();

  void setLibrary(Library library);

  KitDescriptor getPrepKit();

  void setPrepKit(KitDescriptor prepKit);

  User getCreatedBy();

  void setCreatedBy(User createdBy);

  Date getCreationDate();

  void setCreationDate(Date creationDate);

  User getUpdatedBy();

  void setUpdatedBy(User updatedBy);

  Date getLastUpdated();

  void setLastUpdated(Date lastUpdated);

  Boolean getArchived();

  void setArchived(Boolean archived);

  LibraryDesign getLibraryDesign();

  void setLibraryDesign(LibraryDesign rule);

  /**
   * This method should ONLY be used for load/save coordination between the Hibernate and old SQL DAOs. For all other purposes, use
   * getPrepKit().getKitDescriptorId()
   * 
   * @return the Kit Descriptor ID loaded by/for Hibernate
   */
  Long getHibernateKitDescriptorId();

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

}
