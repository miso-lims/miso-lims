package uk.ac.bbsrc.tgac.miso.webapp.integrationtest.page;

import static org.openqa.selenium.support.ui.ExpectedConditions.*;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import uk.ac.bbsrc.tgac.miso.webapp.integrationtest.page.element.HandsOnTable;

public class BulkPoolPage extends HeaderFooterPage {

  public static class Columns {
    public static final String POOL_ALIAS = "Pool Alias";
    public static final String ID_BARCODE = "Matrix Barcode";
    public static final String CREATION_DATE = "Creation Date";
    public static final String CONCENTRATION = "Concentration (nM)";
    public static final String VOLUME = "Volume (µl)";
    public static final String QC_PASSED = "QC Passed?";
    public static final String READY_TO_RUN = "Ready to Run?";

    private Columns() {
      throw new IllegalStateException("Util class not intended for instantiation");
    }
  };

  @FindBy(id = "bulkactions")
  private WebElement toolbar;

  private final HandsOnTable table;

  public BulkPoolPage(WebDriver driver) {
    super(driver);
    PageFactory.initElements(driver, this);
    waitWithTimeout().until(or(titleContains("Create Pools from Dilutions"), titleContains("Edit Pools")));
    table = new HandsOnTable(driver);
  }

}
