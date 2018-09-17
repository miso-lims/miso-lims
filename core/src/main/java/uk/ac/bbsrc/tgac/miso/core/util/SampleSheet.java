package uk.ac.bbsrc.tgac.miso.core.util;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.eaglegenomics.simlims.core.User;

import uk.ac.bbsrc.tgac.miso.core.data.IlluminaRun;
import uk.ac.bbsrc.tgac.miso.core.data.Index;
import uk.ac.bbsrc.tgac.miso.core.data.Partition;
import uk.ac.bbsrc.tgac.miso.core.data.Run;
import uk.ac.bbsrc.tgac.miso.core.data.impl.view.PoolDilution;
import uk.ac.bbsrc.tgac.miso.core.data.impl.view.PoolableElementView;

public enum SampleSheet {
  CASAVA_1_7("CASAVA 1.7") {
    @Override
    public boolean allowedFor(Run run) {
      return run instanceof IlluminaRun;
    }

    @Override
    protected Stream<String> getColumns() {
      return Stream.of("FCID", "Lane", "SampleID", "SampleRef", "Index", "Description", "Control", "Recipe", "Operator");
    }

    @Override
    protected String header(Run run) {
      return "";
    }

    @Override
    protected void makeColumns(Partition p, PoolableElementView dilution, String userName, String[] output) {
      output[0] = p.getSequencerPartitionContainer().getIdentificationBarcode();
      output[1] = p.getPartitionNumber().toString();
      output[2] = String.format("%d_%s_%s", p.getSequencerPartitionContainer().getId(), dilution.getLibraryName(),
          dilution.getDilutionName());
      output[3] = dilution.getSampleAlias().replaceAll("\\s", "");
      output[4] = dilution.getIndices().stream().map(Index::getSequence).collect(Collectors.joining("-"));
      output[5] = dilution.getLibraryDescription();
      output[6] = "N";
      output[7] = "NA";
      output[8] = userName;
    }

  },
  CASAVA_1_8("CASAVA 1.8") {
    @Override
    public boolean allowedFor(Run run) {
      return run instanceof IlluminaRun;
    }

    @Override
    protected Stream<String> getColumns() {
      return Stream.concat(SampleSheet.CASAVA_1_7.getColumns(), Stream.of("Project"));
    }

    @Override
    protected String header(Run run) {
      return "";
    }

    @Override
    protected void makeColumns(Partition partition, PoolableElementView dilution, String userName, String[] output) {
      SampleSheet.CASAVA_1_7.makeColumns(partition, dilution, userName, output);
      output[9] = dilution.getProjectAlias().replaceAll("\\s", "");
    }
  },
  CELL_RANGER("CellRanger") {
    @Override
    public boolean allowedFor(Run run) {
      return run instanceof IlluminaRun;
    }

    @Override
    protected Stream<String> getColumns() {
      return Stream.of("Sample_ID", "index", "Sample_Project");
    }

    @Override
    protected String header(Run run) {
      return "[Data]\n";
    }

    @Override
    protected void makeColumns(Partition partition, PoolableElementView dilution, String userName, String[] output) {
      output[0] = dilution.getDilutionName();
      output[1] = dilution.getIndices().stream().findFirst().map(Index::getSequence).orElse("");
      output[2] = dilution.getProjectShortName();
    }
  };
  private final String alias;

  private SampleSheet(String alias) {
    this.alias = alias;
  }

  public String alias() {
    return alias;
  }

  public abstract boolean allowedFor(Run run);

  private Stream<String> createRowsForPartition(User user, List<String> columns, Partition partition) {
    return partition.getPool().getPoolDilutions().stream()//
        .map(PoolDilution::getPoolableElementView)
        .map(dilution -> {
          final String[] output = new String[columns.size()];
          makeColumns(partition, dilution, user.getLoginName(), output);
          return String.join(",", output);
        });
  }

  public String createSampleSheet(Run run, User user) {
    final List<String> columns = getColumns().collect(Collectors.toList());
    return header(run) + Stream.concat(//
        Stream.of(String.join(",", columns)), //
        run.getSequencerPartitionContainers().stream()//
            .flatMap(container -> container.getPartitions().stream())//
            .filter(partition -> partition.getPool() != null)//
            .flatMap(partition -> createRowsForPartition(user, columns, partition)))
        .collect(Collectors.joining("\n"));
  }

  protected abstract Stream<String> getColumns();

  protected abstract String header(Run run);

  protected abstract void makeColumns(Partition partition, PoolableElementView dilution, String userName, String[] output);
}
