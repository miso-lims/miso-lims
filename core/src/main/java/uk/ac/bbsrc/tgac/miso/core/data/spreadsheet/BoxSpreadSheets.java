package uk.ac.bbsrc.tgac.miso.core.data.spreadsheet;

import java.util.Arrays;
import java.util.List;

import uk.ac.bbsrc.tgac.miso.core.data.Box;

public enum BoxSpreadSheets implements Spreadsheet<Box> {

  TRACKING_LIST("Tracking List", //
      Column.forString("Name", Box::getName), //
      Column.forString("Alias", Box::getAlias), //
      Column.forString("Description", Box::getDescription), //
      Column.forString("Location Note", Box::getLocationBarcode), //
      Column.forString("Freezer Location", //
          box -> box.getStorageLocation() == null ? "" : box.getStorageLocation().getFullDisplayLocation()), //
      Column.forInteger("Items", Box::getTubeCount), //
      Column.forInteger("Capacity", Box::getPositionCount), //
      Column.forString("Size", box -> box.getSize().getLabel()), //
      Column.forString("Use", box -> box.getUse().getAlias()), //
      Column.forString("Last Modified", box -> box.getLastModified().toString()));

  private final List<Column<Box>> columns;
  private final String description;

  @SafeVarargs
  private BoxSpreadSheets(String description, Column<Box>... columns) {
    this.description = description;
    this.columns = Arrays.asList(columns);
  }

  @Override
  public List<Column<Box>> columns() {
    return columns;
  }

  @Override
  public String description() {
    return description;
  }
}
