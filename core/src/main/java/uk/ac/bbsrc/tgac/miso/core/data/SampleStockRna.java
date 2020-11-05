package uk.ac.bbsrc.tgac.miso.core.data;

public interface SampleStockRna extends SampleStock {

  public static final String SUBCATEGORY_NAME = "RNA (stock)";

  public Boolean getDnaseTreated();

  public void setDnaseTreated(Boolean dnaseTreated);

}
