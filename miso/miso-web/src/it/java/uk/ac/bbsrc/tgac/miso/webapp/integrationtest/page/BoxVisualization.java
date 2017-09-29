package uk.ac.bbsrc.tgac.miso.webapp.integrationtest.page;

import static org.openqa.selenium.support.ui.ExpectedConditions.*;
import static uk.ac.bbsrc.tgac.miso.core.util.LimsUtils.isStringEmptyOrNull;
import static uk.ac.bbsrc.tgac.miso.webapp.integrationtest.util.MoreExpectedConditions.elementDoesNotExist;

import java.util.List;
import java.util.stream.Collectors;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.ac.bbsrc.tgac.miso.webapp.integrationtest.page.element.AbstractElement;

public class BoxVisualization extends AbstractElement {

  protected static final Logger log = LoggerFactory.getLogger(BoxVisualization.class);

  private static final By rowSelector = By.cssSelector("tbody tr");
  private static final By tdSelector = By.cssSelector("tbody tr td");
  private static final By thSelector = By.cssSelector("tbody tr th");

  private final List<WebElement> visRows;
  private final List<String> colHeaders;

  @FindBy(id = "boxContentsTableVisualization")
  private WebElement boxVisualization;
  @FindBy(id = "selectedBarcode")
  private WebElement barcodeInput;
  @FindBy(id = "lookupBarcode")
  private WebElement barcodeLookupButton;
  @FindBy(id = "updateSelected")
  private WebElement updatePositionButton;
  @FindBy(id = "removeSelected")
  private WebElement removeTubeButton;
  @FindBy(id = "emptySelected")
  private WebElement discardTubeButton;

  public BoxVisualization(WebDriver driver) {
    super(driver);
    PageFactory.initElements(driver, this);
    this.visRows = boxVisualization.findElements(rowSelector);
    this.colHeaders = boxVisualization.findElements(thSelector).stream()
        .map(element -> element.getText().trim())
        .collect(Collectors.toList());
  }

  public void selectPosition(String position) {
    WebElement selected = getPosition(getRowLabel(position), getColLabel(position));
    selected.click();
  }

  public boolean isEmptyPosition(String position) {
    WebElement target = getPosition(getRowLabel(position), getColLabel(position));
    return target.getAttribute("title").contains("Empty");
  }

  public boolean isUpdatePositionButtonClickable() {
    return updatePositionButton.isEnabled();
  }

  public String getPositionTitle(String position) {
    WebElement target = getPosition(getRowLabel(position), getColLabel(position));
    return target.getAttribute("title");
  }

  protected String getRowLabel(String position) {
    return position.substring(0, 1);
  }

  protected int getColLabel(String position) {
    return Integer.parseInt(position.substring(1));
  }

  protected WebElement getPosition(String rowLabel, int column) {
    List<List<WebElement>> trs = visRows.stream().map(row -> row.findElements(tdSelector)).collect(Collectors.toList());
    if (rowLabel == null || isStringEmptyOrNull(rowLabel)) throw new IllegalArgumentException("Row selector cannot be empty");
    if (colHeaders.size() < column)
      throw new IllegalArgumentException("Column selector " + column + " is larger than table size (" + colHeaders.size() + " columns)");
    List<List<WebElement>> chosenRow = trs.stream()
        .filter(tdList -> tdList.size() > 0 ? tdList.get(0).getText().trim().equals(rowLabel) : false)
        .collect(Collectors.toList());
    if (chosenRow.size() == 0) throw new IllegalArgumentException("Cannot find row " + rowLabel);
    return chosenRow.get(0).get(column);
  }

  public void lookupBarcode(String barcode) {
    barcodeInput.click();
    barcodeInput.clear();
    barcodeInput.sendKeys(barcode);
    barcodeInput.sendKeys(Keys.ESCAPE);
    barcodeLookupButton.click();
    waitUntil(elementDoesNotExist(By.id("ajaxLoader")));
    WebElement okButton = findElementIfExists(By.id("ok"));
    if (okButton != null) {
      okButton.click();
    } else {
      waitUntil(elementToBeClickable(barcodeLookupButton));
    }
  }

  public void updatePosition(boolean requireConfirmation) {
    if (updatePositionButton.isEnabled()) {
      // click the button if it's clickable, otherwise do nothing
      updatePositionButton.click();
      if (requireConfirmation) {
        WebElement okButton = getDriver().findElement(By.id("ok"));
        okButton.click();
        waitUntil(invisibilityOf(okButton));
      }
      waitUntil(elementToBeClickable(updatePositionButton));
    } else {
      throw new IllegalStateException("updatePositionButton is not clickable");
    }
  }

  public void removeTube() {
    if (removeTubeButton.isEnabled()) {
      removeTubeButton.click();
    } else {
      throw new IllegalStateException("Error: removeTubeButton is not clickable");
    }
    confirmAndWaitForRefresh();
  }

  public void discardTube() {
    if (discardTubeButton.isEnabled()) {
      discardTubeButton.click();
    } else {
      throw new IllegalStateException("Error: discardTubeButton is not clickable");
    }
    confirmAndWaitForRefresh();
  }

  protected void confirmAndWaitForRefresh() {
    waitUntil(visibilityOf(getDriver().findElement(By.id("ok"))));
    confirmDialog();
    waitUntil(stalenessOf(getDriver().findElement(By.tagName("html"))));
  }

  protected void confirmDialog() {
    WebElement okButton = getDriver().findElement(By.id("ok"));
    okButton.click();
  }
}
