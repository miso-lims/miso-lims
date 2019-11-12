package uk.ac.bbsrc.tgac.miso.core.data.spreadsheet;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import uk.ac.bbsrc.tgac.miso.core.data.Pool;
import uk.ac.bbsrc.tgac.miso.core.data.qc.PoolQC;;

public enum PoolSpreadSheets implements Spreadsheet<Pool> {
  QPCR_RESULTS("qPCR Results", //
      Column.forString("Name", Pool::getName), //
      Column.forString("Alias", Pool::getAlias), //
      Column.forString("Barcode", Pool::getIdentificationBarcode), //
      Column.forBigDecimal("Latest qPCR QC", pool -> pool.getQCs().stream()//
          .filter(qc -> qc.getType().getName().contains("qPCR"))
          .max(Comparator.comparing(PoolQC::getDate).thenComparing(Comparator.comparing(PoolQC::getLastModified)))//
          .map(PoolQC::getResults).orElse(BigDecimal.ZERO)));

  private final List<Column<Pool>> columns;
  private final String description;

  @SafeVarargs
  private PoolSpreadSheets(String description, Column<Pool>... columns) {
    this.description = description;
    this.columns = Arrays.asList(columns);
  }

  @Override
  public List<Column<Pool>> columns() {
    return columns;
  }

  @Override
  public String description() {
    return description;
  }
}
