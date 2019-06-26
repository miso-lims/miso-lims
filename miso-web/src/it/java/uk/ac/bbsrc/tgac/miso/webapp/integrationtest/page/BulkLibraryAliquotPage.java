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

public class BulkLibraryAliquotPage extends HeaderFooterPage {

  public static class LibraryAliquotColumns {
    public static final String NAME = "Library Aliquot Name";
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

    private LibraryAliquotColumns() {
      throw new IllegalStateException("Util class not intended for instantiation");
    }
  };

  private static final By EDIT_BUTTON_TEXT = By.linkText("Edit");
  private static final By POOL_TOGETHER_BUTTON_TEXT = By.linkText("Pool together");
  private static final By POOL_SEPARATELY_BUTTON_TEXT = By.linkText("Pool separately");

  @FindBy(id = "bulkactions")
  private WebElement toolbar;

  private final HandsOnTable table;

  public BulkLibraryAliquotPage(WebDriver driver) {
    super(driver);
    PageFactory.initElements(driver, this);
    waitWithTimeout().until(or(titleContains("Create Library Aliquots from Libraries "), titleContains("Edit Library Aliquots ")));
    table = new HandsOnTable(driver);
  }

  public static BulkLibraryAliquotPage getForEdit(WebDriver driver, String baseUrl, Collection<Long> aliquotIds) {
    String ids = Joiner.on(',').join(aliquotIds);
    String url = baseUrl + "miso/libraryaliquot/bulk/edit?ids=" + ids;
    driver.get(url);
    return new BulkLibraryAliquotPage(driver);
  }

  public static BulkLibraryAliquotPage getForPropagate(WebDriver driver, String baseUrl, Collection<Long> libraryIds) {
    String ids = Joiner.on(',').join(libraryIds);
    String url = baseUrl + "miso/libraryaliquot/bulk/propagate?ids=" + ids;
    driver.get(url);
    return new BulkLibraryAliquotPage(driver);
  }

  public HandsOnTable getTable() {
    return table;
  }

  public BulkLibraryAliquotPage chainEdit() {
    WebElement html = getHtmlElement();
    toolbar.findElement(EDIT_BUTTON_TEXT).click();
    waitForPageRefresh(html);
    return new BulkLibraryAliquotPage(getDriver());
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
