package uk.ac.bbsrc.tgac.miso.webapp.integrationtest.page;

import static org.openqa.selenium.support.ui.ExpectedConditions.*;

import java.util.Collection;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import com.google.common.base.Joiner;

import uk.ac.bbsrc.tgac.miso.webapp.integrationtest.page.element.HandsOnTable;

public class BulkPoolPage extends HeaderFooterPage {

  public static class Columns {
    public static final String NAME = "Pool Name";
    public static final String ALIAS = "Pool Alias";
    public static final String BARCODE = "Matrix Barcode";
    public static final String CREATE_DATE = "Creation Date";
    public static final String CONCENTRATION = "Concentration (nM)";
    public static final String VOLUME = "Volume (µl)";
    public static final String QC_PASSED = "QC Passed?";
    public static final String READY_TO_RUN = "Ready to Run?";

    private Columns() {
      throw new IllegalStateException("Util class not intended for instantiation");
    }
  };

  private static final By EDIT_BUTTON_TEXT = By.linkText("Edit");

  @FindBy(id = "bulkactions")
  private WebElement toolbar;

  private final HandsOnTable table;

  public BulkPoolPage(WebDriver driver) {
    super(driver);
    PageFactory.initElements(driver, this);
    waitWithTimeout().until(or(titleContains("Create Pools from Dilutions "), titleContains("Edit Pools ")));
    table = new HandsOnTable(driver);
  }

  public static BulkPoolPage getForEdit(WebDriver driver, String baseUrl, Collection<Long> poolIds) {
    String ids = Joiner.on(',').join(poolIds);
    String url = baseUrl + "miso/pool/bulk/edit?ids=" + ids;
    driver.get(url);
    return new BulkPoolPage(driver);
  }

  public static BulkPoolPage getForPoolSeparately(WebDriver driver, String baseUrl, Collection<Long> dilutionIds) {
    String ids = Joiner.on(',').join(dilutionIds);
    String url = baseUrl + "miso/library/dilution/bulk/propagate?ids=" + ids;
    driver.get(url);
    return new BulkPoolPage(driver);
  }

  public static BulkPoolPage getForPoolTogether(WebDriver driver, String baseUrl, Collection<Long> dilutionIds) {
    String ids = Joiner.on(',').join(dilutionIds);
    String url = baseUrl + "miso/library/dilution/bulk/merge?ids=" + ids;
    driver.get(url);
    return new BulkPoolPage(driver);
  }

  public HandsOnTable getTable() {
    return table;
  }

  public BulkPoolPage chainEdit() {
    WebElement html = getHtmlElement();
    toolbar.findElement(EDIT_BUTTON_TEXT).click();
    waitForPageRefresh(html);
    return new BulkPoolPage(getDriver());
  }

}
