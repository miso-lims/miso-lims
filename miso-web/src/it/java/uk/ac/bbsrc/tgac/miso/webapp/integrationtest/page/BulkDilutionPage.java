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

public class BulkDilutionPage extends HeaderFooterPage {

  public static class DilColumns {
    public static final String NAME = "Dilution Name";
    public static final String ID_BARCODE = "Matrix Barcode";
    public static final String BOX_SEARCH = "Box Search";
    public static final String BOX_ALIAS = "Box Alias";
    public static final String BOX_POSITION = "Position";
    public static final String DISCARDED = "Discarded";
    public static final String DISTRIBUTED = "Distributed";
    public static final String DISTRIBUTION_DATE = "Distribution Date";
    public static final String DISTRIBUTION_RECIPIENT = "Distribution Recipient";
    public static final String LIBRARY_ALIAS = "Library Alias";
    public static final String CONCENTRATION = "Conc.";
    public static final String CONCENTRATION_UNITS = "Conc. Units";
    public static final String VOLUME = "Volume";
    public static final String VOLUME_UNITS = "Vol. Units";
    public static final String NG_USED = "ng Lib. Used";
    public static final String VOLUME_USED = "Vol. Lib. Used";
    public static final String CREATION_DATE = "Creation Date";
    public static final String TARGETED_SEQUENCING = "Targeted Sequencing";

    private DilColumns() {
      throw new IllegalStateException("Util class not intended for instantiation");
    }
  };

  private static final By EDIT_BUTTON_TEXT = By.linkText("Edit");
  private static final By POOL_TOGETHER_BUTTON_TEXT = By.linkText("Pool together");
  private static final By POOL_SEPARATELY_BUTTON_TEXT = By.linkText("Pool separately");

  @FindBy(id = "bulkactions")
  private WebElement toolbar;

  private final HandsOnTable table;

  public BulkDilutionPage(WebDriver driver) {
    super(driver);
    PageFactory.initElements(driver, this);
    waitWithTimeout().until(or(titleContains("Create Dilutions from Libraries "), titleContains("Edit Dilutions ")));
    table = new HandsOnTable(driver);
  }

  public static BulkDilutionPage getForEdit(WebDriver driver, String baseUrl, Collection<Long> dilutionIds) {
    String ids = Joiner.on(',').join(dilutionIds);
    String url = baseUrl + "miso/library/dilution/bulk/edit?ids=" + ids;
    driver.get(url);
    return new BulkDilutionPage(driver);
  }

  public static BulkDilutionPage getForPropagate(WebDriver driver, String baseUrl, Collection<Long> libraryIds) {
    String ids = Joiner.on(',').join(libraryIds);
    String url = baseUrl + "miso/library/dilutions/bulk/propagate?ids=" + ids;
    driver.get(url);
    return new BulkDilutionPage(driver);
  }

  public HandsOnTable getTable() {
    return table;
  }

  public BulkDilutionPage chainEdit() {
    WebElement html = getHtmlElement();
    toolbar.findElement(EDIT_BUTTON_TEXT).click();
    waitForPageRefresh(html);
    return new BulkDilutionPage(getDriver());
  }

  public BulkPoolPage chainPoolTogether() {
    WebElement html = getHtmlElement();
    toolbar.findElement(POOL_TOGETHER_BUTTON_TEXT).click();
    waitForPageRefresh(html);
    return new BulkPoolPage(getDriver());
  }

  public BulkPoolPage chainPoolSeparately() {
    WebElement html = getHtmlElement();
    toolbar.findElement(POOL_SEPARATELY_BUTTON_TEXT).click();
    clickOk();
    waitForPageRefresh(html);
    return new BulkPoolPage(getDriver());
  }

}
