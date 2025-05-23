package uk.ac.bbsrc.tgac.miso.core.data.type;

public enum CompressionFormat {
  GZIP("gzip", "zip") {},
  ORA("DRAGEN ORA", "ora") {};

  private final String description;

  private final String extension;


  private CompressionFormat(String description, String extension) {
    this.description = description;
    this.extension = extension;
  }

  public String description() {
    return description;
  }

  public String extension() {
    return extension;
  }


}

