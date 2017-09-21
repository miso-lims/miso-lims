package uk.ac.bbsrc.tgac.miso.webapp.integrationtest.page;

import static org.openqa.selenium.support.ui.ExpectedConditions.*;

import java.util.List;
import java.util.stream.Collectors;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import uk.ac.bbsrc.tgac.miso.core.data.type.PlatformType;
import uk.ac.bbsrc.tgac.miso.webapp.integrationtest.page.element.DataTable;

public class RunPage extends FormPage<RunPage.Field> {

  public static enum Field implements FormPage.FieldElement {
    ID(By.id("runId"), FieldType.LABEL), //
    NAME(By.id("name"), FieldType.LABEL), //
    ALIAS(By.id("alias"), FieldType.TEXT), //
    PLATFORM(By.id("platform"), FieldType.LABEL), //
    SEQUENCER(By.id("sequencer"), FieldType.LABEL), //
    SEQ_PARAMS(By.id("sequencingParameters"), FieldType.DROPDOWN),//
    DESCRIPTION(By.id("description"), FieldType.TEXT), //
    FILE_PATH(By.id("filePath"), FieldType.TEXT), //
    NUM_CYCLES(By.id("numCycles"), FieldType.TEXT), //
    CALL_CYCLE(By.id("callCycle"), FieldType.TEXT), //
    IMG_CYCLE(By.id("imgCycle"), FieldType.TEXT), //
    SCORE_CYCLE(By.id("scoreCycle"), FieldType.TEXT), //
    PAIRED_END(By.id("pairedEnd"), FieldType.CHECKBOX), //
    STATUS(By.name("health"), FieldType.RADIO), //
    START_DATE(By.id("startDate"), FieldType.DATEPICKER), //
    COMPLETION_DATE(By.id("completionDate"), FieldType.DATEPICKER);

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
  } // end Field enum

  public static class PoolSearch {
    public static final String NO_POOL = "→ No Pool";
    public static final String SEARCH = "→ Search";
    public static final String OUTSTANDING_MATCH = "→ Outstanding Orders (Matched Chemistry)";
    public static final String OUTSTANDING_ALL = "→ Outstanding Orders (All)";
    public static final String RTR = "→ Ready to Run";
    public static final String RECENT = "→ Recently Modified";
  } // end PoolSearch class

  public static class LaneQC {
    public static final String OK = "→ OK";
    public static final String OK_COLLAB = "→ OK'd by collaborator";
    public static final String FAIL_INSTRUMENT = "→ Failed: Instrument problem";
    public static final String FAIL_LIB_PREP = "→ Failed: Library preparation problem";
    public static final String FAIL_ANALYSIS = "→ Failed: Analysis problem";
    public static final String FAIL_OTHER = "→ Failed: Other problem";
    public static final String FAIL_SEQINFO = "→ Failed: SeqInfo QC";
    public static final String FAIL_SEQOPS = "→ Failed: SeqOps QC";
  } // end LaneQC class

  @FindBy(id = "save")
  private WebElement saveButton;
  @FindBy(id = "containers")
  private WebElement containersSection;
  @FindBy(id = "partitions")
  private WebElement partitionsSection;
  @FindBy(id = "dialog")
  private WebElement dialog;

  public RunPage(WebDriver driver) {
    super(driver);
    PageFactory.initElements(driver, this);
    waitWithTimeout().until(or(titleContains("Run "), titleContains("New Run ")));
  }

  public static RunPage getForCreate(WebDriver driver, String baseUrl, long sequencerId) {
    driver.get(baseUrl + "miso/run/new/" + sequencerId);
    return new RunPage(driver);
  }

  public static RunPage getForEdit(WebDriver driver, String baseUrl, long runId) {
    driver.get(baseUrl + "miso/run/" + runId);
    return new RunPage(driver);
  }

  public RunPage save() {
    WebElement html = getHtmlElement();
    saveButton.click();
    waitForPageRefresh(html);
    return new RunPage(getDriver());
  }

  public RunPage addContainer(String serialNumber, String platformType, boolean isRunFull) {
    WebElement html = getHtmlElement();
    containersSection.findElement(By.linkText("Add " + PlatformType.get(platformType).getContainerName())).click();
    waitUntil(visibilityOf(dialog));
    if (dialog.findElement(By.tagName("p")).getText().startsWith("Cannot add another ")) {
      if (isRunFull) {
        // if we expect that it's already full, that's fine
        clickOk();
        return this;
      } else {
        // we didn't expect this run to already contain the maximum number of containers
        throw new IllegalArgumentException("Run is already unexpectedly full of containers.");
      }
    } else {
      dialog.findElement(By.tagName("input")).sendKeys(serialNumber);
      clickOk();
      waitForPageRefresh(html);
      return new RunPage(getDriver());
    }
  }

  public RunPage removeContainer(int rowNum) {
    WebElement html = getHtmlElement();
    DataTable containersTable = new DataTable(containersSection.findElement(By.id("list_container")));
    containersTable.checkBoxForRow(rowNum);
    containersSection.findElement(By.linkText("Remove")).click();
    waitForPageRefresh(html);
    return new RunPage(getDriver());
  }

  public void searchForPools(boolean assignFirstPool, List<Integer> partitions, String option, String searchText) {
    checkBoxesAndSelectOption(partitions, "Assign Pool", option);
    switch (option) {
    case PoolSearch.NO_POOL:
      break;
    case PoolSearch.SEARCH:
      waitUntil(textToBe(By.className("ui-dialog-title"), "Search for Pool to Assign"));
      dialog.findElement(By.tagName("input")).sendKeys(searchText);
      getDriver().findElement(By.id("ok")).click();
      waitUntil(textToBe(By.className("ui-dialog-title"), "Select Pool"));
      if (assignFirstPool) clickFirstPoolTile(true, searchText);
      break;
    case PoolSearch.OUTSTANDING_MATCH:
    case PoolSearch.OUTSTANDING_ALL:
    case PoolSearch.RECENT:
      waitUntil(textToBe(By.className("ui-dialog-title"), "Select Pool"));
      if (assignFirstPool) clickFirstPoolTile(false, searchText);
      break;
    case PoolSearch.RTR:
      waitUntil(textToBe(By.className("ui-dialog-title"), "Select Pool"));
      if (assignFirstPool) clickFirstPoolTile(true, searchText);
      break;
    }
  }

  public RunPage assignPools(List<Integer> partitions, String option, String searchText) {
    WebElement html = getHtmlElement();
    searchForPools(true, partitions, option, searchText);
    waitForPageRefresh(html);
    return new RunPage(getDriver());
  }

  public RunPage setPartitionQC(List<Integer> partitions, String option, String noteText) {
    WebElement html = getHtmlElement();
    checkBoxesAndSelectOption(partitions, "Set QC", option);
    switch (option) {
    case LaneQC.OK:
    case LaneQC.OK_COLLAB:
    case LaneQC.FAIL_INSTRUMENT:
    case LaneQC.FAIL_LIB_PREP:
    case LaneQC.FAIL_ANALYSIS:
      break;
    case LaneQC.FAIL_OTHER:
    case LaneQC.FAIL_SEQINFO:
    case LaneQC.FAIL_SEQOPS:
      waitUntil(textToBe(By.className("ui-dialog-title"), "Failed: Other problem Notes"));
      dialog.findElement(By.tagName("input")).sendKeys(noteText);
      getDriver().findElement(By.id("ok")).click();
      break;
    }
    waitForPageRefresh(html);
    return new RunPage(getDriver());
  }

  public String getLaneInfo(String columnHeading, int rowNum) {
    DataTable partitionsTable = new DataTable(partitionsSection.findElement(By.id("list_partition")));
    return partitionsTable.getTextAtCell(columnHeading, rowNum);
  }

  private void checkBoxesAndSelectOption(List<Integer> partitions, String titleText, String option) {
    DataTable partitionsTable = new DataTable(partitionsSection.findElement(By.id("list_partition")));
    partitions.forEach(partition -> partitionsTable.checkBoxForRow(partition));
    partitionsSection.findElement(By.linkText(titleText)).click();
    waitUntil(visibilityOf(dialog));
    waitUntil(textToBe(By.className("ui-dialog-title"), titleText));
    dialog.findElement(By.linkText(option)).click();
  }

  private void clickFirstPoolTile(boolean extraWait, String searchText) {
    if (extraWait) {
      waitUntil(textToBe(By.className("ui-dialog-title"), "Select Pool"), 60);
    } else {
      waitUntil(textToBe(By.className("ui-dialog-title"), "Select Pool"));
    }
    if (dialog.findElements(By.className("pool-tile")).isEmpty()) {
      throw new IllegalArgumentException("Cannot find pool for search term '" + searchText + "'.");
    }
    dialog.findElements(By.className("pool-tile")).get(0).click();
  }

  public List<Long> getPoolIdsFromTiles() {
    if (!dialog.isDisplayed()) {
      throw new IllegalStateException("Dialog is not visible");
    }
    List<WebElement> poolTitles = dialog.findElements(By.className("pool-name"));
    return poolTitles.stream()
        .map(title -> Long.valueOf(title.getText().substring(3, title.getText().indexOf(" "))))
        .collect(Collectors.toList());
  }
}
