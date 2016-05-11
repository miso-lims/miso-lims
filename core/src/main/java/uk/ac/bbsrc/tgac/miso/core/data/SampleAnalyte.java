package uk.ac.bbsrc.tgac.miso.core.data;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import com.eaglegenomics.simlims.core.User;

@JsonIgnoreProperties({ "sample" })
public interface SampleAnalyte {
  
  public static String CATEGORY_NAME = "Analyte";

  Long getId();

  void setId(Long sampleId);

  Sample getSample();

  void setSample(Sample sample);

  SamplePurpose getSamplePurpose();

  void setSamplePurpose(SamplePurpose samplePurpose);

  /**
   * Gets the Group ID string of this sample analyte.
   * @return Long groupId
   */
  Long getGroupId();

  /**
   * Sets the Group ID string of this sample analyte.
   * @param Long groupId
   */
  void setGroupId(Long groupId);

  /**
   * Gets the Group Description string of this sample analyte.
   * @return String groupDescription
   */
  String getGroupDescription();

  /**
   * Sets the Group Description string of this sample analyte.
   * @param String groupDescription
   */
  void setGroupDescription(String groupDescription);

  TissueMaterial getTissueMaterial();

  void setTissueMaterial(TissueMaterial tissueMaterial);

  String getRegion();

  void setRegion(String region);

  String getTubeId();

  void setTubeId(String tubeId);

  User getCreatedBy();

  void setCreatedBy(User createdBy);

  Date getCreationDate();

  void setCreationDate(Date creationDate);

  User getUpdatedBy();

  void setUpdatedBy(User updatedBy);

  Date getLastUpdated();

  void setLastUpdated(Date lastUpdated);
  
  /**
   * @return the short tandem repeat QC status for this SampleAnalyte
   */
  public StrStatus getStrStatus();
  
  /**
   * Sets the short tandem repeat QC status for this SampleAnalyte
   * 
   * @param strStatus
   */
  public void setStrStatus(StrStatus strStatus);
  
  /**
   * Convenience method for setting the short tandem repeat QC status for this SampleAnalyte
   * 
   * @param strStatus must match an existing {@link StrStatus} label
   * @throws IllegalArgumentException if no StrStatus with the requested label exists
   */
  public void setStrStatus(String strStatus);
  
  /**
   * Possible status options for Short Tandem Repeat QC
   */
  public static enum StrStatus {
    
    NOT_SUBMITTED("Not Submitted"),
    SUBMITTED("Submitted"),
    PASS("Pass"),
    FAIL("Fail");
    
    private static final Map<String, StrStatus> lookup = new HashMap<>();
    
    static {
      for (StrStatus sr : StrStatus.values()) {
        lookup.put(sr.getLabel(), sr);
      }
    }
    
    private final String label;
    
    private StrStatus(String label) {
      this.label = label;
    }
    
    public String getLabel() {
      return label;
    }
    
    /**
     * Finds a StrStatus value by its label
     * 
     * @throws IllegalArgumentException if no StrStatus with the requested label exists
     */
    public static StrStatus get(String label) {
      if (!lookup.containsKey(label)) throw new IllegalArgumentException("Invalid STR Status: " + label);
      return lookup.get(label);
    }
    
    public static List<String> getLabels() {
      return new ArrayList<String>(lookup.keySet());
    }
    
  }

}