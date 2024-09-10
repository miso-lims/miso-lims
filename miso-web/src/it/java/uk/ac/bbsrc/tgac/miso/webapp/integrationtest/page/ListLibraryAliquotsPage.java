package uk.ac.bbsrc.tgac.miso.webapp.integrationtest.page;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class ListLibraryAliquotsPage extends ListPage {

  public ListLibraryAliquotsPage(WebDriver driver) {
    super(driver);
  }

  public static ListLibraryAliquotsPage getListPage(WebDriver driver, String baseUrl) {
    String url = String.format("%slibraryaliquots", baseUrl);
    driver.get(url);
    return new ListLibraryAliquotsPage(driver);
  }

  public BulkPoolPage poolSelectedTogether() {
    getDriver().findElement(By.linkText("Pool Together")).click();
    getDriver().findElement(By.id("dialog"));
    clickOk();
    return new BulkPoolPage(getDriver());
  }

  public BulkPoolPage poolSelectedSeparately() {
    getDriver().findElement(By.linkText("Pool Separately")).click();
    getDriver().findElement(By.id("dialog"));
    clickOk();
    return new BulkPoolPage(getDriver());
  }

}
