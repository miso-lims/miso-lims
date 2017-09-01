package uk.ac.bbsrc.tgac.miso.core.service.printing;

import java.io.UnsupportedEncodingException;

import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.ac.bbsrc.tgac.miso.core.data.Barcodable;
import uk.ac.bbsrc.tgac.miso.core.util.LimsUtils;

/**
 * All know printer models that can print barcode labels
 * 
 * Brady printers are programmed in Yabasic:
 * https://www.cab.de/en/support/support-downloads/?suchtyp=bereich&bereich=45&produktgruppe=48&produkt=166
 */
public enum Driver {
  BRADY_1D {

    @Override
    public String encode(Barcodable b) {
      StringBuilder sb = new StringBuilder();

      sb.append("m m\n");
      sb.append("J\n");
      sb.append("S l1;0,0,6,9,50\n");
      sb.append("B 1,0,0,CODE128,5,0.25;").append(b.getIdentificationBarcode()).append("\n");
      sb.append("A ").append(b.getLabelText()).append("\n");
      return sb.toString();
    }

  },
  BRADY_BPT_635_488 {
    @Override
    public String encode(Barcodable barcodable) {
      StringBuilder sb = new StringBuilder();

      try {
        String barcode = new String(Base64.encodeBase64(barcodable.getIdentificationBarcode().getBytes("UTF-8")));
        String alias = barcodable.getLabelText();
        String name = barcodable.getName();
        String barcode64 = new String(Base64.encodeBase64(barcode.getBytes("UTF-8")));

        sb.append("m m\n");
        sb.append("J\n");
        sb.append("S l1;0,0,12,15,38\n");
        sb.append("B 2,6,0,DATAMATRIX,0.21;").append(barcode64).append("\n");
        sb.append("B 13,1,0,DATAMATRIX+RECT,0.25;").append(barcode64).append("\n");
        sb.append("T 29,2,0,5,pt4;[DATE]\n");
        appendTruncated(sb, 20, alias);

        sb.append("T 17,8,0,5,pt6;").append(LimsUtils.unicodeify(alias)).append("\n");
        sb.append("T 17,11,0,5,pt6;").append(LimsUtils.unicodeify(name)).append("\n");
        sb.append("A 1\n");
      } catch (UnsupportedEncodingException e) {
        log.error("get raw state", e);
      }

      return sb.toString();
    }
  },
  BRADY_THT_155_490 {
    @Override
    public String encode(Barcodable barcodable) {
      StringBuilder sb = new StringBuilder();

      try {
        String barcode = new String(Base64.encodeBase64(barcodable.getIdentificationBarcode().getBytes("UTF-8")));
        sb.append("mm\n");
        sb.append("J\n");
        sb.append("O R\n");
        sb.append("S l1;0.0,0.00,17.95,17.95,46.41\n");
        sb.append("B 4,5,0,DATAMATRIX,0.5;").append(barcode).append("\n");
        sb.append("T 15,9,0,5,4;");
        appendTruncated(sb, 12, barcodable.getName());
        sb.append("\n");
        sb.append("T 15,15,0,3,4;");
        appendTruncated(sb, 12, barcodable.getLabelText());
        sb.append("\n");
        sb.append("A 1\n");
      } catch (UnsupportedEncodingException e) {
        log.error("get raw state", e);
        return null;
      }
      return sb.toString();
    }
  },
  BRADY_THT_181_492_3 {
    @Override
    public String encode(Barcodable barcodable) {
      StringBuilder sb = new StringBuilder();

      try {
        String barcode = new String(Base64.encodeBase64(barcodable.getIdentificationBarcode().getBytes("UTF-8")));
        String alias = barcodable.getLabelText();
        String name = barcodable.getName();
        String barcode64 = new String(Base64.encodeBase64(barcode.getBytes("UTF-8")));
        sb.append("m m\n");
        sb.append("J\n");
        sb.append("S l1;0,0,12,15,38\n");
        sb.append("B 3,2,0,DATAMATRIX,0.21;").append(barcode64).append("\n");
        sb.append("B 17,1,0,DATAMATRIX+RECT,0.25;").append(barcode64).append("\n");
        sb.append("T 29,2,0,5,pt4;[DATE]\n");
        appendTruncated(sb, 17, name);
        sb.append("T 17,8,0,5,pt6;").append(LimsUtils.unicodeify(alias)).append("\n");
        sb.append("T 17,11,0,5,pt6;").append(LimsUtils.unicodeify(name)).append("\n");
        sb.append("A 1").append("\n");
        appendTruncated(sb, 17, alias);
        sb.append("T 17,8,0,5,pt6;").append(LimsUtils.unicodeify(alias)).append("\n");
        sb.append("T 17,11,0,5,pt6;").append(LimsUtils.unicodeify(name)).append("\n");
        sb.append("A 1\n");
      } catch (UnsupportedEncodingException e) {
        log.error("get raw state", e);
        return null;
      }
      return sb.toString();
    }
  };
  private static Logger log = LoggerFactory.getLogger(Driver.class);

  protected static void appendTruncated(StringBuilder sb, int length, String str) {
    if (str.length() >= length) {
      sb.append(str, 0, length - 2);
      sb.append("...");
    } else {
      sb.append(str);
    }
  }

  /**
   * Generate the printer commands needed to print a label for the supplied item.
   */
  public abstract String encode(Barcodable b);

}
