package uk.ac.bbsrc.tgac.miso.core.data;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import com.eaglegenomics.simlims.core.User;

import uk.ac.bbsrc.tgac.miso.core.data.impl.kit.KitDescriptor;

@JsonIgnoreProperties({ "sample" })
public interface SampleAdditionalInfo {

  Long getSampleAdditionalInfoId();

  void setSampleAdditionalInfoId(Long sampleAdditionalInfoId);

  Sample getSample();

  void setSample(Sample sample);

  SampleClass getSampleClass();

  void setSampleClass(SampleClass sampleClass);

  TissueOrigin getTissueOrigin();

  void setTissueOrigin(TissueOrigin tissueOrigin);

  TissueType getTissueType();

  void setTissueType(TissueType tissueType);

  Subproject getSubproject();

  void setSubproject(Subproject subproject);

  Integer getPassageNumber();

  void setPassageNumber(Integer passageNumber);

  Integer getTimesReceived();

  void setTimesReceived(Integer timesReceived);

  Integer getTubeNumber();

  void setTubeNumber(Integer tubeNumber);

  Double getConcentration();

  void setConcentration(Double concentration);

  Boolean getArchived();

  void setArchived(Boolean archived);

  User getCreatedBy();

  void setCreatedBy(User createdBy);

  Date getCreationDate();

  void setCreationDate(Date creationDate);

  User getUpdatedBy();

  void setUpdatedBy(User updatedBy);

  Date getLastUpdated();

  void setLastUpdated(Date lastUpdated);

  QcPassedDetail getQcPassedDetail();

  void setQcPassedDetail(QcPassedDetail qcPassedDetail);

  KitDescriptor getPrepKit();

  void setPrepKit(KitDescriptor prepKit);

  Lab getLab();

  void setLab(Lab lab);

  /**
   * This method should ONLY be used for load/save coordination between the Hibernate and old SQL DAOs. For all other purposes, use
   * getPrepKit().getKitDescriptorId()
   * 
   * @return the Kit Descriptor ID loaded by/for Hibernate
   */
  Long getHibernateKitDescriptorId();
  
  /**
   * @return the short tandem repeat QC status for this Sample
   */
  public StrStatus getStrStatus();
  
  /**
   * Sets the short tandem repeat QC status for this Sample
   * 
   * @param strStatus
   */
  public void setStrStatus(StrStatus strStatus);
  
  /**
   * Convenience method for setting the short tandem repeat QC status for this Sample
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