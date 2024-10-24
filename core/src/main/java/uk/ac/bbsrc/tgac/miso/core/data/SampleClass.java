package uk.ac.bbsrc.tgac.miso.core.data;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import uk.ac.bbsrc.tgac.miso.core.util.MapBuilder;

public interface SampleClass extends Deletable, Serializable, Timestamped {

  public static final List<String> CATEGORIES = Collections.unmodifiableList(Arrays.asList(SampleIdentity.CATEGORY_NAME,
      SampleTissue.CATEGORY_NAME, SampleTissueProcessing.CATEGORY_NAME, SampleStock.CATEGORY_NAME,
      SampleAliquot.CATEGORY_NAME));

  public static final Map<String, List<String>> SUBCATEGORIES = new MapBuilder<String, List<String>>()
      .put(SampleIdentity.CATEGORY_NAME, SampleIdentity.SUBCATEGORIES)
      .put(SampleTissue.CATEGORY_NAME, SampleTissue.SUBCATEGORIES)
      .put(SampleTissueProcessing.CATEGORY_NAME, SampleTissueProcessing.SUBCATEGORIES)
      .put(SampleStock.CATEGORY_NAME, SampleStock.SUBCATEGORIES)
      .put(SampleAliquot.CATEGORY_NAME, SampleAliquot.SUBCATEGORIES)
      .build();

  public String getAlias();

  public void setAlias(String alias);

  public String getSampleCategory();

  public void setSampleCategory(String sampleCategory);

  public String getSampleSubcategory();

  public void setSampleSubcategory(String sampleCategory);

  /**
   * @return the class identifier used in Sample alias generation
   */
  public String getSuffix();

  /**
   * Sets the class identifier used in Sample alias generation
   * 
   * @param suffix
   */
  public void setSuffix(String suffix);

  public String getV2NamingCode();

  public void setV2NamingCode(String v2NamingCode);

  public boolean isArchived();

  public void setArchived(boolean archived);

  public boolean isDirectCreationAllowed();

  public void setDirectCreationAllowed(boolean directCreationAllowed);

  public Set<SampleValidRelationship> getParentRelationships();

  public Set<SampleValidRelationship> getChildRelationships();

  public SampleType getDefaultSampleType();

  public void setDefaultSampleType(SampleType sampleType);

}
