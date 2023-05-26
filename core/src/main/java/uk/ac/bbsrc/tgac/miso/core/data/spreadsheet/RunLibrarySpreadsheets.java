package uk.ac.bbsrc.tgac.miso.core.data.spreadsheet;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

import uk.ac.bbsrc.tgac.miso.core.data.RunPartitionAliquot;
import uk.ac.bbsrc.tgac.miso.core.data.impl.view.ParentSample;

public enum RunLibrarySpreadsheets implements Spreadsheet<RunPartitionAliquot> {

  LIBRARY_SEQUENCING_REPORT("Library Sequencing Report", //
      Column.forString("Instrument Model", runLib -> runLib.getRun().getSequencer().getInstrumentModel().getAlias()), //
      Column.forString("Run Name", runLib -> runLib.getRun().getName()), //
      Column.forString("Run Alias", runLib -> runLib.getRun().getAlias()), //
      Column.forString("Pool Name", runLib -> runLib.getPartition().getPool().getName()), //
      Column.forString("Library Aliquot Name", runLib -> runLib.getAliquot().getName()), //
      Column.forString("Library Aliquot Alias", runLib -> runLib.getAliquot().getAlias()), //
      Column.forString("External Name", true,
          getDetailedSampleAttribute(sam -> sam.getIdentityAttributes().getExternalName())), //
      Column.forString("Subproject", true,
          getDetailedSampleAttribute(
              sam -> sam.getParentSubproject() == null ? null : sam.getParentSubproject().getAlias())) //
  );

  private static Function<RunPartitionAliquot, String> getDetailedSampleAttribute(
      Function<ParentSample, String> getter) {
    return runLibrary -> {
      ParentSample sample = runLibrary.getAliquot().getParentLibrary().getParentSample();
      if (sample.getParentAttributes() != null) {
        return getter.apply((ParentSample) sample);
      } else {
        return null;
      }
    };
  }

  private final String description;
  private final List<Column<RunPartitionAliquot>> columns;

  @SafeVarargs
  private RunLibrarySpreadsheets(String description, Column<RunPartitionAliquot>... columns) {
    this.description = description;
    this.columns = Arrays.asList(columns);
  }

  @Override
  public List<Column<RunPartitionAliquot>> columns() {
    return columns;
  }

  @Override
  public String description() {
    return description;
  }

}
