package uk.ac.bbsrc.tgac.miso.webapp.integrationtest.page;

import static org.openqa.selenium.support.ui.ExpectedConditions.titleContains;

import java.util.Collection;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import com.google.common.base.Joiner;

import uk.ac.bbsrc.tgac.miso.core.util.MapBuilder;

public class TransferPage extends FormPage<TransferPage.Field> {

  public static enum Field implements FormPage.FieldElement {
    ID(By.id("transferForm_id"), By.id("transferForm_idLabel")), //
    TRANSFER_TIME(By.id("transferForm_transferTime")), //
    SENDER_GROUP(By.id("transferForm_senderGroupId")), //
    RECIPIENT(By.id("transferForm_recipient")), //
    RECIPIENT_GROUP(By.id("transferForm_recipientGroupId"));

    private final By selector;
    private final By labelSelector;

    private Field(By selector) {
      this.selector = selector;
      this.labelSelector = null;
    }

    private Field(By selector, By labelSelector) {
      this.selector = selector;
      this.labelSelector = labelSelector;
    }

    @Override
    public By getSelector() {
      return selector;
    }

    @Override
    public By getLabelSelector() {
      return labelSelector;
    }
  }

  @FindBy(id = "save")
  private WebElement saveButton;

  public TransferPage(WebDriver driver) {
    super(driver, "transferForm");
    PageFactory.initElements(driver, this);
    waitWithTimeout().until(titleContains("Transfer "));
  }

  public static TransferPage getForCreateWithSamples(WebDriver driver, String baseUrl, Collection<Long> sampleIds) {
    MapBuilder<String, String> params = new MapBuilder<String, String>()
        .put("sampleIds", Joiner.on(',').join(sampleIds));
    postData(driver, baseUrl + "transfer/new", params.build());
    return new TransferPage(driver);
  }

  public TransferPage save() {
    WebElement html = getHtmlElement();
    saveButton.click();
    waitForPageRefresh(html);
    return new TransferPage(getDriver());
  }

}
