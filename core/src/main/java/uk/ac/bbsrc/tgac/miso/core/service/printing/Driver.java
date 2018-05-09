package uk.ac.bbsrc.tgac.miso.core.service.printing;

import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import org.apache.commons.codec.binary.Base64;

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
    public String encode(Barcodable b, int copies) {
      StringBuilder sb = new StringBuilder();

      sb.append("m m\n");
      sb.append("J\n");
      sb.append("S l1;0,0,6,9,50\n");
      sb.append("B 1,0,0,CODE128,5,0.25;").append(getBarcode(b)).append("\n");
      sb.append("A ").append(copies).append("\n");
      return sb.toString();
    }

  },
  BRADY_BPT_635_488 {
    @Override
    public String encode(Barcodable barcodable, int copies) {
      StringBuilder sb = new StringBuilder();

      String barcode = getBarcode64(barcodable);
      String alias = barcodable.getLabelText();
      String name = barcodable.getName();
      String barcode64 = new String(Base64.encodeBase64(barcode.getBytes(StandardCharsets.UTF_8)));

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
      sb.append("A ").append(copies).append("\n");

      return sb.toString();
    }
  },
  BRADY_THT_155_490 {
    @Override
    public String encode(Barcodable barcodable, int copies) {
      StringBuilder sb = new StringBuilder();

      String barcode = getBarcode(barcodable);
      sb.append("mm\n");
      sb.append("J\n");
      sb.append("O R\n");
      sb.append("S 0.0,0.00,12.78,12.78,41.50\n");
      sb.append("B 2,2,0,DATAMATRIX,0.5;").append(barcode).append("\n");
      sb.append("T 9,3,0,5,3;");
      appendTruncated(12, barcodable.getName(), s -> appendBradyEscapedUnicode(sb, s));
      sb.append("\n");
      AtomicInteger offset = new AtomicInteger();
      BiConsumer<Integer, String> writer = (line, text) -> {
        sb.append("T 9,");
        sb.append(5 + (line + offset.get()) * 3);
        sb.append(",0,3,2;");
        appendBradyEscapedUnicode(sb, text);
        sb.append("\n");
      };
      offset.addAndGet(multiline(28, 5, barcodable.getLabelText(), writer));
      multiline(28, 5 - offset.get(), barcodable.getBarcodeExtraInfo(), writer);
      sb.append("A ").append(copies).append("\n");
      return sb.toString();
    }
  },
  BRADY_THT_155_490T {
    @Override
    public String encode(Barcodable barcodable, int copies) {
      StringBuilder sb = new StringBuilder();

      sb.append("mm\n");
      sb.append("J\n");
      sb.append("O R\n");
      sb.append("S 0.0,0.00,12.78,12.78,41.50\n");
      sb.append("T 3,12,90,3,2;");
      appendTruncated(10, barcodable.getName(), s -> appendBradyEscapedUnicode(sb, s));
      sb.append("\n");
      AtomicInteger offset = new AtomicInteger();
      AtomicInteger font = new AtomicInteger(5);
      BiConsumer<Integer, String> writer = (line, text) -> {
        sb.append("T ");
        sb.append(6 + (line + offset.get()) * 3);
        sb.append(",12,90,");
        sb.append(font.get());
        sb.append(",2;");
        appendBradyEscapedUnicode(sb, text);
        sb.append("\n");
      };

      offset.addAndGet(multiline(9, 4, barcodable.getLabelText(), writer));
      font.set(3);
      offset.addAndGet(multiline(9, 2, barcodable.getBarcodeSizeInfo(), writer));
      multiline(9, 7 - offset.get(), barcodable.getBarcodeExtraInfo(), writer);
      sb.append("A ").append(copies).append("\n");
      return sb.toString();
    }
  },
  BRADY_THT_179_492 {
    @Override
    public String encode(Barcodable barcodable, int copies) {
      StringBuilder sb = new StringBuilder();

      String barcode = getBarcode(barcodable);
      sb.append("mm\n");
      sb.append("J\n");
      sb.append("O R\n");
      sb.append("S l1;0.0,0.00,25.0,25.5,25.0\n");
      sb.append("B 11,12,0,DATAMATRIX,0.3;").append(barcode).append("\n");
      if (barcodable.getLabelText().length() > 17) {
        sb.append("T 2,4,0,3,2;");
        appendBradyEscapedUnicode(sb, barcodable.getLabelText().substring(0, 17));
        sb.append("\n");
        sb.append("T 2,7,0,3,2;");
        appendTruncated(14, barcodable.getLabelText().substring(17), s -> appendBradyEscapedUnicode(sb, s));
        sb.append("\n");
      } else {
        sb.append("T 2,4,0,3,2;");
        appendTruncated(14, barcodable.getLabelText(), s -> appendBradyEscapedUnicode(sb, s));
        sb.append("\n");
      }
      if (barcodable.getBarcodeDate() != null) {
        sb.append("T 2,10,0,3,2;");
        sb.append(LimsUtils.formatDate(barcodable.getBarcodeDate()));
        sb.append("\n");
      }
      sb.append("A ").append(copies).append("\n");
      return sb.toString();
    }
  },

  BRADY_THT_181_492_3 {
    @Override
    public String encode(Barcodable barcodable, int copies) {
      StringBuilder sb = new StringBuilder();

      String barcode = getBarcode64(barcodable);
      String alias = barcodable.getLabelText();
      String name = barcodable.getName();
      String barcode64 = new String(Base64.encodeBase64(barcode.getBytes(StandardCharsets.UTF_8)));
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
      sb.append("A ").append(copies).append("\n");
      return sb.toString();
    }
  },
  ZEBRA_8363 {
    // Square

    @Override
    public String encode(Barcodable b, int copies) {
      StringBuilder sb = new StringBuilder();
      sb.append("CT~~CD,~CC^~CT~\r\n");
      sb.append("^XA~TA000~JSN^LT0^MNW^MTT^PON^PMN^LH0,0^JMA^PR2,2~SD30^JUS^LRN^CI28^XZ\r\n");
      sb.append("^XA\r\n");
      sb.append("^MMT\r\n");
      sb.append("^PW200\r\n");
      sb.append("^LL0185\r\n");
      sb.append("^LS0\r\n");
      if (b.getAlias().length() > 15) {
        sb.append("^FT16,59^A0N,20,19^FH\\^FD");
        sb.append(b.getAlias().substring(0, 15));
        sb.append("^FS\r\n");
        sb.append("^FT16,83^A0N,20,19^FH\\^FD");
        appendTruncated(15, b.getAlias().substring(15), sb::append);
        sb.append("^FS\r\n");
      } else {
        sb.append("^FT16,59^A0N,20,19^FH\\^FD");
        sb.append(b.getAlias());
        sb.append("^FS\r\n");

      }
      if (b.getBarcodeDate() != null) {
        sb.append("^FT12,171^A0N,17,16^FH\\^FD");
        sb.append(LimsUtils.formatDate(b.getBarcodeDate()));
        sb.append("^FS\r\n");
      }
      sb.append("^BY42,42^FT300,360^BXN,3,200,0,0,1,~\r\n");
      sb.append("^FH\\^FD");
      sb.append(getBarcode(b));
      sb.append("^FS\r\n");
      sb.append("^FT16,112^A0N,20,19^FH\\^FD");
      appendTruncated(15, b.getBarcodeExtraInfo(), sb::append);
      sb.append("^FS\r\n");
      sb.append("^XZ\n");
      return String.join("", Collections.nCopies(copies, sb.toString()));
    }

  },
  ZEBRA_JTT_183 {
    // Rectangle + circle

    @Override
    public String encode(Barcodable b, int copies) {
      StringBuilder sb = new StringBuilder();
      sb.append("CT~~CD,~CC^~CT~\r\n");
      sb.append("^XA~TA000~JSN^LT0^MNW^MTT^PON^PMN^LH0,0^JMA^PR2,2~SD30^JUS^LRN^CI28^XZ\r\n");
      sb.append("^XA\r\n");
      sb.append("^MMT\r\n");
      sb.append("^PW336\r\n");
      sb.append("^LL0112\r\n");
      sb.append("^LS0\r\n");
      sb.append("^FT247,50^A0N,14,14^FB85,1,0,C^FH\\^FD");
      sb.append(b.getAlias().length() > 10 ? b.getAlias().substring(0, 10) : b.getAlias());
      sb.append("^FS\r\n");
      if (b.getAlias().length() > 10) {
        sb.append("^FT247,70^A0N,14,14^FB85,1,0,C^FH\\^FD");
        appendTruncated(10, b.getAlias().substring(10), sb::append);
        sb.append("^FS\r\n");
      }
      sb.append("^FT76,33^A0N,21,21^FH\\^FD");
      appendTruncated(15, b.getAlias(), sb::append);
      sb.append("^FS\r\n");
      sb.append("^FT76,62^A0N,20,19^FH\\^FD").append(LimsUtils.formatDate(b.getBarcodeDate())).append("^FS\r\n");
      sb.append("^FT76,88^A0N,18,16^FH\\^FD");
      appendTruncated(12, b.getBarcodeExtraInfo(), sb::append);
      sb.append("^FS\r\n");
      sb.append("^BY40,37^FT25,82^BXN,2,200,0,0,1,~\r\n");
      sb.append("^FH\\^FD");
      sb.append(b.getAlias());
      sb.append("^FS\r\n");
      sb.append("^PQ").append(copies).append("\r\n");
      sb.append("^XZ\r\n");
      return sb.toString();
    }

  },
  ZEBRA_JTT_7 {
    // Rectangle

    @Override
    public String encode(Barcodable b, int copies) {
      StringBuilder sb = new StringBuilder();
      sb.append("CT~~CD,~CC^~CT~\r\n");
      sb.append("^XA~TA000~JSN^LT0^MNW^MTT^PON^PMN^LH0,0^JMA^PR2,2~SD30^JUS^LRN^CI28^XZ\r\n");
      sb.append("^XA\r\n");
      sb.append("^MMT\r\n");
      sb.append("^PW200\r\n");
      sb.append("^LL0098\r\n");
      sb.append("^LS0\r\n");
      sb.append("^FT14,27^A0N,20,19^FH\\^FD");
      if (b.getAlias().length() > 18) {
        sb.append(b.getAlias().substring(0, 18));
      } else {
        sb.append(b.getAlias());
      }
      sb.append("^FS\r\n");
      if (b.getAlias().length() > 18) {
        sb.append("^FT14,45^A0N,20,19^FH\\^FD");
        appendTruncated(21, b.getAlias().substring(18), sb::append);
        sb.append("^FS\r\n");
      }
      sb.append("^FT14,94^A0N,20,19^FH^FD").append(LimsUtils.formatDate(b.getBarcodeDate())).append("^FS\r\n");
      sb.append("^FT13,74^A0N,20,19^FH^FD");
      appendTruncated(12, b.getBarcodeSizeInfo(), s -> appendZebraEscapedUnicode(sb, s));
      sb.append("^FS\r\n");
      sb.append("^BY32,32^FT158,96^BXN,2,200,0,0,1,~\r\n");
      sb.append("^FH\\^FD").append(getBarcode(b)).append("^FS\r\n");
      sb.append("^PQ").append(copies).append("\r\n");
      sb.append("^XZ\r\n");
      return sb.toString();
    }

  };

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
    if (str == null) {
      return;
    }
    if (str.length() >= length) {
      writeEscaped.accept(str.substring(0, length - 2));
      writeEscaped.accept("...");
    } else {
      writeEscaped.accept(str);
    }
  }

  private static void appendZebraEscapedUnicode(StringBuilder b, String text) {
    for (byte by : text.getBytes(StandardCharsets.UTF_8)) {
      b.append("_").append(String.format("%02X", by));
    }
  }

  private static String getBarcode(Barcodable barcodable) {
    String str = barcodable.getIdentificationBarcode();
    if (LimsUtils.isStringBlankOrNull(str)) {
      str = barcodable.getName();
    }
    return str;
  }

  private static String getBarcode64(Barcodable barcodable) {
    return new String(Base64.encodeBase64(getBarcode(barcodable).getBytes(StandardCharsets.UTF_8)));
  }

  private static int multiline(int lineLength, int maxLines, String input, BiConsumer<Integer, String> writer) {
    if (LimsUtils.isStringBlankOrNull(input)) {
      return 0;
    }
    int line;
    for (line = 0; line < maxLines - 1 && input.length() > line * lineLength; line++) {
      writer.accept(line, input.substring(line * lineLength, Math.min(input.length(), (line + 1) * lineLength)));
    }
    if (input.length() > maxLines * lineLength) {
      writer.accept(maxLines - 1, input.substring((maxLines - 1) * lineLength, maxLines * lineLength - 2) + "...");
      line++;
    }
    return line;
  }

  /**
   * Generate the printer commands needed to print a label for the supplied item.
   */
  public abstract String encode(Barcodable b, int copies);

}
