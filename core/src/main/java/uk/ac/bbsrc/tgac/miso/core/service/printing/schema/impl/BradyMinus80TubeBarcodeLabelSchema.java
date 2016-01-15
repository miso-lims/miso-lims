package uk.ac.bbsrc.tgac.miso.core.service.printing.schema.impl;

import java.io.File;
import java.io.UnsupportedEncodingException;

import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sourceforge.fluxion.spi.ServiceProvider;
import uk.ac.bbsrc.tgac.miso.core.data.Barcodable;
import uk.ac.bbsrc.tgac.miso.core.factory.barcode.BarcodeLabelFactory;
import uk.ac.bbsrc.tgac.miso.core.service.printing.factory.FileGeneratingBarcodeLabelFactory;
import uk.ac.bbsrc.tgac.miso.core.service.printing.schema.BarcodableSchema;
import uk.ac.bbsrc.tgac.miso.core.util.LimsUtils;

/**
 * Created with IntelliJ IDEA. User: bianx Date: 09/05/2013 Time: 11:48 To change this template use File | Settings | File Templates.
 */
@ServiceProvider
public class BradyMinus80TubeBarcodeLabelSchema implements BarcodableSchema<File, Barcodable> {
  protected static final Logger log = LoggerFactory.getLogger(BradyMinus80TubeBarcodeLabelSchema.class);
  private BarcodeLabelFactory<File, Barcodable, BarcodableSchema<File, Barcodable>> barcodeLabelFactory = new FileGeneratingBarcodeLabelFactory<Barcodable>();

  @Override
  public String getName() {
    return "bradyMinus80TubeBarcodeLabelSchema";
  }

  private Barcodable barcodable;

  @Override
  public Class<Barcodable> isStateFor() {
    return Barcodable.class; // To change body of implemented methods use File | Settings | File Templates.
  }

  @Override
  public File getPrintableLabel(Barcodable barcodable) {
    return barcodeLabelFactory.getLabel(this, barcodable);
  }

  @Override
  public BarcodeLabelFactory getBarcodeLabelFactory() {
    return barcodeLabelFactory;
  }

  @Override
  public String getRawState(Barcodable barcodable) {

    StringBuilder sb = new StringBuilder();

    try {
      String barcode = new String(Base64.encodeBase64(barcodable.getIdentificationBarcode().getBytes("UTF-8")));
      String alias = barcodable.getLabelText();
      String name = barcodable.getName();

      sb.append("m m").append("\n");
      sb.append("J").append("\n");
      sb.append("S l1;0,0,12,15,38").append("\n");
      sb.append("B 2,6,0,DATAMATRIX,0.21;").append(barcode).append("\n");
      sb.append("B 13,1,0,DATAMATRIX+RECT,0.25;").append(barcode).append("\n");
      sb.append("T 29,2,0,5,pt4;[DATE]").append("\n");

      // shorten alias to fit on label if too long
      if (alias.length() >= 20) {
        alias = alias.substring(0, 18) + "...";
      }

      sb.append("T 17,8,0,5,pt6;").append(LimsUtils.unicodeify(alias)).append("\n");
      sb.append("T 17,11,0,5,pt6;").append(LimsUtils.unicodeify(name)).append("\n");
      sb.append("A 1").append("\n");
    } catch (UnsupportedEncodingException e) {
      log.error("get raw state", e);
    }

    return sb.toString();

  }

}
