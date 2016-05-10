package uk.ac.bbsrc.tgac.miso.core.data;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import com.eaglegenomics.simlims.core.User;

@JsonIgnoreProperties({ "sample" })
public interface Identity {
  
  public static final String CATEGORY_NAME = "Identity";

  Long getSampleId();

  void setSampleId(Long sampleId);

  Sample getSample();

  void setSample(Sample sample);

  String getInternalName();

  void setInternalName(String internalName);

  String getExternalName();

  void setExternalName(String externalName);

  User getCreatedBy();

  void setCreatedBy(User createdBy);

  Date getCreationDate();

  void setCreationDate(Date creationDate);

  User getUpdatedBy();

  void setUpdatedBy(User updatedBy);

  Date getLastUpdated();

  void setLastUpdated(Date lastUpdated);

  /**
   * @return the sex for this donor
   */
  public DonorSex getDonorSex();

  /**
   * Sets the sex for this donor
   * 
   * @param donorSex
   */
  public void setDonorSex(DonorSex donorSex);

  /**
   * Convenience method for setting the sex for this donor
   * 
   * @param donorSex must match an existing {@link DonorSex} label
   * @throws IllegalArgumentException if no DonorSex with the requested label exists
   */
  public void setDonorSex(String donorSex);

  /**
   * Possible sexes for donors
   */
  public static enum DonorSex {

    MALE("Male"),
    FEMALE("Female"),
    OTHER("Other"),
    UNSPECIFIED("Unspecified"),
    UNKNOWN("Unknown");

    private static final Map<String, DonorSex> lookup = new HashMap<>();

    static {
      for (DonorSex ds : DonorSex.values()) {
        lookup.put(ds.getLabel(), ds);
      }
    }

    private final String label;

    private DonorSex(String label) {
      this.label = label;
    }

    public String getLabel() {
      return label;
    }

    /**
     * Finds a DonorSex value by its label
     * 
     * @throws IllegalArgumentException if no DonorSex with the requested label exists
     */
    public static DonorSex get(String label) {
      if (!lookup.containsKey(label)) throw new IllegalArgumentException("Invalid Donor Sex: " + label);
      return lookup.get(label);
    }

    public static List<String> getLabels() {
      return new ArrayList<String>(lookup.keySet());
    }
  }

}