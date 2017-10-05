package uk.ac.bbsrc.tgac.miso.webapp.integrationtest.page;

import static org.openqa.selenium.support.ui.ExpectedConditions.*;

import java.util.HashMap;
import java.util.Map;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import com.google.common.collect.Lists;

import uk.ac.bbsrc.tgac.miso.core.data.type.PlatformType;

public class ContainerPage extends FormPage<ContainerPage.Field> {

  public static enum Field implements FormPage.FieldElement {
    ID(By.id("containerId"), FieldType.LABEL), //
    SERIAL_NUMBER(By.id("identificationBarcode"), FieldType.TEXT), //
    PLATFORM(By.id("platform"), FieldType.LABEL), //
    MODEL(By.id("model"), FieldType.LABEL), //
    CLUSTERING_KIT(By.id("clusteringKit"), FieldType.DROPDOWN), //
    MULTIPLEXING_KIT(By.id("multiplexingKit"), FieldType.DROPDOWN);

    private final By selector;
    private final FieldType type;

    private Field(By selector, FieldType type) {
      this.selector = selector;
      this.type = type;
    }

    @Override
    public By getSelector() {
      return selector;
    }

    @Override
    public FieldType getType() {
      return type;
    }
  } // end Field enum
  
  private static final Map<String, Long> idForPlatform;
  static {
    idForPlatform = new HashMap<>();
    idForPlatform.put("Illumina HiSeq 2500", 1L);
    idForPlatform.put("Illumina MiSeq", 2L);
    idForPlatform.put("PacBio RS II", 3L);
  }

  @FindBy(id = "save")
  private WebElement saveButton;

  public ContainerPage(WebDriver driver) {
    super(driver);
    PageFactory.initElements(driver, this);
    waitWithTimeout().until(or(titleContains("Flow Cell "), titleContains("8Pac ")));
  }

  public static ContainerPage getForCreate(WebDriver driver, String baseUrl, String platformType, String sequencerModel,
      int numPartitions) {
    driver.get(baseUrl + "miso/containers");
    ListTabbedPage listContainers = new ListTabbedPage(driver);
    String partitionText = numPartitions + " " + PlatformType.get(platformType).getPartitionName() + (numPartitions == 1 ? "" : "s");
    listContainers
        .clickAddButton(Lists.newArrayList(sequencerModel, partitionText));
    driver.get(baseUrl + "miso/container/new/" + idForPlatform.get(sequencerModel) + "?count=" + numPartitions);
    return new ContainerPage(driver);
  }

  public static ContainerPage getForEdit(WebDriver driver, String baseUrl, long containerId) {
    driver.get(baseUrl + "miso/container/" + containerId);
    return new ContainerPage(driver);
  }

  public ContainerPage save() {
    WebElement html = getHtmlElement();
    saveButton.click();
    waitForPageRefresh(html);
    return new ContainerPage(getDriver());
  }
}
