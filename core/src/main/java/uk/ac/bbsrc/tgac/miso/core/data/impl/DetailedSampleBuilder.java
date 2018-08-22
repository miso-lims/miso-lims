package uk.ac.bbsrc.tgac.miso.core.data.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.TreeSet;

import org.w3c.dom.Document;

import com.eaglegenomics.simlims.core.Note;
import com.eaglegenomics.simlims.core.SecurityProfile;
import com.eaglegenomics.simlims.core.User;
import com.google.common.collect.Lists;

import uk.ac.bbsrc.tgac.miso.core.data.AbstractSample;
import uk.ac.bbsrc.tgac.miso.core.data.Box;
import uk.ac.bbsrc.tgac.miso.core.data.ChangeLog;
import uk.ac.bbsrc.tgac.miso.core.data.ConcentrationUnit;
import uk.ac.bbsrc.tgac.miso.core.data.DetailedQcStatus;
import uk.ac.bbsrc.tgac.miso.core.data.DetailedSample;
import uk.ac.bbsrc.tgac.miso.core.data.Lab;
import uk.ac.bbsrc.tgac.miso.core.data.Library;
import uk.ac.bbsrc.tgac.miso.core.data.Project;
import uk.ac.bbsrc.tgac.miso.core.data.QcTarget;
import uk.ac.bbsrc.tgac.miso.core.data.Sample;
import uk.ac.bbsrc.tgac.miso.core.data.SampleAliquot;
import uk.ac.bbsrc.tgac.miso.core.data.SampleClass;
import uk.ac.bbsrc.tgac.miso.core.data.SampleIdentity;
import uk.ac.bbsrc.tgac.miso.core.data.SampleLCMTube;
import uk.ac.bbsrc.tgac.miso.core.data.SamplePurpose;
import uk.ac.bbsrc.tgac.miso.core.data.SampleQC;
import uk.ac.bbsrc.tgac.miso.core.data.SampleSlide;
import uk.ac.bbsrc.tgac.miso.core.data.SampleStock;
import uk.ac.bbsrc.tgac.miso.core.data.SampleTissue;
import uk.ac.bbsrc.tgac.miso.core.data.SampleTissueProcessing;
import uk.ac.bbsrc.tgac.miso.core.data.Stain;
import uk.ac.bbsrc.tgac.miso.core.data.Subproject;
import uk.ac.bbsrc.tgac.miso.core.data.TissueMaterial;
import uk.ac.bbsrc.tgac.miso.core.data.TissueOrigin;
import uk.ac.bbsrc.tgac.miso.core.data.TissueType;
import uk.ac.bbsrc.tgac.miso.core.data.VolumeUnit;
import uk.ac.bbsrc.tgac.miso.core.data.impl.boxposition.SampleBoxPosition;
import uk.ac.bbsrc.tgac.miso.core.data.impl.changelog.SampleChangeLog;
import uk.ac.bbsrc.tgac.miso.core.data.type.ConsentLevel;
import uk.ac.bbsrc.tgac.miso.core.data.type.StrStatus;

public class DetailedSampleBuilder
    implements DetailedSample, SampleAliquot, SampleStock, SampleTissue, SampleTissueProcessing, SampleSlide, SampleLCMTube, SampleIdentity {

  private static final long serialVersionUID = 1L;

  private static final List<String> CATEGORY_ORDER = Arrays.asList(SampleIdentity.CATEGORY_NAME, SampleTissue.CATEGORY_NAME,
      SampleTissueProcessing.CATEGORY_NAME, SampleStock.CATEGORY_NAME, SampleAliquot.CATEGORY_NAME);

  // Sample attributes
  private long sampleId = AbstractSample.UNSAVED_ID;
  private Project project;
  private SecurityProfile securityProfile = null;
  private String accession;
  private String name;
  private String description;
  private String scientificName;
  private String taxonIdentifier;
  private String sampleType;
  private Date receivedDate;
  private Boolean qcPassed;
  private String identificationBarcode;
  private String locationBarcode;
  private String alias;
  private User lastModifier;
  private Double volume;
  private VolumeUnit volumeUnits;
  private Double concentration;
  private ConcentrationUnit concentrationUnits;
  private boolean discarded = false;
  private boolean isSynthetic = false;
  private boolean nonStandardAlias = false;
  private final Collection<ChangeLog> changeLog = new ArrayList<>();
  private Collection<SampleQC> sampleQCs = new TreeSet<>();
  private Collection<Note> notes = new HashSet<>();

  // DetailedSample attributes
  private DetailedSample parent;
  private SampleClass sampleClass;
  private DetailedQcStatus detailedQcStatus;
  private String detailedQcStatusNote;
  private Subproject subproject;
  private Boolean archived = Boolean.FALSE;
  private String groupId;
  private String groupDescription;
  private Integer siblingNumber;
  private Long preMigrationId;
  private Long identityId;
  private Date creationDate;

  // Identity attributes
  private String externalName;
  private DonorSex donorSex = DonorSex.UNKNOWN;
  private ConsentLevel consentLevel;

  // TissueSample attributes
  private SampleClass tissueClass; // identifies a parent tissue class if this sample itself is not a tissue
  private TissueOrigin tissueOrigin;
  private TissueType tissueType;
  private String secondaryIdentifier;
  private Lab lab;
  private Integer passageNumber;
  private Integer timesReceived;
  private Integer tubeNumber;
  private TissueMaterial tissueMaterial;
  private String region;

  // AnalyteAliquot attributes
  private SamplePurpose samplePurpose;

  // SampleStock attributes
  private SampleClass stockClass;
  private StrStatus strStatus = StrStatus.NOT_SUBMITTED;
  private Boolean dnaseTreated;

  // TissueProcessingSample attributes
  // Slide
  private Integer slides;
  private Integer discards;
  private Integer thickness;
  private Stain stain;
  // LCM Tube
  private Integer slidesConsumed;

  public DetailedSampleBuilder() {
    this(null);
  }

  public DetailedSampleBuilder(User user) {
    if (user != null) {
      securityProfile = new SecurityProfile(user);
    }
  }

  @Override
  public long getId() {
    return sampleId;
  }

  @Override
  public void setId(long id) {
    this.sampleId = id;
  }

  @Override
  public Project getProject() {
    return project;
  }

  @Override
  public void setProject(Project project) {
    this.project = project;
  }

  @Override
  public Collection<SampleQC> getQCs() {
    return sampleQCs;
  }

  @Override
  public QcTarget getQcTarget() {
    return QcTarget.Sample;
  }

  @Override
  public void setQCs(Collection<SampleQC> qcs) {
    this.sampleQCs = qcs;
  }

  @Override
  public Collection<Note> getNotes() {
    return notes;
  }

  @Override
  public void addNote(Note note) {
    this.notes.add(note);
  }

  @Override
  public void setNotes(Collection<Note> notes) {
    this.notes = notes;
  }

  public Document getSubmissionDocument() {
    throw new UnsupportedOperationException("Method not implemented on builder");
  }

  public void setSubmissionDocument(Document submissionDocument) {
    throw new UnsupportedOperationException("Method not implemented on builder");
  }

  @Override
  public SecurityProfile getSecurityProfile() {
    return securityProfile;
  }

  @Override
  public void setSecurityProfile(SecurityProfile securityProfile) {
    this.securityProfile = securityProfile;
  }

  @Override
  public String getAccession() {
    return accession;
  }

  @Override
  public void setAccession(String accession) {
    this.accession = accession;
  }

  @Override
  public String getName() {
    return name;
  }

  @Override
  public void setName(String name) {
    this.name = name;
  }

  @Override
  public String getDescription() {
    return description;
  }

  @Override
  public void setDescription(String description) {
    this.description = description;
  }

  @Override
  public String getScientificName() {
    return scientificName;
  }

  @Override
  public void setScientificName(String scientificName) {
    this.scientificName = scientificName;
  }

  @Override
  public String getTaxonIdentifier() {
    return taxonIdentifier;
  }

  @Override
  public void setTaxonIdentifier(String taxonIdentifier) {
    this.taxonIdentifier = taxonIdentifier;
  }

  @Override
  public String getSampleType() {
    return sampleType;
  }

  @Override
  public void setSampleType(String sampleType) {
    this.sampleType = sampleType;
  }

  @Override
  public Date getReceivedDate() {
    return receivedDate;
  }

  @Override
  public void setReceivedDate(Date receivedDate) {
    this.receivedDate = receivedDate;
  }

  @Override
  public Boolean getQcPassed() {
    return qcPassed;
  }

  @Override
  public void setQcPassed(Boolean qcPassed) {
    this.qcPassed = qcPassed;
  }

  @Override
  public String getIdentificationBarcode() {
    return identificationBarcode;
  }

  @Override
  public void setIdentificationBarcode(String identificationBarcode) {
    this.identificationBarcode = identificationBarcode;
  }

  @Override
  public String getLocationBarcode() {
    return locationBarcode;
  }

  @Override
  public void setLocationBarcode(String locationBarcode) {
    this.locationBarcode = locationBarcode;
  }

  @Override
  public String getAlias() {
    return alias;
  }

  @Override
  public void setAlias(String alias) {
    this.alias = alias;
  }

  @Override
  public User getCreator() {
    return null;
  }

  @Override
  public void setCreator(User user) {
    throw new UnsupportedOperationException("Method not implemented on builder");
  }

  @Override
  public Date getCreationTime() {
    return null;
  }

  @Override
  public void setCreationTime(Date creationTime) {
    throw new UnsupportedOperationException("Method not implemented on builder");
  }

  @Override
  public void setLastModified(Date lastModified) {
    throw new UnsupportedOperationException("Method not implemented on builder");
  }

  @Override
  public User getLastModifier() {
    return lastModifier;
  }

  @Override
  public void setLastModifier(User lastModifier) {
    this.lastModifier = lastModifier;
  }

  @Override
  public DetailedSample getParent() {
    return parent;
  }

  @Override
  public void setParent(DetailedSample parent) {
    this.parent = parent;
  }

  @Override
  public List<DetailedSample> getChildren() {
    throw new UnsupportedOperationException("Method not implemented on builder");
  }

  @Override
  public void setChildren(List<DetailedSample> children) {
    throw new UnsupportedOperationException("Method not implemented on builder");
  }

  @Override
  public SampleClass getSampleClass() {
    return sampleClass;
  }

  @Override
  public void setSampleClass(SampleClass sampleClass) {
    this.sampleClass = sampleClass;
  }

  @Override
  public TissueOrigin getTissueOrigin() {
    return tissueOrigin;
  }

  @Override
  public void setTissueOrigin(TissueOrigin tissueOrigin) {
    this.tissueOrigin = tissueOrigin;
  }

  @Override
  public TissueType getTissueType() {
    return tissueType;
  }

  @Override
  public void setTissueType(TissueType tissueType) {
    this.tissueType = tissueType;
  }

  @Override
  public DetailedQcStatus getDetailedQcStatus() {
    return detailedQcStatus;
  }

  @Override
  public void setDetailedQcStatus(DetailedQcStatus detaildQcStatus) {
    this.detailedQcStatus = detaildQcStatus;
  }

  @Override
  public String getDetailedQcStatusNote() {
    return detailedQcStatusNote;
  }

  @Override
  public void setDetailedQcStatusNote(String detaildQcStatusNote) {
    this.detailedQcStatusNote = detaildQcStatusNote;
  }

  @Override
  public Subproject getSubproject() {
    return subproject;
  }

  @Override
  public void setSubproject(Subproject subproject) {
    this.subproject = subproject;
  }

  @Override
  public String getSecondaryIdentifier() {
    return secondaryIdentifier;
  }

  @Override
  public void setSecondaryIdentifier(String secondaryIdentifier) {
    this.secondaryIdentifier = secondaryIdentifier;
  }

  @Override
  public Lab getLab() {
    return lab;
  }

  @Override
  public void setLab(Lab lab) {
    this.lab = lab;
  }

  @Override
  public Integer getPassageNumber() {
    return passageNumber;
  }

  @Override
  public void setPassageNumber(Integer passageNumber) {
    this.passageNumber = passageNumber;
  }

  @Override
  public Integer getTimesReceived() {
    return timesReceived;
  }

  @Override
  public void setTimesReceived(Integer timesReceived) {
    this.timesReceived = timesReceived;
  }

  @Override
  public Integer getTubeNumber() {
    return tubeNumber;
  }

  @Override
  public void setTubeNumber(Integer tubeNumber) {
    this.tubeNumber = tubeNumber;
  }

  @Override
  public Double getConcentration() {
    return concentration;
  }

  @Override
  public void setConcentration(Double concentration) {
    this.concentration = concentration;
  }

  @Override
  public ConcentrationUnit getConcentrationUnits() {
    return concentrationUnits;
  }

  @Override
  public void setConcentrationUnits(ConcentrationUnit concentrationUnits) {
    this.concentrationUnits = concentrationUnits;
  }

  @Override
  public Boolean getArchived() {
    return archived;
  }

  @Override
  public void setArchived(Boolean archived) {
    this.archived = archived;
  }

  @Override
  public Integer getSiblingNumber() {
    return siblingNumber;
  }

  @Override
  public void setSiblingNumber(Integer siblingNumber) {
    this.siblingNumber = siblingNumber;
  }

  @Override
  public String getGroupId() {
    return groupId;
  }

  @Override
  public void setGroupId(String groupId) {
    this.groupId = groupId;
  }

  @Override
  public String getGroupDescription() {
    return groupDescription;
  }

  @Override
  public void setGroupDescription(String groupDescription) {
    this.groupDescription = groupDescription;
  }

  @Override
  public Optional<DetailedSample> getEffectiveGroupIdSample() {
    throw new UnsupportedOperationException("Method not implemented on builder");
  }

  @Override
  public String getExternalName() {
    return externalName;
  }

  @Override
  public void setExternalName(String externalName) {
    this.externalName = externalName;
  }

  @Override
  public DonorSex getDonorSex() {
    return donorSex;
  }

  @Override
  public void setDonorSex(DonorSex donorSex) {
    this.donorSex = donorSex;
  }

  @Override
  public ConsentLevel getConsentLevel() {
    return consentLevel;
  }

  @Override
  public void setConsentLevel(ConsentLevel consentLevel) {
    this.consentLevel = consentLevel;
  }

  @Override
  public Integer getSlides() {
    return slides;
  }

  @Override
  public void setSlides(Integer slides) {
    this.slides = slides;
  }

  @Override
  public Integer getSlidesRemaining() {
    return null;
  }

  @Override
  public Integer getDiscards() {
    return discards;
  }

  @Override
  public void setDiscards(Integer discards) {
    this.discards = discards;
  }

  @Override
  public Integer getThickness() {
    return thickness;
  }

  @Override
  public void setThickness(Integer thickness) {
    this.thickness = thickness;
  }

  @Override
  public Integer getSlidesConsumed() {
    return slidesConsumed;
  }

  @Override
  public void setSlidesConsumed(Integer slidesConsumed) {
    this.slidesConsumed = slidesConsumed;
  }

  @Override
  public SamplePurpose getSamplePurpose() {
    return samplePurpose;
  }

  @Override
  public void setSamplePurpose(SamplePurpose samplePurpose) {
    this.samplePurpose = samplePurpose;
  }

  @Override
  public TissueMaterial getTissueMaterial() {
    return tissueMaterial;
  }

  @Override
  public void setTissueMaterial(TissueMaterial tissueMaterial) {
    this.tissueMaterial = tissueMaterial;
  }

  public SampleClass getStockClass() {
    return stockClass;
  }

  public void setStockClass(SampleClass stockClass) {
    this.stockClass = stockClass;
  }

  @Override
  public StrStatus getStrStatus() {
    return strStatus;
  }

  @Override
  public void setStrStatus(StrStatus strStatus) {
    this.strStatus = strStatus;
  }

  @Override
  public String getRegion() {
    return region;
  }

  @Override
  public void setRegion(String region) {
    this.region = region;
  }

  @Override
  public Collection<ChangeLog> getChangeLog() {
    return changeLog;
  }

  @Override
  public int compareTo(Sample s) {
    return 0;
  }

  @Override
  public Box getBox() {
    return null;
  }

  @Override
  public void setDiscarded(boolean discarded) {
    this.discarded = discarded;
  }

  @Override
  public boolean isDiscarded() {
    return discarded;
  }

  @Override
  public Double getVolume() {
    return volume;
  }

  @Override
  public void setVolume(Double volume) {
    this.volume = volume;
  }

  @Override
  public VolumeUnit getVolumeUnits() {
    return volumeUnits;
  }

  @Override
  public void setVolumeUnits(VolumeUnit volumeUnits) {
    this.volumeUnits = volumeUnits;
  }

  @Override
  public String getBoxPosition() {
    return null;
  }

  @Override
  public String getLabelText() {
    return null;
  }

  @Override
  public void setDonorSex(String donorSex) {
    this.donorSex = DonorSex.valueOf(donorSex);
  }

  @Override
  public void setStrStatus(String strStatus) {
    this.strStatus = StrStatus.get(strStatus);
  }

  @Override
  public void addLibrary(Library library) {
    throw new UnsupportedOperationException("Method not implemented on builder");
  }

  @Override
  public Collection<Library> getLibraries() {
    return Lists.newArrayList();
  }

  public SampleClass getTissueClass() {
    return tissueClass;
  }

  public void setTissueClass(SampleClass tissueClass) {
    this.tissueClass = tissueClass;
  }

  @Override
  public Date getLastModified() {
    return new Date();
  }

  @Override
  public Boolean isSynthetic() {
    return isSynthetic;
  }

  @Override
  public void setSynthetic(Boolean isSynthetic) {
    this.isSynthetic = isSynthetic;
  }

  @Override
  public Boolean getDNAseTreated() {
    return dnaseTreated;
  }

  @Override
  public void setDNAseTreated(Boolean dnaseTreated) {
    this.dnaseTreated = dnaseTreated;
  }

  @Override
  public boolean hasNonStandardAlias() {
    return nonStandardAlias;
  }

  @Override
  public void setNonStandardAlias(boolean nonStandardAlias) {
    this.nonStandardAlias = nonStandardAlias;
  }

  @Override
  public Long getPreMigrationId() {
    return preMigrationId;
  }

  @Override
  public void setPreMigrationId(Long preMigrationId) {
    this.preMigrationId = preMigrationId;
  }

  @Override
  public Long getIdentityId() {
    return identityId;
  }

  @Override
  public void setIdentityId(Long identityId) {
    this.identityId = identityId;
  }

  @Override
  public Date getCreationDate() {
    return creationDate;
  }

  @Override
  public void setCreationDate(Date creationDate) {
    this.creationDate = creationDate;
  }

  public DetailedSample build() {
    if (sampleClass == null || sampleClass.getSampleCategory() == null) {
      throw new NullPointerException("Missing sample class or category");
    }
    DetailedSample sample = null;
    switch (sampleClass.getSampleCategory()) {
    case SampleIdentity.CATEGORY_NAME:
      SampleIdentity identity = buildIdentity();
      sample = identity;
      break;
    case SampleTissue.CATEGORY_NAME:
      sample = buildTissue();
      break;
    case SampleTissueProcessing.CATEGORY_NAME:
      if (sampleClass.getAlias().equals(SampleSlide.SAMPLE_CLASS_NAME)) {
        SampleSlide slide = new SampleSlideImpl();
        slide.setSlides(slides);
        slide.setDiscards(discards);
        slide.setThickness(thickness);
        slide.setStain(stain);
        sample = slide;
      } else if (sampleClass.getAlias().equals(SampleLCMTube.SAMPLE_CLASS_NAME)) {
        SampleLCMTube lcmTube = new SampleLCMTubeImpl();
        lcmTube.setSlidesConsumed(slidesConsumed);
        sample = lcmTube;
      } else {
        SampleTissueProcessing processing = new SampleTissueProcessingImpl();
        sample = processing;
      }
      break;
    case SampleStock.CATEGORY_NAME:
      SampleStock stock = buildStock();
      sample = stock;
      break;
    case SampleAliquot.CATEGORY_NAME:
      SampleAliquot aliquot = new SampleAliquotImpl();
      aliquot.setSamplePurpose(samplePurpose);
      sample = aliquot;
      break;
    default:
      throw new IllegalArgumentException("Unknown sample category: " + sampleClass.getSampleCategory());
    }

    if (parent != null) {
      sample.setParent(parent);
    } else {
      DetailedSample parent = null;
      int categoryIndex = CATEGORY_ORDER.indexOf(sampleClass.getSampleCategory());
      if (categoryIndex < 0) {
        throw new IllegalArgumentException("Sample has no parent and cannot infer order from sample category.");
      }
      if (categoryIndex > 0) {
        if (identityId == null && externalName != null) {
          parent = buildIdentity();
        } else if (identityId != null) {
          parent = new SampleIdentityImpl();
          parent.setId(identityId);
        }
      }
      if (categoryIndex > 1 && tissueClass != null) {
        SampleTissue tissue = buildTissue();
        tissue.setParent(parent);
        tissue.setSampleClass(tissueClass);
        parent = tissue;
      }
      if (categoryIndex > 3 && stockClass != null) {
        SampleStock stock = buildStock();
        stock.setParent(parent);
        stock.setSampleClass(stockClass);
        parent = stock;
      }
      sample.setParent(parent);
    }

    sample.setId(sampleId);
    sample.setProject(project);
    sample.setSecurityProfile(securityProfile);
    sample.setAccession(accession);
    sample.setName(name);
    sample.setDescription(description);
    sample.setScientificName(scientificName);
    sample.setTaxonIdentifier(taxonIdentifier);
    sample.setSampleType(sampleType);
    sample.setReceivedDate(receivedDate);
    sample.setQcPassed(qcPassed);
    sample.setIdentificationBarcode(identificationBarcode);
    sample.setLocationBarcode(locationBarcode);
    sample.setAlias(alias);
    sample.setLastModifier(lastModifier);
    sample.setVolume(volume);
    sample.setVolumeUnits(volumeUnits);
    sample.setDiscarded(discarded);
    sample.getChangeLog().addAll(changeLog);
    sample.setNotes(notes);

    sample.setSampleClass(sampleClass);
    sample.setDetailedQcStatus(detailedQcStatus);
    sample.setDetailedQcStatusNote(detailedQcStatusNote);
    sample.setSubproject(subproject);
    sample.setArchived(archived);
    sample.setGroupId(groupId);
    sample.setGroupDescription(groupDescription);
    sample.setSynthetic(isSynthetic);
    sample.setConcentration(concentration);
    sample.setConcentrationUnits(concentrationUnits);
    sample.setNonStandardAlias(nonStandardAlias);
    sample.setSiblingNumber(siblingNumber);
    sample.setPreMigrationId(preMigrationId);
    sample.setQCs(sampleQCs);
    sample.setCreationDate(creationDate);

    return sample;
  }

  private SampleIdentity buildIdentity() {
    if (externalName == null) {
      throw new NullPointerException("Missing externalName");
    }
    SampleIdentity identity = new SampleIdentityImpl();
    identity.setExternalName(externalName);
    identity.setDonorSex(donorSex);
    return identity;
  }

  private SampleStock buildStock() {
    SampleStock stock = new SampleStockImpl();
    stock.setStrStatus(strStatus);
    stock.setDNAseTreated(dnaseTreated);
    return stock;
  }

  private SampleTissue buildTissue() {
    SampleTissue tissue = new SampleTissueImpl();
    tissue.setTimesReceived(timesReceived);
    tissue.setTubeNumber(tubeNumber);
    tissue.setPassageNumber(passageNumber);
    tissue.setTissueType(tissueType);
    tissue.setTissueOrigin(tissueOrigin);
    tissue.setSecondaryIdentifier(secondaryIdentifier);
    tissue.setLab(lab);
    tissue.setTissueMaterial(tissueMaterial);
    tissue.setRegion(region);
    return tissue;
  }

  @Override
  public ChangeLog createChangeLog(String summary, String columnsChanged, User user) {
    SampleChangeLog changeLog = new SampleChangeLog();
    changeLog.setSample(this);
    changeLog.setSummary(summary);
    changeLog.setColumnsChanged(columnsChanged);
    changeLog.setUser(user);
    return changeLog;
  }

  @Override
  public EntityType getEntityType() {
    throw new UnsupportedOperationException("Temporary builder object should not be consumed as a Boxable");
  }

  @Override
  public Stain getStain() {
    return stain;
  }

  @Override
  public void setStain(Stain stain) {
    this.stain = stain;
  }

  @Override
  public Date getBarcodeDate() {
    return null;
  }

  @Override
  public String getBarcodeExtraInfo() {
    return getDescription();
  }

  @Override
  public void setBoxPosition(SampleBoxPosition boxPosition) {
    throw new UnsupportedOperationException("Method not implemented on builder");
  }

  @Override
  public void removeFromBox() {
    throw new UnsupportedOperationException("Method not implemented on builder");
  }

  @Override
  public String getBarcodeSizeInfo() {
    return "HOW DID YOU EVER PRINT THIS?!?";
  }

  @Override
  public String getDeleteType() {
    throw new UnsupportedOperationException("Cannot delete non-persisted builder object");
  }

  @Override
  public String getDeleteDescription() {
    throw new UnsupportedOperationException("Cannot delete non-persisted builder object");
  }

  @Override
  public SecurityProfile getDeletionSecurityProfile() {
    throw new UnsupportedOperationException("Cannot delete non-persisted builder object");
  }

  @Override
  public boolean isSaved() {
    return false;
  }

}
