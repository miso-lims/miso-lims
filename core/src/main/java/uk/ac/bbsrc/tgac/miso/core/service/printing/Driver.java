package uk.ac.bbsrc.tgac.miso.core.service.printing;

/**
 * All know printer models that can print barcode labels
 *
 * Brady printers are programmed in Yabasic:
 * https://www.cab.de/en/support/support-downloads/?suchtyp=bereich&bereich=45&produktgruppe=48&produkt=166
 */
public enum Driver {
  BRADY {
    @Override
    public LabelCanvas start(double width, double height) {
      return new BradyLabelGenerator(width, height, 1);
    }

  },
  ZEBRA_6DPMM {
    @Override
    public LabelCanvas start(double width, double height) {
      return new ZebraLabelGenerator(6, width, height);
    }
  },
  ZEBRA_8DPMM {
    @Override
    public LabelCanvas start(double width, double height) {
      return new ZebraLabelGenerator(8, width, height);
    }
  },
  ZEBRA_12DPMM {
    @Override
    public LabelCanvas start(double width, double height) {
      return new ZebraLabelGenerator(12, width, height);
    }
  },
  ZEBRA_24DPMM {
    @Override
    public LabelCanvas start(double width, double height) {
      return new ZebraLabelGenerator(24, width, height);
    }
  };
  /**
   * Generate a fresh label to draw on to create printer commands.
   */
  public abstract LabelCanvas start(double width, double height);

}
