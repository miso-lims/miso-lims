package uk.ac.bbsrc.tgac.miso.webapp.integrationtest.page;

import static org.openqa.selenium.support.ui.ExpectedConditions.titleContains;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.Select;

public class SamplePage extends HeaderFooterPage {

  @FindBy(id = "sampleId")
  private WebElement idLabel;
  @FindBy(id = "name")
  private WebElement nameLabel;
  @FindBy(id = "alias")
  private WebElement aliasLabel;
  @FindBy(id = "project")
  private WebElement projectDropdownForCreateLabel;
  @FindBy(id = "projectName")
  private WebElement projectNameForEditLabel;
  @FindBy(id = "description")
  private WebElement descriptionLabel;
  @FindBy(id = "receiveddatepicker")
  private WebElement receivedDateInput;
  @FindBy(id = "scientificName")
  private WebElement scientificNameLabel;
  @FindBy(id = "sampleTypes")
  private WebElement sampleTypeLabel;
  @FindBy(id = "detailedQcStatus")
  private WebElement qcStatusLabel;
  @FindBy(id = "qcStatusNote")
  private WebElement qcNoteLabel;
  @FindBy(id = "volume")
  private WebElement volumeLabel;
  @FindBy(id = "discarded")
  private WebElement discardedLabel;
  @FindBy(id = "identityId")
  private WebElement identityIdLabel;
  @FindBy(id = "externalName")
  private WebElement externalNameLabel;
  @FindBy(id = "donorSex")
  private WebElement donorSexLabel;
  @FindBy(id = "parentAlias")
  private WebElement parentAliasLabel;
  @FindBy(id = "sampleClass")
  private WebElement sampleClassLabel;
  @FindBy(id = "sampleClassAlias")
  private WebElement sampleClassAliasLabel;
  @FindBy(id = "subProject")
  private WebElement subProjectLabel;
  @FindBy(id = "groupId")
  private WebElement groupIdLabel;
  @FindBy(id = "groupDescription")
  private WebElement groupDescriptionLabel;
  @FindBy(id = "concentration")
  private WebElement concentrationLabel;
  @FindBy(id = "tissueClass")
  private WebElement tissueClassLabel;
  @FindBy(id = "tissueOrigin")
  private WebElement tissueOriginLabel;
  @FindBy(id = "tissueType")
  private WebElement tissueTypeLabel;
  @FindBy(id = "passageNumber")
  private WebElement passageNumberLabel;
  @FindBy(id = "timesReceived")
  private WebElement timesReceivedLabel;
  @FindBy(id = "tubeNumber")
  private WebElement tubeNumberLabel;
  @FindBy(id = "externalInstituteIdentifier")
  private WebElement externalInstituteIdentifierLabel;
  @FindBy(id = "lab")
  private WebElement labLabel;
  @FindBy(id = "tissueMaterial")
  private WebElement tissueMaterialLabel;
  @FindBy(id = "region")
  private WebElement regionLabel;
  @FindBy(id = "slidesRemaining")
  private WebElement slidesRemainingLabel;
  @FindBy(id = "slides")
  private WebElement slidesLabel;
  @FindBy(id = "discards")
  private WebElement discardsLabel;
  @FindBy(id = "thickness")
  private WebElement thicknessLabel;
  @FindBy(id = "stain")
  private WebElement stainLabel;
  @FindBy(id = "slidesConsumed")
  private WebElement slidesConsumedLabel;
  @FindBy(id = "strStatus")
  private WebElement strStatusLabel;
  @FindBy(id = "DNAseTreated")
  private WebElement dnaseTreatedLabel;
  @FindBy(id = "samplePurpose")
  private WebElement samplePurposeLabel;

  public SamplePage(WebDriver driver) {
    super(driver);
    PageFactory.initElements(driver, this);
    waitWithTimeout().until(titleContains("Sample "));
  }

  public static SamplePage get(WebDriver driver, String baseUrl, long sampleId) {
    driver.get(baseUrl + "miso/sample/" + sampleId);
    return new SamplePage(driver);
  }

  public String getId() {
    return idLabel.getText();
  }

  public String getName() {
    return nameLabel.getText();
  }

  public String getAlias() {
    return aliasLabel.getAttribute("value");
  }

  public void setAlias(String alias) {
    setText(alias, aliasLabel);
  }

  public String getProjectDropdownForCreateLabel() {
    return projectDropdownForCreateLabel.getText();
  }

  public void setProjectDropdownForCreateLabel(String project) {
    setDropdown(project, projectDropdownForCreateLabel);
  }

  public void setProjectForCreate(String project) {
    if ("select".equals(projectDropdownForCreateLabel.getTagName())) {
      setDropdown(project, projectDropdownForCreateLabel);
    }
  }

  public String getProjectNameForEditLabel() {
    return projectNameForEditLabel.getText();
  }

  public String getDescription() {
    return descriptionLabel.getAttribute("value");
  }

  public void setDescription(String description) {
    setText(description, descriptionLabel);
  }

  public String getReceivedDate() {
    return receivedDateInput.getAttribute("value");
  }

  public void setReceivedDate(String receivedDate) {
    setText(receivedDate, receivedDateInput);
  }

  public String getScientificName() {
    return scientificNameLabel.getAttribute("value");
  }

  public void setScientificName(String scientificName) {
    setText(scientificName, scientificNameLabel);
  }

  public String getSampleType() {
    return getSelectedDropdownText(sampleTypeLabel);
  }

  public void setSampleType(String sampleType) {
    setDropdown(sampleType, sampleTypeLabel);
  }

  public String getQcStatus() {
    return getSelectedDropdownText(qcStatusLabel);
  }

  public void setQcStatus(String qcStatus) {
    setDropdown(qcStatus, qcStatusLabel);
  }

  public String getQcNote() {
    return qcNoteLabel.getAttribute("value");
  }

  public void setQcNote(String qcNote) {
    setText(qcNote, qcNoteLabel);
  }

  public String getVolume() {
    return volumeLabel.getAttribute("value");
  }

  public void setVolume(String volume) {
    setText(volume, volumeLabel);
  }

  public boolean getDiscarded() {
    return Boolean.valueOf(discardedLabel.getAttribute("value"));
  }

  public void setDiscarded(boolean discarded) {
    setCheckbox(discarded, discardedLabel);
  }

  public String getIdentityId() {
    return identityIdLabel.getAttribute("value");
  }

  public void setIdentityId(String identityId) {
    setText(identityId, identityIdLabel);
  }

  public String getExternalName() {
    if ("input".equals(externalNameLabel.getTagName())) {
      return externalNameLabel.getAttribute("value");
    } else {
      return externalNameLabel.getText();
    }
  }

  public void setExternalName(String externalName) {
    setText(externalName, externalNameLabel);
  }

  public String getDonorSex() {
    if (donorSexLabel instanceof Select) {
      return getSelectedDropdownText(donorSexLabel);
    } else {
      return donorSexLabel.getText();
    }
  }

  public void setDonorSex(String donorSex) {
    setDropdown(donorSex, donorSexLabel);
  }

  public String getParentAlias() {
    return parentAliasLabel.getText();
  }

  public String getSampleClass() {
    if (sampleClassLabel instanceof Select) {
      return getSelectedDropdownText(sampleClassLabel);
    } else {
      return sampleClassAliasLabel.getText();
    }
  }

  public void setSampleClass(String sampleClass) {
    Select sampleClassSelect = (Select) sampleClassLabel;
    WebElement matches = sampleClassSelect.getOptions().stream().filter(option -> sampleClass.equals(option.getText())).findAny()
        .orElse(null);
    if (matches == null) throw new IllegalArgumentException("Sample class " + sampleClass + " not available for sample " + getId());

    setDropdown(sampleClass, sampleClassLabel);
  }

  public String getSubProject() {
    return getSelectedDropdownText(subProjectLabel);
  }

  public void setSubProject(String subProject) {
    setDropdown(subProject, this.subProjectLabel);
  }

  public String getGroupId() {
    return groupIdLabel.getAttribute("value");
  }

  public void setGroupId(String groupId) {
    setText(groupId, groupIdLabel);
  }

  public String getGroupDescription() {
    return groupDescriptionLabel.getAttribute("value");
  }

  public void setGroupDescription(String groupDescription) {
    setText(groupDescription, groupDescriptionLabel);
  }

  public String getConcentration() {
    return concentrationLabel.getAttribute("value");
  }

  public void setConcentration(String concentration) {
    setText(concentration, concentrationLabel);
  }

  public String getTissueClass() {
    if (tissueClassLabel instanceof Select) {
      return getSelectedDropdownText(tissueClassLabel);
    } else {
      return tissueClassLabel.getText();
    }
  }

  public void setTissueClass(String tissueClass) {
    setDropdown(tissueClass, tissueClassLabel);
  }

  public String getTissueOrigin() {
    return getSelectedDropdownText(tissueOriginLabel);
  }

  public void setTissueOrigin(String tissueOrigin) {
    setDropdown(tissueOrigin, tissueOriginLabel);
  }

  public String getTissueType() {
    return getSelectedDropdownText(tissueTypeLabel);
  }

  public void setTissueType(String tissueType) {
    setDropdown(tissueType, tissueTypeLabel);
  }

  public String getPassageNumber() {
    if ("input".equals(passageNumberLabel.getTagName())) {
      return passageNumberLabel.getAttribute("value");
    } else {
      return passageNumberLabel.getText();
    }
  }

  public void setPassageNumber(String passageNumber) {
    setText(passageNumber, passageNumberLabel);
  }

  public String getTimesReceived() {
    if ("input".equals(timesReceivedLabel.getTagName())) {
      return timesReceivedLabel.getAttribute("value");
    } else {
      return timesReceivedLabel.getText();
    }
  }

  public void setTimesReceived(String timesReceived) {
    setText(timesReceived, timesReceivedLabel);
  }

  public String getTubeNumber() {
    if ("input".equals(tubeNumberLabel.getTagName())) {
      return tubeNumberLabel.getAttribute("value");
    } else {
      return tubeNumberLabel.getText();
    }
  }

  public void setTubeNumber(String tubeNumber) {
    setText(tubeNumber, tubeNumberLabel);
  }

  public String getTissueMaterial() {
    return getSelectedDropdownText(tissueMaterialLabel);
  }

  public void setTissueMaterial(String tissueMaterial) {
    setDropdown(tissueMaterial, tissueMaterialLabel);
  }

  public String getRegion() {
    return regionLabel.getAttribute("value");
  }

  public void setRegion(String region) {
    setText(region, regionLabel);
  }

  public String getExternalInstituteIdentifier() {
    return externalInstituteIdentifierLabel.getAttribute("value");
  }

  public void setExternalInstituteIdentifier(String externalInstituteIdentifier) {
    setText(externalInstituteIdentifier, externalInstituteIdentifierLabel);
  }

  public String getLab() {
    return getSelectedDropdownText(labLabel);
  }

  public void setLab(String lab) {
    setDropdown(lab, labLabel);
  }

  public String getSlidesRemaining() {
    return slidesRemainingLabel.getText();
  }

  public void setSlidesRemaining(String slidesRemaining) {
    setText(slidesRemaining, slidesRemainingLabel);
  }

  public String getSlides() {
    return slidesLabel.getAttribute("value");
  }

  public void setSlides(String slides) {
    setText(slides, slidesLabel);
  }

  public String getSlideDiscards() {
    return discardsLabel.getAttribute("value");
  }

  public void setSlideDiscards(String discards) {
    setText(discards, discardsLabel);
  }

  public String getSlideThickness() {
    return thicknessLabel.getAttribute("value");
  }

  public void setSlideThickness(String thickness) {
    setText(thickness, thicknessLabel);
  }

  public String getSlideStain() {
    return getSelectedDropdownText(stainLabel);
  }

  public void setSlideStain(String stain) {
    setDropdown(stain, stainLabel);
  }

  public String getSlidesConsumed() {
    return slidesConsumedLabel.getAttribute("value");
  }

  public void setSlidesConsumed(String slidesConsumed) {
    setText(slidesConsumed, slidesConsumedLabel);
  }

  public String getStrStatus() {
    return getSelectedDropdownText(strStatusLabel).trim();
  }

  public void setStrStatus(String strStatus) {
    setDropdown(strStatus, strStatusLabel);
  }

  public String getDnaseTreated() {
    return dnaseTreatedLabel.getAttribute("value");
  }

  public void setDnaseTreated(Boolean dnaseTreated) {
    setCheckbox(dnaseTreated, dnaseTreatedLabel);
  }

  public String getSamplePurpose() {
    return getSelectedDropdownText(samplePurposeLabel);
  }

  public void setSamplePurpose(String samplePurpose) {
    setDropdown(samplePurpose, samplePurposeLabel);
  }
}
