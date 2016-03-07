package uk.ac.bbsrc.tgac.miso.core.data;

import java.util.Date;

import com.eaglegenomics.simlims.core.User;

import uk.ac.bbsrc.tgac.miso.core.data.impl.kit.KitDescriptor;

public interface LibraryAdditionalInfo {
  
  Long getId();

  void setId(Long id);
  
  Library getLibrary();
  
  void setLibrary(Library library);
  
  TissueOrigin getTissueOrigin();
  
  void setTissueOrigin(TissueOrigin tissueOrigin);
  
  TissueType getTissueType();
  
  void setTissueType(TissueType tissueType);
  
  SampleGroupId getSampleGroupId();
  
  void setSampleGroupId(SampleGroupId sampleGroupId);
  
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
  
  /**
   * This method should ONLY be used for load/save coordination between the Hibernate and old SQL DAOs. 
   * For all other purposes, use getPrepKit().getKitDescriptorId()
   * 
   * @return the Kit Descriptor ID loaded by/for Hibernate
   */
  Long getHibernateKitDescriptorId();
  
}
