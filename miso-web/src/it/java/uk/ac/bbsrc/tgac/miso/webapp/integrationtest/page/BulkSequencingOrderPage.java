package uk.ac.bbsrc.tgac.miso.webapp.integrationtest.page;

import static org.openqa.selenium.support.ui.ExpectedConditions.titleContains;

import java.util.Collection;
import java.util.List;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.PageFactory;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;

import uk.ac.bbsrc.tgac.miso.core.util.MapBuilder;
import uk.ac.bbsrc.tgac.miso.webapp.integrationtest.page.element.HandsOnTable;

public class BulkSequencingOrderPage extends BulkPage {

  public static class Columns {
    public static final String NAME = "Pool Name";
    public static final String ALIAS = "Pool Alias";
    public static final String PURPOSE = "Purpose";
    public static final String INSTRUMENT_MODEL = "Instrument Model";
    public static final String CONTAINER_MODEL = "Container Model";
    public static final String PARAMETERS = "Sequencing Parameters";
    public static final String PARTITIONS = "Partitions";
    public static final String DESCRIPTION = "Description";

    public static List<String> all() {
      return Lists.newArrayList(NAME, ALIAS, PURPOSE, INSTRUMENT_MODEL, CONTAINER_MODEL, PARAMETERS, PARTITIONS,
          DESCRIPTION);
    }

    private Columns() {
      throw new IllegalStateException("Util class not intended for instantiation");
    }
  };

  public static final String CREATE_URL_FRAGMENT = "sequencingorder/bulk/new";

  private HandsOnTable table;

  public BulkSequencingOrderPage(WebDriver driver) {
    super(driver);
    PageFactory.initElements(driver, this);
    waitWithTimeout().until(titleContains("Create Sequencing Orders from Pools "));
    refreshElements();
  }

  public static BulkSequencingOrderPage getForCreate(WebDriver driver, String baseUrl, Collection<Long> poolIds) {
    String poolIdString = Joiner.on(",").join(poolIds);
    postData(driver, baseUrl + CREATE_URL_FRAGMENT,
        new MapBuilder<String, String>().put("poolIds", poolIdString).build());
    return new BulkSequencingOrderPage(driver);
  }

  @Override
  public HandsOnTable getTable() {
    return table;
  }

  @Override
  protected void refreshElements() {
    table = new HandsOnTable(getDriver());
  }

}
