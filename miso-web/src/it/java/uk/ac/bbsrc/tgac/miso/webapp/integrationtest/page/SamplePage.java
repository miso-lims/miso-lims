package uk.ac.bbsrc.tgac.miso.webapp.integrationtest.page;

import static org.openqa.selenium.support.ui.ExpectedConditions.titleContains;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.PageFactory;

public class SamplePage extends FormPage<SamplePage.Field> {

  public static enum Field implements FormPage.FieldElement {
    ID(By.id("sampleId"), FieldType.LABEL), NAME(By.id("name"), FieldType.LABEL), ALIAS(By.id("alias"), FieldType.TEXT), PROJECT(By
        .id("project"), FieldType.DROPDOWN), DESCRIPTION(By.id("description"), FieldType.TEXT), RECEIVED_DATE(By
            .id("receiveddatepicker"), FieldType.TEXT), SCIENTIFIC_NAME(By.id("scientificName"), FieldType.TEXT), SAMPLE_TYPE(By.id(
                "sampleTypes"), FieldType.DROPDOWN), DETAILED_QC_STATUS(By.id("detailedQcStatus"), FieldType.DROPDOWN), QC_STATUS_NOTE(By
                    .id("qcStatusNote"), FieldType.TEXT), VOLUME(By.id("volume"), FieldType.TEXT), DISCARDED(By
                        .id("discarded"), FieldType.CHECKBOX), IDENTITY(By.id("identityId"), FieldType.LABEL), EXTERNAL_NAME(By
                            .id("externalName"), FieldType.TEXT), DONOR_SEX(By.id("donorSex"), FieldType.DROPDOWN), PARENT_ALIAS(By.id(
                                "parentAlias"), FieldType.LABEL), SAMPLE_CLASS(By.id("sampleClass"), FieldType.LABEL), SAMPLE_CLASS_ALIAS(By
                                    .id("sampleClassAlias"), FieldType.LABEL), SUBPROJECT(By.id(
                                        "subProject"), FieldType.DROPDOWN), GROUP_ID(By.id("groupId"), FieldType.TEXT), GROUP_DESCRIPTION(By
                                            .id("groupDescription"), FieldType.TEXT), CONCENTRATION(By
                                                .id("concentration"), FieldType.TEXT), TISSUE_CLASS(By
                                                    .id("tissueClass"), FieldType.DROPDOWN), TISSUE_ORIGIN(By
                                                        .id("tissueOrigin"), FieldType.DROPDOWN), TISSUE_TYPE(By
                                                            .id("tissueType"), FieldType.DROPDOWN), PASSAGE_NUMBER(By
                                                                .id("passageNumber"), FieldType.TEXT), TIMES_RECEIVED(By
                                                                    .id("timesReceived"), FieldType.TEXT), TUBE_NUMBER(By
                                                                        .id("tubeNumber"), FieldType.TEXT), SECONDARY_IDENTIFIER(By
                                                                            .id("secondaryIdentifier"), FieldType.TEXT), LAB(By
                                                                                .id("lab"), FieldType.DROPDOWN), TISSUE_MATERIAL(By.id(
                                                                                    "tissueMaterial"), FieldType.DROPDOWN), REGION(By.id(
                                                                                        "region"), FieldType.TEXT), SLIDES_REMAINING(By.id(
                                                                                            "slidesRemaining"), FieldType.LABEL), SLIDES(By
                                                                                                .id("slides"), FieldType.TEXT), DISCARDS(By
                                                                                                    .id("discards"), FieldType.TEXT), THICKNESS(By
                                                                                                        .id("thickness"), FieldType.TEXT), STAIN(By
                                                                                                            .id("stain"), FieldType.DROPDOWN), SLIDES_CONSUMED(By
                                                                                                                .id("slidesConsumed"), FieldType.TEXT), STR_STATUS(By
                                                                                                                    .id("strStatus"), FieldType.TEXT), DNASE_TREATED(By
                                                                                                                        .id("DNAseTreated"), FieldType.CHECKBOX), SAMPLE_PURPOSE(By
                                                                                                                            .id("samplePurpose"), FieldType.DROPDOWN),

    WARNINGS(By.className("big"), FieldType.LABEL);

    private final By selector;
    private final FieldType type;

    private Field(By selector, FieldType type) {
      this.selector = selector;
      this.type = type;
    }

    @Override
    public By getSelector() {
      return selector;
    }

    @Override
    public FieldType getType() {
      return type;
    }
  }

  public SamplePage(WebDriver driver) {
    super(driver);
    PageFactory.initElements(driver, this);
    waitWithTimeout().until(titleContains("Sample "));
  }

  public static SamplePage get(WebDriver driver, String baseUrl, long sampleId) {
    driver.get(baseUrl + "miso/sample/" + sampleId);
    return new SamplePage(driver);
  }

}
