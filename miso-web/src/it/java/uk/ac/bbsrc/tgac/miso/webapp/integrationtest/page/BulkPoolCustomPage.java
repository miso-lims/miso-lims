package uk.ac.bbsrc.tgac.miso.webapp.integrationtest.page;

import static org.openqa.selenium.support.ui.ExpectedConditions.titleContains;

import java.util.Collection;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

import com.google.common.base.Joiner;

import uk.ac.bbsrc.tgac.miso.core.util.MapBuilder;
import uk.ac.bbsrc.tgac.miso.webapp.integrationtest.page.element.HandsOnTable;

public class BulkPoolCustomPage extends BulkPoolPage {

  private static final By tableSelector = By.cssSelector("#hotContainer .ht_master");

  public BulkPoolCustomPage(WebDriver driver) {
    super(driver);
    waitWithTimeout().until(titleContains("Create Pools from Library Aliquots "));
  }

  public static BulkPoolCustomPage get(WebDriver driver, String baseUrl, Collection<Long> aliquotIds,
      int poolQuantity) {
    String ids = Joiner.on(',').join(aliquotIds);
    String url = baseUrl + "libraryaliquot/bulk/pool";
    MapBuilder<String, String> params = new MapBuilder<String, String>()
        .put("ids", ids)
        .put("quantity", Integer.toString(poolQuantity));
    postData(driver, url, params.build());
    return new BulkPoolCustomPage(driver);
  }

  @Override
  public HandsOnTable getTable() {
    return new HandsOnTable(getDriver());
  }

  public void switchToLibraryAliquotView() {
    switchView("Choose Library Aliquots");
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
