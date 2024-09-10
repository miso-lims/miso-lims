package uk.ac.bbsrc.tgac.miso.webapp.integrationtest.page;

import static org.openqa.selenium.support.ui.ExpectedConditions.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.Select;

import uk.ac.bbsrc.tgac.miso.core.data.ConcentrationUnit;
import uk.ac.bbsrc.tgac.miso.core.data.type.PlatformType;
import uk.ac.bbsrc.tgac.miso.core.util.LimsUtils;
import uk.ac.bbsrc.tgac.miso.webapp.integrationtest.page.element.DataTable;

public class RunPage extends FormPage<RunPage.Field> {

  public static enum Field implements FormPage.FieldElement {
    ID(By.id("runForm_id")), //
    NAME(By.id("runForm_name")), //
    ALIAS(By.id("runForm_alias")), //
    PLATFORM(By.id("runForm_platformType")), //
    SEQUENCER(By.id("runForm_instrumentIdLabel")), //
    SEQ_PARAMS(By.id("runForm_sequencingParametersId")), //
    DESCRIPTION(By.id("runForm_description")), //
    FILE_PATH(By.id("runForm_runPath")), //
    NUM_CYCLES(By.id("runForm_numCycles")), //
    CALL_CYCLE(By.id("runForm_calledCycles")), //
    IMG_CYCLE(By.id("runForm_imagedCycles")), //
    SCORE_CYCLE(By.id("runForm_scoredCycles")), //
    PAIRED_END(By.id("runForm_pairedEnd")), //
    STATUS(By.id("runForm_status")), //
    START_DATE(By.id("runForm_startDate")), //
    COMPLETION_DATE(By.id("runForm_endDate"));

    private final By selector;

    private Field(By selector) {
      this.selector = selector;
    }

    @Override
    public By getSelector() {
      return selector;
    }
  } // end Field enum

  public static class PoolSearch {
    public static final String NO_POOL = "No Pool";
    public static final String SEARCH = "Search";
    public static final String OUTSTANDING_MATCH = "Outstanding Orders (Matched Chemistry)";
    public static final String OUTSTANDING_ALL = "Outstanding Orders (All)";
    public static final String RTR = "Ready to Run";
    public static final String RECENT = "Recently Modified";
  } // end PoolSearch class

  public static class LaneQC {
    public static final String OK = "OK (order fulfilled, pipeline to proceed)";
    public static final String OK_COLLAB = "OK'd by collaborator (order fulfilled, pipeline to proceed)";
    public static final String FAIL_INSTRUMENT = "Failed: Instrument problem (order not fulfilled, no pipeline)";
    public static final String FAIL_LIB_PREP = "Failed: Library preparation problem (order fulfilled, no pipeline)";
    public static final String FAIL_ANALYSIS = "Failed: Analysis problem (order fulfilled, no pipeline)";
    public static final String FAIL_OTHER = "Failed: Other problem (order not fulfilled, no pipeline)";
  } // end LaneQC class

  public static class RunTableWrapperId {
    public static final String PARTITION = "list_partition_wrapper";
    public static final String CONTAINER = "list_container_wrapper";
    public static final String EXPERIMENT = "list_experiment_wrapper";
    public static final String CHANGELOG = "changelog_wrapper";
  } // end TableWrapperId class

  @FindBy(id = "save")
  private WebElement saveButton;
  @FindBy(id = "containers")
  private WebElement containersSection;
  @FindBy(id = "partitions")
  private WebElement partitionsSection;
  @FindBy(id = "dialog")
  private WebElement dialog;

  public RunPage(WebDriver driver) {
    super(driver, "runForm");
    PageFactory.initElements(driver, this);
    waitWithTimeout().until(or(titleContains("Run "), titleContains("New Run ")));
  }

  public static RunPage getForCreate(WebDriver driver, String baseUrl, long sequencerId) {
    driver.get(baseUrl + "run/new/" + sequencerId);
    return new RunPage(driver);
  }

  public static RunPage getForEdit(WebDriver driver, String baseUrl, long runId) {
    driver.get(baseUrl + "run/" + runId);
    return new RunPage(driver);
  }

  public RunPage save() {
    WebElement html = getHtmlElement();
    saveButton.click();
    waitForPageRefresh(html);
    return new RunPage(getDriver());
  }

  public DataTable getTable(String tableWrapperId) {
    return new DataTable(getDriver(), tableWrapperId);
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
    DataTable containersTable = new DataTable(getDriver(), RunTableWrapperId.CONTAINER);
    containersTable.checkBoxForRow(rowNum);
    containersSection.findElement(By.linkText("Remove")).click();
    waitForPageRefresh(html);
    return new RunPage(getDriver());
  }

  public void searchForPools(boolean assignFirstPool, List<Integer> partitions, String option, String searchText,
      BigDecimal loadingConcentration, ConcentrationUnit units) {
    checkLaneBoxesAndSelectOption(partitions, "Assign Pool", option);
    switch (option) {
      case PoolSearch.NO_POOL:
        return;
      case PoolSearch.SEARCH:
        waitUntil(textToBe(By.className("ui-dialog-title"), "Search for Pool to Assign"));
        dialog.findElement(By.tagName("input")).sendKeys(searchText);
        getDriver().findElement(By.id("ok")).click();
        waitUntil(textToBe(By.className("ui-dialog-title"), "Select Pool"));
        if (assignFirstPool)
          clickFirstPoolTile(true, searchText);
        break;
      case PoolSearch.OUTSTANDING_MATCH:
      case PoolSearch.OUTSTANDING_ALL:
      case PoolSearch.RECENT:
        waitUntil(textToBe(By.className("ui-dialog-title"), "Select Pool"));
        if (assignFirstPool)
          clickFirstPoolTile(false, searchText);
        break;
      case PoolSearch.RTR:
        waitUntil(textToBe(By.className("ui-dialog-title"), "Select Pool"));
        if (assignFirstPool)
          clickFirstPoolTile(true, searchText);
        break;
    }
    if (assignFirstPool) {
      setLoadingConcentration(loadingConcentration, units);
    }
  }

  public void searchForPools(boolean assignFirstPool, List<Integer> partitions, String option, String searchText) {
    searchForPools(assignFirstPool, partitions, option, searchText, null, null);
  }

  private void setLoadingConcentration(BigDecimal concentration, ConcentrationUnit units) {
    waitUntil(textToBe(By.className("ui-dialog-title"), "Set Loading Concentration"));
    if (concentration != null) {
      dialog.findElement(By.tagName("input")).sendKeys(LimsUtils.toNiceString(concentration));
      Select unitsDropdown = new Select(dialog.findElement(By.tagName("select")));
      unitsDropdown.selectByVisibleText(units.getUnits().replace("&#181;", "µ"));
    }
    clickOk();
  }

  public RunPage assignPools(List<Integer> partitions, String option, String searchText,
      BigDecimal loadingConcentration,
      ConcentrationUnit units) {
    WebElement html = getHtmlElement();
    searchForPools(true, partitions, option, searchText, loadingConcentration, units);
    // setLoadingConcentration();
    waitForPageRefresh(html);
    return new RunPage(getDriver());
  }

  public RunPage assignPools(List<Integer> partitions, String option, String searchText) {
    return assignPools(partitions, option, searchText, null, null);
  }

  public RunPage setPartitionQC(List<Integer> partitions, String option, String noteText) {
    WebElement html = getHtmlElement();
    checkLaneBoxesAndSelectOption(partitions, "Set QC", option);
    if (LaneQC.FAIL_OTHER.equals(option)) {
      waitUntil(textToBe(By.className("ui-dialog-title"), "Failed: Other problem Notes"));
      dialog.findElement(By.tagName("input")).sendKeys(noteText);
      getDriver().findElement(By.id("ok")).click();
    }
    waitForPageRefresh(html);
    return new RunPage(getDriver());
  }

  public String getLaneInfo(String columnHeading, int rowNum) {
    DataTable partitionsTable = new DataTable(getDriver(), RunTableWrapperId.PARTITION);
    return partitionsTable.getTextAtCell(columnHeading, rowNum);
  }

  private void checkLaneBoxesAndSelectOption(List<Integer> partitions, String titleText, String option) {
    DataTable partitionsTable = new DataTable(getDriver(), RunTableWrapperId.PARTITION);
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
    if (dialog.findElements(By.className("tile")).isEmpty()) {
      throw new IllegalArgumentException("Cannot find pool for search term '" + searchText + "'.");
    }
    dialog.findElements(By.className("tile")).get(0).click();
  }

  public List<Long> getPoolIdsFromTiles() {
    if (!dialog.isDisplayed()) {
      throw new IllegalStateException("Dialog is not visible");
    }
    List<WebElement> poolTitles = dialog.findElements(By.className("name"));
    return poolTitles.stream()
        .map(title -> Long.valueOf(title.getText().substring(3, title.getText().indexOf(" "))))
        .collect(Collectors.toList());
  }

  public List<String> getPoolWarningsFromTiles() {
    if (!dialog.isDisplayed()) {
      throw new IllegalStateException("Dialog is not visible");
    }
    List<WebElement> poolWarnings = dialog.findElements(By.className("parsley-custom-error-message"));
    poolWarnings.addAll(dialog.findElements(By.className("message-error")));
    poolWarnings.addAll(dialog.findElements(By.className("message-info")));
    return poolWarnings.stream().map(warning -> warning.getText().substring(2)).collect(Collectors.toList());
  }
}
