package uk.ac.bbsrc.tgac.miso.webapp.integrationtest.page;

import static org.openqa.selenium.support.ui.ExpectedConditions.titleContains;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.regex.Pattern;

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
    // oh this looks awful:
    // https://stackoverflow.com/questions/18206578/java-lang-classcastexception-com-sun-proxy-proxy8-cannot-be-cast-to-org-openqa
      
    waitWithTimeout().until(titleContains("Sample "));
  }

  public static SamplePage get(WebDriver driver, String baseUrl, long sampleId) {
    driver.get(baseUrl + "miso/sample/" + sampleId);
    return new SamplePage(driver);
  }

  public String getSampleId() {
    return idLabel.getText();
  }

  public void setSampleId() {
    throw new UnsupportedOperationException();
  }

  public String getSampleName() {
    return nameLabel.getText();
  }

  public void setSampleName() {
    throw new UnsupportedOperationException();
  }

  public String getSampleAlias() {
    return aliasLabel.getAttribute("value");
  }

  public void setSampleAlias(String alias) {
    setText(alias, aliasLabel);
  }

  public String getProjectDropdownForCreateLabel() {
    return projectDropdownForCreateLabel.getText();
  }

  public void setProjectDropdownForCreateLabel(String project) {
    setDropdown(project, projectDropdownForCreateLabel);
  }

  public String getSampleProjectForCreate(String project) {
    throw new UnsupportedOperationException();
  }

  public void setSampleProjectForCreate(String project) {
    if ("select".equals(projectDropdownForCreateLabel.getTagName())) {
      setDropdown(project, projectDropdownForCreateLabel);
    }
  }

  public String getProjectNameForEditLabel() {
    return projectNameForEditLabel.getText();
  }

  public void setProjectNameForEditLabel() {
    throw new UnsupportedOperationException();
  }

  public String getSampleDescription() {
    return descriptionLabel.getAttribute("value");
  }

  public void setSampleDescription(String description) {
    setText(description, descriptionLabel);
  }

  public String getSampleReceivedDate() {
    return receivedDateInput.getAttribute("value");
  }

  public void setSampleReceivedDate(String receivedDate) {
    Pattern dateFormat = Pattern.compile("[0-3]\\d/[0-1]\\d/\\d{4}");
    if (dateFormat.matcher(receivedDate).matches()) {
      setText(receivedDate, receivedDateInput);
    } else {
      throw new IllegalArgumentException("Date must be in format DD/MM/YYYY");
    }
  }

  public String getSampleScientificName() {
    return scientificNameLabel.getAttribute("value");
  }

  public void setSampleScientificName(String scientificName) {
    setText(scientificName, scientificNameLabel);
  }

  public String getSampleType() {
    return getSelectedDropdownText(sampleTypeLabel);
  }

  public void setSampleType(String sampleType) {
    setDropdown(sampleType, sampleTypeLabel);
  }

  public String getSampleQcStatus() {
    return getSelectedDropdownText(qcStatusLabel);
  }

  public void setSampleQcStatus(String qcStatus) {
    setDropdown(qcStatus, qcStatusLabel);
  }

  public String getSampleQcNote() {
    return qcNoteLabel.getAttribute("value");
  }

  public void setSampleQcNote(String qcNote) {
    setText(qcNote, qcNoteLabel);
  }

  public String getSampleVolume() {
    return volumeLabel.getAttribute("value");
  }

  public void setSampleVolume(String volume) {
    setText(volume, volumeLabel);
  }

  public boolean getSampleDiscarded() {
    return Boolean.valueOf(discardedLabel.getAttribute("value"));
  }

  public void setSampleDiscarded(boolean discarded) {
    setCheckbox(discarded, discardedLabel);
  }

  public String getSampleIdentityId() {
    return identityIdLabel.getAttribute("value");
  }

  public void setSampleIdentityId(String identityId) {
    setText(identityId, identityIdLabel);
  }

  public String getSampleExternalName() {
    if ("input".equals(externalNameLabel.getTagName())) {
      return externalNameLabel.getAttribute("value");
    } else {
      return externalNameLabel.getText();
    }
  }

  public void setSampleExternalName(String externalName) {
    setText(externalName, externalNameLabel);
  }

  public String getSampleDonorSex() {
    if (donorSexLabel instanceof Select) {
      return getSelectedDropdownText(donorSexLabel);
    } else {
      return donorSexLabel.getText();
    }
  }

  public void setSampleDonorSex(String donorSex) {
    setDropdown(donorSex, donorSexLabel);
  }

  public String getSampleParentAlias() {
    return parentAliasLabel.getText();
  }

  public void setSampleParentAlias() {
    throw new UnsupportedOperationException();
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
    if (matches == null) throw new IllegalArgumentException("Sample class " + sampleClass + " not available for sample " + getSampleId());

    setDropdown(sampleClass, sampleClassLabel);
  }

  public String getSampleSubProject() {
    return getSelectedDropdownText(subProjectLabel);
  }

  public void setSampleSubProject(String subProject) {
    setDropdown(subProject, this.subProjectLabel);
  }

  public String getSampleGroupId() {
    return groupIdLabel.getAttribute("value");
  }

  public void setSampleGroupId(String groupId) {
    setText(groupId, groupIdLabel);
  }

  public String getSampleGroupDescription() {
    return groupDescriptionLabel.getAttribute("value");
  }

  public void setSampleGroupDescription(String groupDescription) {
    setText(groupDescription, groupDescriptionLabel);
  }

  public String getSampleConcentration() {
    return concentrationLabel.getAttribute("value");
  }

  public void setSampleConcentration(String concentration) {
    setText(concentration, concentrationLabel);
  }

  public String getSampleTissueClass() {
    if (tissueClassLabel instanceof Select) {
      return getSelectedDropdownText(tissueClassLabel);
    } else {
      return tissueClassLabel.getText();
    }
  }

  public void setSampleTissueClass(String tissueClass) {
    setDropdown(tissueClass, tissueClassLabel);
  }

  public String getSampleTissueOrigin() {
    return getSelectedDropdownText(tissueOriginLabel);
  }

  public void setSampleTissueOrigin(String tissueOrigin) {
    setDropdown(tissueOrigin, tissueOriginLabel);
  }

  public String getSampleTissueType() {
    return getSelectedDropdownText(tissueTypeLabel);
  }

  public void setSampleTissueType(String tissueType) {
    setDropdown(tissueType, tissueTypeLabel);
  }

  public String getSamplePassageNumber() {
    if ("input".equals(passageNumberLabel.getTagName())) {
      return passageNumberLabel.getAttribute("value");
    } else {
      return passageNumberLabel.getText();
    }
  }

  public void setSamplePassageNumber(String passageNumber) {
    setText(passageNumber, passageNumberLabel);
  }

  public String getSampleTimesReceived() {
    if ("input".equals(timesReceivedLabel.getTagName())) {
      return timesReceivedLabel.getAttribute("value");
    } else {
      return timesReceivedLabel.getText();
    }
  }

  public void setSampleTimesReceived(String timesReceived) {
    setText(timesReceived, timesReceivedLabel);
  }

  public String getSampleTubeNumber() {
    if ("input".equals(tubeNumberLabel.getTagName())) {
      return tubeNumberLabel.getAttribute("value");
    } else {
      return tubeNumberLabel.getText();
    }
  }

  public void setSampleTubeNumber(String tubeNumber) {
    setText(tubeNumber, tubeNumberLabel);
  }

  public String getSampleTissueMaterial() {
    return getSelectedDropdownText(tissueMaterialLabel);
  }

  public void setSampleTissueMaterial(String tissueMaterial) {
    setDropdown(tissueMaterial, tissueMaterialLabel);
  }

  public String getSampleRegion() {
    return regionLabel.getAttribute("value");
  }

  public void setSampleRegion(String region) {
    setText(region, regionLabel);
  }

  public String getSampleExternalInstituteIdentifier() {
    return externalInstituteIdentifierLabel.getAttribute("value");
  }

  public void setSampleExternalInstituteIdentifier(String externalInstituteIdentifier) {
    setText(externalInstituteIdentifier, externalInstituteIdentifierLabel);
  }

  public String getSampleLab() {
    return getSelectedDropdownText(labLabel);
  }

  public void setSampleLab(String lab) {
    setDropdown(lab, labLabel);
  }

  public String getSampleSlidesRemaining() {
    return slidesRemainingLabel.getText();
  }

  public void setSampleSlidesRemaining(String slidesRemaining) {
    setText(slidesRemaining, slidesRemainingLabel);
  }

  public String getSampleSlides() {
    return slidesLabel.getAttribute("value");
  }

  public void setSampleSlides(String slides) {
    setText(slides, slidesLabel);
  }

  public String getSampleSlideDiscards() {
    return discardsLabel.getAttribute("value");
  }

  public void setSampleSlideDiscards(String discards) {
    setText(discards, discardsLabel);
  }

  public String getSampleSlideThickness() {
    return thicknessLabel.getAttribute("value");
  }

  public void setSampleSlideThickness(String thickness) {
    setText(thickness, thicknessLabel);
  }

  public String getSampleSlideStain() {
    return getSelectedDropdownText(stainLabel);
  }

  public void setSampleSlideStain(String stain) {
    setDropdown(stain, stainLabel);
  }

  public String getSampleSlidesConsumed() {
    return slidesConsumedLabel.getAttribute("value");
  }

  public void setSampleSlidesConsumed(String slidesConsumed) {
    setText(slidesConsumed, slidesConsumedLabel);
  }

  public String getSampleStrStatus() {
    return getSelectedDropdownText(strStatusLabel);
  }

  public void setSampleStrStatus(String strStatus) {
    setDropdown(strStatus, strStatusLabel);
  }

  public String getSampleDnaseTreated() {
    return dnaseTreatedLabel.getAttribute("value");
  }

  public void setSampleDnaseTreated(Boolean dnaseTreated) {
    setCheckbox(dnaseTreated, dnaseTreatedLabel);
  }

  public String getSampleSamplePurpose() {
    return getSelectedDropdownText(samplePurposeLabel);
  }

  public void setSampleSamplePurpose(String samplePurpose) {
    setDropdown(samplePurpose, samplePurposeLabel);
  }

  public String getSensibleDate(String date) {
    SimpleDateFormat fromUI = new SimpleDateFormat("dd/MM/yyyy");
    SimpleDateFormat sensible = new SimpleDateFormat("yyyy-MM-dd");
    try {
      return sensible.format(fromUI.parse(date));
    } catch (ParseException e) {
      throw new IllegalArgumentException("Very bad date format", e);
    }
  }
}
