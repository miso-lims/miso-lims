package uk.ac.bbsrc.tgac.miso.core.service.printing;

import java.nio.charset.StandardCharsets;
import java.util.stream.Stream;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.StringEscapeUtils;

import uk.ac.bbsrc.tgac.miso.core.data.Barcodable;
import uk.ac.bbsrc.tgac.miso.core.data.Pair;
import uk.ac.bbsrc.tgac.miso.core.service.printing.LabelCanvas.FontStyle;
import uk.ac.bbsrc.tgac.miso.core.service.printing.LabelCanvas.Justification;
import uk.ac.bbsrc.tgac.miso.core.service.printing.LabelCanvas.TextDirection;
import uk.ac.bbsrc.tgac.miso.core.util.LimsUtils;

/**
 * All label templates by label stock
 */
public enum Layout {
  AVERY_8363 {
    // UNTESTED
    // Square

    @Override
    public LabelCanvas draw(Driver driver, Barcodable barcodable) {
      LabelCanvas label = driver.start(8.3, 7.7);
      label.textClipped(2.4, 7.125, .8, 15, barcodable.getAlias());
      if (barcodable.getBarcodeDate() != null) {
        label.text(0.5, 7.125, .8, LimsUtils.formatDate(barcodable.getBarcodeDate()));
      }
      label.barcode2d(1.75, 1.75, 0.125, getBarcode(barcodable));
      label.textClipped(0.666, 4.666, .8, 15, unescapeHtml(barcodable.getBarcodeExtraInfo()));
      return label;
    }

  },
  BPT_635_488 {
    @Override
    public LabelCanvas draw(Driver driver, Barcodable barcodable) {
      // UNTESTED
      String barcode = getBarcode64(barcodable);

      LabelCanvas label = driver.start(12, 38);
      label.barcode2d(2, 6, 0.21, barcode);
      label.barcode2d(13, 1, 0.25, barcode);
      label.textClipped(29, 2, 1.4, 20, barcodable.getLabelText());
      label.textClipped(17, 8, 2, 20, barcodable.getLabelText());
      label.text(17, 11, 2, barcodable.getName());

      return label;
    }
  },
  JTT_183 {
    // Rectangle + circle

    @Override
    public LabelCanvas draw(Driver driver, Barcodable barcodable) {
      LabelCanvas label = driver.start(14, 4.666);
      // This label can handle 10 char/line, but we restrict it to 8 so that the barcodes break in a nice place for projects that people
      // care about.
      label.multilineText(10.7, 2.3, 0.5, 8, 2, Stream.of(new Pair<>(FontStyle.REGULAR, barcodable.getAlias())));
      label.multilineText(1, 1.375, .8, 18, 2, Stream.of(//
          new Pair<>(FontStyle.BOLD, barcodable.getAlias())));
      label.multilineText(1, 3.375, .8, 12, 2, Stream.of(//
          new Pair<>(FontStyle.REGULAR, LimsUtils.formatDate(barcodable.getBarcodeDate())), //
          new Pair<>(FontStyle.REGULAR, unescapeHtml(barcodable.getBarcodeExtraInfo()))));
      label.barcode2d(6.78, 4, 0.1, barcodable.getAlias());
      return label;
    }

  },
  JTT_7 {
    // Rectangle

    @Override
    public LabelCanvas draw(Driver driver, Barcodable barcodable) {
      LabelCanvas label = driver.start(8.3, 4.08);
      label.multilineText(0.3, 1, .8, 18, 2, Stream.of(//
          new Pair<>(FontStyle.BOLD, barcodable.getAlias())));
      label.multilineText(0.3, 3, .8, 12, 2, Stream.of(//
          new Pair<>(FontStyle.REGULAR, LimsUtils.formatDate(barcodable.getBarcodeDate())), //
          new Pair<>(FontStyle.REGULAR, unescapeHtml(barcodable.getBarcodeExtraInfo()))));
      label.barcode2d(5.78, 3.8, 0.1, getBarcode(barcodable));
      return label;
    }

  },
  JTT_7_GROUPDESC {
    // Rectangle

    @Override
    public LabelCanvas draw(Driver driver, Barcodable barcodable) {
      LabelCanvas label = driver.start(8.3, 4.08);
      label.multilineText(0.3, 1, .8, 18, 2, Stream.of(//
          new Pair<>(FontStyle.BOLD, barcodable.getAlias())));
      label.multilineText(0.3, 3, .8, 12, 2, Stream.of(//
          new Pair<>(FontStyle.REGULAR, LimsUtils.formatDate(barcodable.getBarcodeDate())), //
          new Pair<>(FontStyle.REGULAR, unescapeHtml(barcodable.getBarcodeGroupDescription()))));
      label.barcode2d(5.78, 3.8, 0.1, getBarcode(barcodable));
      return label;
    }

  },
  JTT_7S {
    // Rectangle

    @Override
    public LabelCanvas draw(Driver driver, Barcodable barcodable) {
      LabelCanvas label = driver.start(8.3, 4.08);
      label.multilineText(0.3, 1, .8, 18, 2, Stream.of(//
          new Pair<>(FontStyle.BOLD, barcodable.getAlias())));
      label.multilineText(0.3, 3, .8, 12, 2, Stream.of(//
          new Pair<>(FontStyle.REGULAR, LimsUtils.formatDate(barcodable.getBarcodeDate())), //
          new Pair<>(FontStyle.REGULAR, unescapeHtml(barcodable.getBarcodeSizeInfo()))));
      label.barcode2d(5.78, 3.8, 0.1, getBarcode(barcodable));
      return label;
    }

  },
  THT_155_490 {
    @Override
    public LabelCanvas draw(Driver driver, Barcodable barcodable) {
      LabelCanvas label = driver.start(41, 15);

      String barcode = getBarcode(barcodable);
      label.barcode2d(2, 2, 0.3, barcode);
      label.textClipped(7, 3, 3, 12, TextDirection.NORMAL, FontStyle.BOLD, Justification.LEFT, barcodable.getName());
      label.multilineText(7, 5, 2, 28, 5, Stream.of(//
          new Pair<>(FontStyle.REGULAR, barcodable.getLabelText()), //
          new Pair<>(FontStyle.REGULAR, unescapeHtml(barcodable.getBarcodeExtraInfo()))));
      return label;
    }
  },
  THT_155_490T {
    @Override
    public LabelCanvas draw(Driver driver, Barcodable barcodable) {
      LabelCanvas label = driver.start(41, 15);
      label.textClipped(9, 12, 2, 10, TextDirection.VERTICAL_UP, FontStyle.REGULAR, Justification.LEFT, barcodable.getName());
      label.multilineText(11, 12, 2, 9, 7, TextDirection.VERTICAL_UP, Stream.of(//
          new Pair<>(FontStyle.BOLD, barcodable.getLabelText()), //
          new Pair<>(FontStyle.REGULAR, unescapeHtml(barcodable.getBarcodeSizeInfo())), //
          new Pair<>(FontStyle.REGULAR, unescapeHtml(barcodable.getBarcodeExtraInfo()))));
      return label;
    }
  },
  THT_179_492 {
    @Override
    public LabelCanvas draw(Driver driver, Barcodable barcodable) {
      String barcode = getBarcode(barcodable);
      LabelCanvas label = driver.start(25.0, 25.0);
      label.barcode2d(11, 12, 0.3, barcode);
      label.multilineText(2, 4, 2, 14, 3, Stream.of(//
          new Pair<>(FontStyle.REGULAR, barcodable.getLabelText()), //
          new Pair<>(FontStyle.REGULAR, LimsUtils.formatDate(barcodable.getBarcodeDate()))));
      return label;
    }
  },
  FTT_152C1_1WH {
    @Override
    public LabelCanvas draw(Driver driver, Barcodable barcodable) {
      String barcode = getBarcode(barcodable);
      LabelCanvas label = driver.start(22.86, 19.05);
      label.barcode2d(3, 12, 0.3, barcode);
      label.multilineText(0, 5, 1.8, 14, 3, Stream.of(//
          new Pair<>(FontStyle.REGULAR, barcodable.getLabelText()), //
          new Pair<>(FontStyle.REGULAR, LimsUtils.formatDate(barcodable.getBarcodeDate()))));
      return label;
    }
  },
  THT_181_492_3 {
    @Override
    public LabelCanvas draw(Driver driver, Barcodable barcodable) {
      // UNTESTED
      LabelCanvas label = driver.start(38, 12);

      String alias = barcodable.getLabelText();
      String name = barcodable.getName();
      String barcode64 = getBarcode64(barcodable);

      label.barcode2d(3, 2, 0.21, barcode64);
      label.barcode2d(17, 1, 0.25, barcode64);
      label.textClipped(29, 2, 1.4, 17, name);
      label.text(17, 8, 2, TextDirection.NORMAL, FontStyle.BOLD, Justification.LEFT, alias);
      label.text(17, 11, 2, TextDirection.NORMAL, FontStyle.BOLD, Justification.LEFT, name);
      label.textClipped(17, 8, 2, 17, alias);
      label.text(17, 11, 2, TextDirection.NORMAL, FontStyle.BOLD, Justification.LEFT, name);
      return label;
    }
  };

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

  private static String unescapeHtml(String string) {
    if (string == null) return null;
    return StringEscapeUtils.unescapeHtml(string);
  }

  /**
   * Generate the printer commands needed to print a label for the supplied item.
   */
  public abstract LabelCanvas draw(Driver driver, Barcodable b);

}
