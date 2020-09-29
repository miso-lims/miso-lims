package uk.ac.bbsrc.tgac.miso.webapp.util;

public enum PageMode {

  CREATE("create"), EDIT("edit"), PROPAGATE("propagate");

  public static final String PROPERTY = "pageMode";

  private final String label;

  private PageMode(String label) {
    this.label = label;
  }

  public String getLabel() {
    return label;
  }

}
