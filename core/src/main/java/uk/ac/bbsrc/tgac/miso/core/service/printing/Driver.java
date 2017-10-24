package uk.ac.bbsrc.tgac.miso.core.service.printing;

import java.io.UnsupportedEncodingException;
import java.util.function.Consumer;

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
 * 
 * Zebra printers use ZPL:
 * https://www.zebra.com/content/dam/zebra/manuals/en-us/software/zpl-zbi2-pm-en.pdf
 */
@SuppressWarnings("squid:S1192")
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
        appendTruncated(20, alias, s -> appendBradyEscapedUnicode(sb, s));

        sb.append("T 17,8,0,5,pt6;");
        appendBradyEscapedUnicode(sb, alias);
        sb.append("\n");
        sb.append("T 17,11,0,5,pt6;");
        appendBradyEscapedUnicode(sb, name);
        sb.append("\n");
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
        sb.append("T 15,7,0,5,4;");
        appendTruncated(12, barcodable.getName(), s -> appendBradyEscapedUnicode(sb, s));
        sb.append("\n");
        sb.append("T 15,12,0,3,4;");
        appendTruncated(12, barcodable.getLabelText(), s -> appendBradyEscapedUnicode(sb, s));
        sb.append("\n");
        if (barcodable.getBarcodeDate() != null) {
          sb.append("T 15,17,0,3,4;");
          sb.append(LimsUtils.formatDate(barcodable.getBarcodeDate()));
          sb.append("\n");
        }
        sb.append("A 1\n");
      } catch (UnsupportedEncodingException e) {
        log.error("get raw state", e);
        return null;
      }
      return sb.toString();
    }
  },
  BRADY_THT_179_492 {
    @Override
    public String encode(Barcodable barcodable) {
      StringBuilder sb = new StringBuilder();

      try {
        String barcode = new String(Base64.encodeBase64(barcodable.getIdentificationBarcode().getBytes("UTF-8")));
        sb.append("mm\n");
        sb.append("J\n");
        sb.append("O R\n");
        sb.append("S l1;0.0,0.00,25.0,25.5,25.0\n");
        sb.append("B 18,2,0,DATAMATRIX,0.3;").append(barcode).append("\n");
        if (barcodable.getLabelText().length() > 14) {
          sb.append("T 1.5,3,0,3,2;");
          appendBradyEscapedUnicode(sb, barcodable.getLabelText().substring(0, 14));
          sb.append("\n");
          sb.append("T 1.5,6,0,3,2;");
          appendTruncated(14, barcodable.getLabelText().substring(14), s -> appendBradyEscapedUnicode(sb, s));
          sb.append("\n");
        } else {
          sb.append("T 1.5,3,0,3,2;");
          appendTruncated(14, barcodable.getLabelText(), s -> appendBradyEscapedUnicode(sb, s));
          sb.append("\n");
        }
        if (barcodable.getBarcodeDate() != null) {
          sb.append("T 1.5,9,0,3,2;");
          sb.append(LimsUtils.formatDate(barcodable.getBarcodeDate()));
          sb.append("\n");
        }
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
        appendTruncated(17, name, s -> appendBradyEscapedUnicode(sb, s));
        sb.append("T 17,8,0,5,pt6;");
        appendBradyEscapedUnicode(sb, alias);
        sb.append("\n");
        sb.append("T 17,11,0,5,pt6;");
        appendBradyEscapedUnicode(sb, name);
        sb.append("\n");
        sb.append("A 1").append("\n");
        appendTruncated(17, alias, s -> appendBradyEscapedUnicode(sb, s));
        sb.append("T 17,8,0,5,pt6;");
        appendBradyEscapedUnicode(sb, alias);
        sb.append("\n");
        sb.append("T 17,11,0,5,pt6;");
        appendBradyEscapedUnicode(sb, name);
        sb.append("\n");
        sb.append("A 1\n");
      } catch (UnsupportedEncodingException e) {
        log.error("get raw state", e);
        return null;
      }
      return sb.toString();
    }
  },
  ZEBRA_8363 {
    // Square

    @Override
    public String encode(Barcodable b) {
      StringBuilder sb = new StringBuilder();
      sb.append("CT~~CD,~CC^~CT~\r\n");
      sb.append("^XA\r\n");
      sb.append("~TA000~JSN^LT0^MNW^MTT^PON^PMN^LH0,0^JMA^PR6,6^MD15^LRN^CI0\r\n");
      sb.append("^MMT\r\n");
      sb.append("^PW200\r\n");
      sb.append("^LL0185\r\n");
      sb.append("^LS0\r\n");
      if (b.getAlias().length() > 15) {
        sb.append("^FT16,57^A0N,20,19^FH\\^FD");
        sb.append(b.getAlias().substring(0, 15));
        sb.append("^FS\r\n");
        sb.append("^FT16,81^A0N,20,19^FH\\^FD");
        appendTruncated(15, b.getAlias().substring(15), sb::append);
        sb.append("^FS\r\n");
      } else {
        sb.append("^FT16,57^A0N,20,19^FH\\^FD");
        sb.append(b.getAlias());
        sb.append("^FS\r\n");

      }
      if (b.getBarcodeDate() != null) {
        sb.append("^FT12,169^A0N,17,16^FH\\^FD");
        sb.append(LimsUtils.formatDate(b.getBarcodeDate()));
        sb.append("^FS\r\n");
      }
      sb.append("^BY42,42^FT150,176^BXN,3,200,0,0,1,~\r\n");
      sb.append("^FH\\^FD");
      sb.append(b.getIdentificationBarcode());
      sb.append("^FS\r\n");
      sb.append("^FT16,110^A0N,20,19^FH\\^FD");
      appendTruncated(15, b.getBarcodeExtraInfo(), sb::append);
      sb.append("^FS\r\n");
      sb.append("^XZ\n");
      return sb.toString();
    }

  },
  ZEBRA_JTT_183 {
    // Rectangle + circle

    @Override
    public String encode(Barcodable b) {
      StringBuilder sb = new StringBuilder();
      sb.append("﻿CT~~CD,~CC^~CT~\r\n");
      sb.append("^XA\r\n");
      sb.append("~TA000~JSN^LT0^MNW^MTT^PON^PMN^LH0,0^JMA^PR2,2^MD30^LRN^CI0\r\n");
      sb.append("^MMT\r\n");
      sb.append("^PW336\r\n");
      sb.append("^LL0112\r\n");
      sb.append("^LS0\r\n");
      sb.append("^FT247,45^A0N,14,14^FB85,1,0,C^FH\\^FD");
      appendTruncated(10, b.getAlias(), sb::append);
      sb.append("^FS\r\n");
      sb.append("^FT247,66^A0N,11,12^FB85,1,0,C^FH\\^FD").append(LimsUtils.formatDate(b.getBarcodeDate())).append("^FS\r\n");
      sb.append("^FT76,38^A0N,21,21^FH\\^FD");
      appendTruncated(8, b.getAlias(), sb::append);
      sb.append("^FS\r\n");
      sb.append("^FT76,65^A0N,20,19^FH\\^FD").append(LimsUtils.formatDate(b.getBarcodeDate())).append("^FS\r\n");
      sb.append("^FT76,91^A0N,18,16^FH\\^FD");
      appendTruncated(12, b.getBarcodeExtraInfo(), sb::append);
      sb.append("^FS\r\n");
      sb.append("^BY40,40^FT25,82^BXN,2,200,0,0,1,~\r\n");
      sb.append("^FH\\^FDPCSI_0735_Ly_R_nn_1-1^FS\r\n");
      sb.append("^FT246,90^A0N,14,14^FB86,1,0,C^FH\\^FD");
      appendTruncated(10, b.getBarcodeExtraInfo(), sb::append);
      sb.append("^FS\r\n");
      sb.append("^XZ\r\n");
      return sb.toString();
    }

  },
  ZEBRA_JTT_7 {
    // Rectangle

    @Override
    public String encode(Barcodable b) {
      StringBuilder sb = new StringBuilder();
      sb.append("CT~~CD,~CC^~CT~\r\n");
      sb.append("^XA\r\n");
      sb.append("~TA000~JSN^LT0^MNW^MTT^PON^PMN^LH0,0^JMA^PR6,6^MD15^LRN^CI0\r\n");
      sb.append("^MMT\r\n");
      sb.append("^PW200\r\n");
      sb.append("^LL0098\r\n");
      sb.append("^LS0\r\n");
      sb.append("^FT14,27^A0N,20,19^FH\\^FD");
      appendTruncated(21, b.getAlias(), sb::append);
      sb.append("^FS\r\n");
      sb.append("^FT14,94^A0N,20,19^FH\\^FD").append(LimsUtils.formatDate(b.getBarcodeDate())).append("^FS\r\n");
      sb.append("^FT13,74^A0N,20,19^FH\\^FD");
      appendTruncated(12, b.getBarcodeExtraInfo(), sb::append);
      sb.append("^FS\r\n");
      sb.append("^BY32,32^FT158,96^BXN,2,200,0,0,1,~\r\n");
      sb.append("^FH\\^FD1").append(b.getIdentificationBarcode()).append("^FS\r\n");
      sb.append("^XZ\r\n");
      return sb.toString();
    }

  };
  private static Logger log = LoggerFactory.getLogger(Driver.class);

  private static void appendBradyEscapedUnicode(StringBuilder b, String text) {
    text.codePoints().forEachOrdered(codePoint -> {
      if (codePoint < 128) {
        b.appendCodePoint(codePoint);
      } else {
        b.append("[U:$").append(String.format("%04X", codePoint)).append("]");
      }
    });
  }

  protected static void appendTruncated(int length, String str, Consumer<String> writeEscaped) {
    if (str.length() >= length) {
      writeEscaped.accept(str.substring(0, length - 2));
      writeEscaped.accept("...");
    } else {
      writeEscaped.accept(str);
    }
  }

  /**
   * Generate the printer commands needed to print a label for the supplied item.
   */
  public abstract String encode(Barcodable b);

}
