package uk.ac.bbsrc.tgac.miso.core.data.impl;

import static com.google.common.base.Preconditions.checkArgument;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.Table;

import com.eaglegenomics.simlims.core.User;

import uk.ac.bbsrc.tgac.miso.core.data.Identity;
import uk.ac.bbsrc.tgac.miso.core.data.Project;
import uk.ac.bbsrc.tgac.miso.core.data.Sample;
import uk.ac.bbsrc.tgac.miso.core.data.SampleClass;
import uk.ac.bbsrc.tgac.miso.core.util.LimsUtils;

@Entity
@Table(name = "Identity")
public class IdentityImpl extends DetailedSampleImpl implements Identity {

  private static final long serialVersionUID = 1L;

  @ElementCollection
  @CollectionTable(name = "Identity_ExternalName", joinColumns = @JoinColumn(name = "sampleId"))
  @Column(name = "externalName", nullable = false)
  private Set<String> externalName;

  @Enumerated(EnumType.STRING)
  private DonorSex donorSex = DonorSex.UNKNOWN;

  @Override
  public String getExternalName() {
    StringBuilder sb = new StringBuilder();
    boolean first = true;
    for (String part : externalName) {
      if (first) {
        first = false;
      } else {
        sb.append(",");
      }
      sb.append(part);
    }
    return sb.toString();
  }

  @Override
  public Set<String> getExternalNameSet() {
    return externalName;
  }

  /**
   * 
   */
  @Override
  public void setExternalName(String externalName) {
    Set<String> externalNames = new HashSet<String>();
    for (String part : externalName.split(",")) {
      externalNames.add(part.trim().replaceAll("\\s+", " "));
    }
    this.externalName = externalNames;
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

    /** User is needed to create a SecurityProfile. */
    private User user;

    private String externalName;
    private DonorSex donorSex;

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

    public IdentityBuilder user(User user) {
      this.user = user;
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

    public Sample build() {
      checkArgument(project != null, "A Project must be provided to create a Sample.");
      checkArgument(!LimsUtils.isStringEmptyOrNull(description), "Must provide a description to create a Sample");
      checkArgument(!LimsUtils.isStringEmptyOrNull(sampleType), "Must provide a sampleType to create a Sample");
      checkArgument(!LimsUtils.isStringEmptyOrNull(scientificName), "Must provide a scientificName to create a Sample");
      checkArgument(rootSampleClass != null, "A root SampleClass must be provided to create an Identity Sample.");
      log.debug("Create an Identity Sample.");

      IdentityImpl i = new IdentityImpl();
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
      i.inheritPermissions(project);

      return i;
    }

  }

}
