package uk.ac.bbsrc.tgac.miso.webapp.integrationtest.page;

import static org.openqa.selenium.support.ui.ExpectedConditions.titleContains;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.PageFactory;

public class SamplePage extends FormPage<SamplePage.Field> {

  public static enum Field implements FormPage.FieldElement {
    ID(By.id("sampleForm_sampleId")), //
    NAME(By.id("sampleForm_name")), //
    ALIAS(By.id("sampleForm_alias")), //
    PROJECT(By.id("sampleForm_project")), //
    DESCRIPTION(By.id("sampleForm_description")), //
    SCIENTIFIC_NAME(By.id("sampleForm_scientificName")), //
    SAMPLE_TYPE(By.id("sampleForm_sampleTypes")), //
    DETAILED_QC_STATUS(By.id("sampleForm_detailedQcStatus")), //
    QC_STATUS_NOTE(By.id("sampleForm_qcStatusNote")), //
    VOLUME(By.id("sampleForm_volume")), //
    DISCARDED(By.id("sampleForm_discarded")), //
    IDENTITY(By.id("sampleForm_identityId")), //
    EXTERNAL_NAME(By.id("sampleForm_externalName")), //
    DONOR_SEX(By.id("sampleForm_donorSex")), //
    PARENT_ALIAS(By.id("sampleForm_parentAlias")), //
    SAMPLE_CLASS(By.id("sampleForm_sampleClass")), //
    SAMPLE_CLASS_ALIAS(By.id("sampleForm_sampleClassAlias")), //
    SUBPROJECT(By.id("sampleForm_subProject")), //
    GROUP_ID(By.id("sampleForm_groupId")), //
    GROUP_DESCRIPTION(By.id("sampleForm_groupDescription")), //
    CONCENTRATION(By.id("sampleForm_concentration")), //
    TISSUE_CLASS(By.id("sampleForm_tissueClass")), //
    TISSUE_ORIGIN(By.id("sampleForm_tissueOrigin")), //
    TISSUE_TYPE(By.id("sampleForm_tissueType")), //
    PASSAGE_NUMBER(By.id("sampleForm_passageNumber")), //
    TIMES_RECEIVED(By.id("sampleForm_timesReceived")), //
    TUBE_NUMBER(By.id("sampleForm_tubeNumber")), //
    SECONDARY_IDENTIFIER(By.id("sampleForm_secondaryIdentifier")), //
    LAB(By.id("sampleForm_lab")), //
    TISSUE_MATERIAL(By.id("sampleForm_tissueMaterial")), //
    REGION(By.id("sampleForm_region")), //
    SLIDES(By.id("sampleForm_slides")), //
    DISCARDS(By.id("sampleForm_discards")), //
    THICKNESS(By.id("sampleForm_thickness")), //
    STAIN(By.id("sampleForm_stain")), //
    SLIDES_CONSUMED(By.id("sampleForm_strStatus")), //
    DNASE_TREATED(By.id("sampleForm_DNAseTreated")), //
    SAMPLE_PURPOSE(By.id("sampleForm_samplePurpose")), //

    WARNINGS(By.className("big")); //

    private final By selector;

    private Field(By selector) {
      this.selector = selector;
    }

    @Override
    public By getSelector() {
      return selector;
    }
  }

  public SamplePage(WebDriver driver) {
    super(driver, "sampleForm");
    PageFactory.initElements(driver, this);
    waitWithTimeout().until(titleContains("Sample "));
  }

  public static SamplePage get(WebDriver driver, String baseUrl, long sampleId) {
    driver.get(baseUrl + "sample/" + sampleId);
    return new SamplePage(driver);
  }

}
