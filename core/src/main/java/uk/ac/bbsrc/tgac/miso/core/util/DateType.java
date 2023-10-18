package uk.ac.bbsrc.tgac.miso.core.util;

public enum DateType {
  CREATE("creation"), //
  UPDATE("update"), //
  RECEIVE("received"), //
  ENTERED("entered"), //
  DISTRIBUTED("distribution"), //
  REB_EXPIRY("REB expiry");

  private final String label;

  private DateType(String label) {
    this.label = label;
  }

  public String getLabel() {
    return label;
  }
}
