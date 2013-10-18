package uk.ac.bbsrc.tgac.miso.core.service.printing.schema.impl;

import net.sf.json.JSONObject;
import net.sourceforge.fluxion.spi.ServiceProvider;
import org.apache.commons.codec.binary.Base64;
import uk.ac.bbsrc.tgac.miso.core.factory.barcode.BarcodeLabelFactory;
import uk.ac.bbsrc.tgac.miso.core.service.printing.factory.FileGeneratingBarcodeLabelFactory;
import uk.ac.bbsrc.tgac.miso.core.service.printing.schema.BarcodableSchema;
import uk.ac.bbsrc.tgac.miso.core.util.LimsUtils;

import java.io.File;
import java.io.UnsupportedEncodingException;

/**
 * Created with IntelliJ IDEA.
 * User: bianx
 * Date: 09/05/2013
 * Time: 11:48
 * To change this template use File | Settings | File Templates.
 */
@ServiceProvider
public class Brady1DBarcodeLabelSchema implements BarcodableSchema<File, JSONObject> {
  private BarcodeLabelFactory<File, JSONObject, BarcodableSchema<File, JSONObject>> barcodeLabelFactory = new FileGeneratingBarcodeLabelFactory<JSONObject>();

  public String getName() {
    return "brady1DBarcodeLabelSchema";
  }

  private JSONObject jsonObject;

  @Override
  public Class<JSONObject> isStateFor() {
    return JSONObject.class;  //To change body of implemented methods use File | Settings | File Templates.
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
      sb.append("B 2,0,0,CODE128,5,0.25;").append(field1).append("\n");
      sb.append("A ").append(count).append("\n");
    }
    catch (Exception e) {
      e.printStackTrace();
    }
    return sb.toString();
  }

}
