package uk.ac.bbsrc.tgac.miso.webapp.integrationtest.page;

import static org.openqa.selenium.support.ui.ExpectedConditions.titleContains;

import java.util.Collection;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

import com.google.common.base.Joiner;

import uk.ac.bbsrc.tgac.miso.webapp.integrationtest.page.element.HandsOnTable;

public class BulkPoolCustomPage extends BulkPoolPage {

  private static final By tableSelector = By.cssSelector("#hotContainer .ht_master");

  public BulkPoolCustomPage(WebDriver driver) {
    super(driver);
    waitWithTimeout().until(titleContains("Create Pools from Dilutions "));
  }

  public static BulkPoolCustomPage get(WebDriver driver, String baseUrl, Collection<Long> dilutionIds, int poolQuantity) {
    String ids = Joiner.on(',').join(dilutionIds);
    String url = baseUrl + "miso/library/dilution/bulk/pool?ids=" + ids + "&quantity=" + poolQuantity;
    driver.get(url);
    return new BulkPoolCustomPage(driver);
  }

  @Override
  public HandsOnTable getTable() {
    return new HandsOnTable(getDriver());
  }

  public void switchToDilutionView() {
    switchView("Choose Dilutions");
  }

  public void switchToPoolView() {
    switchView("Edit Pools");
  }

  private void switchView(String buttonText) {
    WebElement table = getDriver().findElement(tableSelector);
    getToolbar().findElement(By.linkText(buttonText)).click();
    waitWithTimeout().until(ExpectedConditions.stalenessOf(table));
  }

}
