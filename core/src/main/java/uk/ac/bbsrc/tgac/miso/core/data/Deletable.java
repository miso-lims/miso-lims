package uk.ac.bbsrc.tgac.miso.core.data;

public interface Deletable extends Identifiable {

  public String getDeleteType();

  public String getDeleteDescription();

}
