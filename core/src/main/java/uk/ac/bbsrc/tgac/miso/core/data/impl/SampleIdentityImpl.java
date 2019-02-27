package uk.ac.bbsrc.tgac.miso.core.data.impl;

import static com.google.common.base.Preconditions.checkArgument;

import java.util.Set;
import java.util.stream.Collectors;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;

import uk.ac.bbsrc.tgac.miso.core.data.Project;
import uk.ac.bbsrc.tgac.miso.core.data.Sample;
import uk.ac.bbsrc.tgac.miso.core.data.SampleClass;
import uk.ac.bbsrc.tgac.miso.core.data.SampleIdentity;
import uk.ac.bbsrc.tgac.miso.core.data.type.ConsentLevel;
import uk.ac.bbsrc.tgac.miso.core.util.LimsUtils;

@Entity
@Table(name = "Identity")
public class SampleIdentityImpl extends DetailedSampleImpl implements SampleIdentity {

  private static final long serialVersionUID = 1L;

  @Column(nullable = false)
  private String externalName;

  @Enumerated(EnumType.STRING)
  private DonorSex donorSex = DonorSex.UNKNOWN;

  @Enumerated(EnumType.STRING)
  private ConsentLevel consentLevel;

  @Override
  public String getExternalName() {
    return externalName;
  }

  @Override
  public void setExternalName(String externalName) {
    StringBuilder sb = new StringBuilder();
    boolean first = true;
    for (String part : externalName.split(",")) {
      if (first) {
        first = false;
      } else {
        sb.append(",");
      }
      sb.append(part.trim().replaceAll("\\s+", " "));
    }
    this.externalName = sb.toString();
  }

  /**
   * Convenience method to take external name strings and split them at commas and trim excess whitespace
   * 
   * @param externalNameString
   * @return Set<String> external name(s) set
   */
  public static Set<String> getSetFromString(String externalNameString) {
    return COMMA.splitAsStream(externalNameString).map(part -> part.trim().replaceAll("\\s+", " ")).collect(Collectors.toSet());
  }

  @Override
  public DonorSex getDonorSex() {
    return donorSex;
  }

  @Override
  public void setDonorSex(DonorSex donorSex) {
    this.donorSex = (donorSex == null) ? DonorSex.UNKNOWN : donorSex;
  }

  @Override
  public void setDonorSex(String donorSex) {
    this.donorSex = DonorSex.get(donorSex);
  }

  public static class IdentityBuilder {

    private String name;
    private String alias;
    private SampleClass rootSampleClass;
    private Double volume;
    private String description;
    private String sampleType;
    private Project project;
    private String scientificName;

    private String externalName;
    private DonorSex donorSex;
    private ConsentLevel consentLevel;

    public IdentityBuilder rootSampleClass(SampleClass rootSampleClass) {
      this.rootSampleClass = rootSampleClass;
      return this;
    }

    public IdentityBuilder name(String name) {
      this.name = name;
      return this;
    }

    public IdentityBuilder alias(String alias) {
      this.alias = alias;
      return this;
    }

    public IdentityBuilder volume(Double volume) {
      this.volume = volume;
      return this;
    }

    public IdentityBuilder description(String description) {
      this.description = description;
      return this;
    }

    public IdentityBuilder sampleType(String sampleType) {
      this.sampleType = sampleType;
      return this;
    }

    public IdentityBuilder project(Project project) {
      this.project = project;
      return this;
    }

    public IdentityBuilder scientificName(String scientificName) {
      this.scientificName = scientificName;
      return this;
    }

    public IdentityBuilder externalName(String externalName) {
      this.externalName = externalName;
      return this;
    }

    public IdentityBuilder donorSex(DonorSex donorSex) {
      this.donorSex = donorSex;
      return this;
    }

    public IdentityBuilder consentLevel(ConsentLevel consentLevel) {
      this.consentLevel = consentLevel;
      return this;
    }

    public Sample build() {
      checkArgument(project != null, "A Project must be provided to create a Sample.");
      checkArgument(!LimsUtils.isStringEmptyOrNull(sampleType), "Must provide a sampleType to create a Sample");
      checkArgument(!LimsUtils.isStringEmptyOrNull(scientificName), "Must provide a scientificName to create a Sample");
      checkArgument(rootSampleClass != null, "A root SampleClass must be provided to create an Identity Sample.");
      log.debug("Create an Identity Sample.");

      SampleIdentityImpl i = new SampleIdentityImpl();
      i.setName(name);
      i.setAlias(alias);
      i.setSampleClass(rootSampleClass);
      i.setVolume(volume);
      i.setDescription(description);
      i.setSampleType(sampleType);
      i.setProject(project);
      i.setScientificName(scientificName);
      i.setExternalName(externalName);
      i.setDonorSex(donorSex);
      i.setConsentLevel(consentLevel);

      return i;
    }

  }

  @Override
  public ConsentLevel getConsentLevel() {
    return consentLevel;
  }

  @Override
  public void setConsentLevel(ConsentLevel consentLevel) {
    this.consentLevel = consentLevel;
  }

}
