package uk.ac.bbsrc.tgac.miso.webapp.integrationtest.page;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class ListPoolsPage extends ListPage {

  public ListPoolsPage(WebDriver driver) {
    super(driver);
  }

  public static ListPoolsPage getListPage(WebDriver driver, String baseUrl) {
    String url = String.format("%spools", baseUrl);
    driver.get(url);
    return new ListPoolsPage(driver);
  }

  Pattern poolProportionFieldPattern = Pattern.compile(".* \\((IPO\\d*)\\)\\*:.*");

  public BulkPoolPage mergeSelected(Map<String, Integer> poolNameToProportion) {
    getDriver().findElement(By.linkText("Merge")).click();
    WebElement dialog = getDriver().findElement(By.id("dialog"));
    for (WebElement p : dialog.findElements(By.tagName("p"))) {
      Matcher m = poolProportionFieldPattern.matcher(p.getText());
      if (p.getText().contains("Create New Box")) {
        continue;
      }
      if (!m.matches()) {
        throw new IllegalStateException("Dialog has unexpected field: " + p.getText());
      }
      String poolName = m.group(1);
      if (!poolNameToProportion.containsKey(poolName)) {
        throw new IllegalArgumentException("No proportion supplied for pool " + poolName);
      }
      WebElement input = p.findElement(By.tagName("input"));
      input.click();
      input.clear();
      input.sendKeys(poolNameToProportion.get(poolName).toString());
    }
    clickOk();
    return new BulkPoolPage(getDriver());
  }

}
