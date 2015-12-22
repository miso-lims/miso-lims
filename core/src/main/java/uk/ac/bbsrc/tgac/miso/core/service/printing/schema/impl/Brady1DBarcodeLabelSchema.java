package uk.ac.bbsrc.tgac.miso.core.service.printing.schema.impl;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sf.json.JSONObject;
import net.sourceforge.fluxion.spi.ServiceProvider;
import uk.ac.bbsrc.tgac.miso.core.factory.barcode.BarcodeLabelFactory;
import uk.ac.bbsrc.tgac.miso.core.service.printing.factory.FileGeneratingBarcodeLabelFactory;
import uk.ac.bbsrc.tgac.miso.core.service.printing.schema.BarcodableSchema;

/**
 * Created with IntelliJ IDEA. User: bianx Date: 09/05/2013 Time: 11:48 To change this template use File | Settings | File Templates.
 */
@ServiceProvider
public class Brady1DBarcodeLabelSchema implements BarcodableSchema<File, JSONObject> {
  protected static final Logger log = LoggerFactory.getLogger(Brady1DBarcodeLabelSchema.class);
  private BarcodeLabelFactory<File, JSONObject, BarcodableSchema<File, JSONObject>> barcodeLabelFactory = new FileGeneratingBarcodeLabelFactory<JSONObject>();

  @Override
  public String getName() {
    return "brady1DBarcodeLabelSchema";
  }

  private JSONObject jsonObject;

  @Override
  public Class<JSONObject> isStateFor() {
    return JSONObject.class; // To change body of implemented methods use File | Settings | File Templates.
  }

  @Override
  public File getPrintableLabel(JSONObject jsonObject) {
    return barcodeLabelFactory.getLabel(this, jsonObject);
  }

  @Override
  public BarcodeLabelFactory getBarcodeLabelFactory() {
    return barcodeLabelFactory;
  }

  @Override
  public String getRawState(JSONObject jsonObject) {
    StringBuilder sb = new StringBuilder();

    try {
      String field1 = jsonObject.getString("field1");

      String count = jsonObject.getString("field2");

      sb.append("m m").append("\n");
      sb.append("J").append("\n");
      sb.append("S l1;0,0,6,9,50").append("\n");
      sb.append("B 1,0,0,CODE128,5,0.25;").append(field1).append("\n");
      sb.append("A ").append(count).append("\n");
    } catch (Exception e) {
      log.error("get raw state", e);
    }
    return sb.toString();
  }

}
