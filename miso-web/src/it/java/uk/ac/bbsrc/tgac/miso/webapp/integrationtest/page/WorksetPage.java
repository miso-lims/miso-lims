package uk.ac.bbsrc.tgac.miso.webapp.integrationtest.page;

import static org.openqa.selenium.support.ui.ExpectedConditions.*;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.openqa.selenium.By;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;

import uk.ac.bbsrc.tgac.miso.webapp.integrationtest.page.element.DataTable;

public class WorksetPage extends FormPage<WorksetPage.Field> {

  public static enum Field implements FormPage.FieldElement {
    ID(By.id("worksetForm_id")), //
    ALIAS(By.id("worksetForm_alias")), //
    DESCRIPTION(By.id("worksetForm_description")), //
    CREATOR(By.id("worksetForm_creator"));//

    private final By selector;

    private Field(By selector) {
      this.selector = selector;
    }

    @Override
    public By getSelector() {
      return selector;
    }
  }

  private static final String samplesTableWrapper = "list_samples_wrapper";
  private static final String librariesTableWrapper = "list_libraries_wrapper";
  private static final String libraryAliquotsTableWrapper = "list_libraryAliquots_wrapper";
  private static final String poolsTableWrapper = "list_pools_wrapper";

  private static final By okSelector = By.id("ok");

  @FindBy(id = "save")
  private WebElement saveButton;
  @FindBy(id = "dialog")
  private WebElement dialog;

  private DataTable samplesTable = null;
  private DataTable librariesTable = null;
  private DataTable libraryAliquotsTable = null;
  private DataTable poolsTable = null;

  public WorksetPage(WebDriver driver) {
    super(driver, "worksetForm");
    PageFactory.initElements(driver, this);
    waitWithTimeout().until(titleContains("Workset "));
    if (findElementIfExists(By.id(samplesTableWrapper)) != null) {
      samplesTable = new DataTable(driver, samplesTableWrapper);
    }
    if (findElementIfExists(By.id(librariesTableWrapper)) != null) {
      librariesTable = new DataTable(driver, librariesTableWrapper);
    }
    if (findElementIfExists(By.id(libraryAliquotsTableWrapper)) != null) {
      libraryAliquotsTable = new DataTable(driver, libraryAliquotsTableWrapper);
    }
    if (findElementIfExists(By.id(poolsTableWrapper)) != null) {
      poolsTable = new DataTable(driver, poolsTableWrapper);
    }
  }

  public static WorksetPage get(WebDriver driver, String baseUrl, long worksetId) {
    driver.get(baseUrl + "workset/" + worksetId);
    return new WorksetPage(driver);
  }

  public static WorksetPage getForNew(WebDriver driver, String baseUrl) {
    driver.get(baseUrl + "workset/new");
    return new WorksetPage(driver);
  }

  /**
   * Attempts to save the Workset
   * 
   * @return the refreshed WorksetPage if the save was successful; otherwise, null. A null return
   *         means that the save has failed, and validation/error messages should be displayed on the
   *         page
   */
  public WorksetPage clickSave() {
    WebElement html = getHtmlElement();
    WebElement errorBox = getDriver().findElement(By.className("bs-callout"));
    if (errorBox.isDisplayed()) {
      saveButton.click();
      waitUntil(ExpectedConditions.or(ExpectedConditions.stalenessOf(html), ExpectedConditions.stalenessOf(errorBox)));
    } else {
      saveButton.click();
      try {
        waitUntil(
            ExpectedConditions.or(ExpectedConditions.stalenessOf(html), ExpectedConditions.visibilityOf(errorBox)));
      } catch (StaleElementReferenceException e) {
        waitUntil(ExpectedConditions.stalenessOf(html));
      }
    }
    if (ExpectedConditions.stalenessOf(html).apply(getDriver())) {
      waitForPageRefresh(html);
      return new WorksetPage(getDriver());
    } else {
      return null;
    }
  }

  public List<String> getAliasValidationErrors() {
    return getDriver().findElements(By.cssSelector("#worksetForm_aliasError > ul > li")).stream()
        .map(WebElement::getText)
        .collect(Collectors.toList());
  }

  public List<String> getSampleNames() {
    return getNames(samplesTable);
  }

  public List<String> getLibraryNames() {
    return getNames(librariesTable);
  }

  public List<String> getLibraryAliquotNames() {
    return getNames(libraryAliquotsTable);
  }

  public List<String> getPoolNames(){
    return getNames(poolsTable);
  }

  private List<String> getNames(DataTable table) {
    if (table == null) {
      return Collections.emptyList();
    } else {
      return table.getColumnValues("Name");
    }
  }

  public WorksetPage removeSamplesByName(List<String> names) {
    return removeMembersByName(names, samplesTable);
  }

  public WorksetPage removeLibrariesByName(List<String> names) {
    return removeMembersByName(names, librariesTable);
  }

  public WorksetPage removeLibraryAliquotsByName(List<String> names) {
    return removeMembersByName(names, libraryAliquotsTable);
  }

  public WorksetPage removePoolsByName(List<String> names){
    return removeMembersByName(names, poolsTable);
  }

  private WorksetPage removeMembersByName(List<String> names, DataTable table) {
    WebElement html = getHtmlElement();
    List<String> nameCol = getNames(table);
    for (int i = 0; i < nameCol.size(); i++) {
      if (names.contains(nameCol.get(i))) {
        table.checkBoxForRow(i);
      }
    }
    table.clickButton("Remove from Workset");
    waitUntil(visibilityOf(dialog));
    WebElement ok = getDriver().findElement(okSelector);
    clickOk();
    waitUntil(and(stalenessOf(ok), visibilityOfElementLocated(okSelector)));
    clickOk();
    waitForPageRefresh(html);
    return new WorksetPage(getDriver());
  }

}
