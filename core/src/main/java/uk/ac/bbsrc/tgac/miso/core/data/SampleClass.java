package uk.ac.bbsrc.tgac.miso.core.data;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import com.eaglegenomics.simlims.core.User;

public interface SampleClass extends Serializable {

  List<String> CATEGORIES = Collections.unmodifiableList(Arrays.asList(SampleIdentity.CATEGORY_NAME, SampleTissue.CATEGORY_NAME,
  SampleTissueProcessing.CATEGORY_NAME, SampleStock.CATEGORY_NAME, SampleAliquot.CATEGORY_NAME));

  Long getId();

  void setId(Long sampleClassId);

  String getAlias();

  void setAlias(String alias);

  User getCreatedBy();

  void setCreatedBy(User createdBy);

  Date getCreationDate();

  void setCreationDate(Date creationDate);

  User getUpdatedBy();

  void setUpdatedBy(User updatedBy);

  Date getLastUpdated();

  void setLastUpdated(Date lastUpdated);

  String getSampleCategory();

  void setSampleCategory(String sampleCategory);

  /**
   * @return the class identifier used in Sample alias generation
   */
  String getSuffix();

  /**
   * Sets the class identifier used in Sample alias generation
   * 
   * @param suffix
   */
  void setSuffix(String suffix);

  Boolean getDNAseTreatable();

  void setDNAseTreatable(Boolean treatable);

  default boolean hasPathToIdentity(Collection<SampleValidRelationship> relationships) {
    if (getSampleCategory().equals(SampleIdentity.CATEGORY_NAME)) {
      return true;
    }
    return relationships.stream()
        .filter(relationship -> !relationship.getArchived() && relationship.getChild().getId() == getId()
            && !relationship.getParent().getSampleCategory().equals(getSampleCategory()))
        .anyMatch(relationship -> relationship.getParent().hasPathToIdentity(relationships));
  }
}