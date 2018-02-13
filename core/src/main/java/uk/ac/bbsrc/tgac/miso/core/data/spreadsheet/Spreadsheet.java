package uk.ac.bbsrc.tgac.miso.core.data.spreadsheet;

import java.util.List;

public interface Spreadsheet<T> {
  public List<Column<T>> columns();

  public String description();

  public String name();
}
