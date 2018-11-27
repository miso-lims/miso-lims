package uk.ac.bbsrc.tgac.miso.core.data.spreadsheet;

import java.util.Arrays;
import java.util.List;

import uk.ac.bbsrc.tgac.miso.core.data.Partition;

public enum PartitionSpreadsheets implements Spreadsheet<Partition> {

  LOADING_CONCENTRATIONS("Loading Concentrations", //
      Column.forString("Container", p -> p.getSequencerPartitionContainer().getAlias()), //
      Column.forInteger("Partition", Partition::getPartitionNumber), //
      Column.forString("Pool", p -> p.getPool() == null ? null : p.getPool().getAlias()), //
      Column.forBigDecimal("Loading Concentration", Partition::getLoadingConcentration), //
      Column.forString("Units",
          p -> p.getLoadingConcentrationUnits() == null ? null : p.getLoadingConcentrationUnits().getUnits().replace("&#181;", "µ")));

  private final String description;
  private final List<Column<Partition>> columns;

  @SafeVarargs
  private PartitionSpreadsheets(String description, Column<Partition>... columns) {
    this.description = description;
    this.columns = Arrays.asList(columns);
  }

  @Override
  public List<Column<Partition>> columns() {
    return columns;
  }

  @Override
  public String description() {
    return description;
  }

}
