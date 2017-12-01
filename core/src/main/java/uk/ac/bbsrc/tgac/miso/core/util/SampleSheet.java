package uk.ac.bbsrc.tgac.miso.core.util;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.eaglegenomics.simlims.core.User;

import uk.ac.bbsrc.tgac.miso.core.data.Index;
import uk.ac.bbsrc.tgac.miso.core.data.Partition;
import uk.ac.bbsrc.tgac.miso.core.data.Run;
import uk.ac.bbsrc.tgac.miso.core.data.impl.view.PoolableElementView;

public enum SampleSheet {
  CASAVA_1_7 {
    @Override
    protected Stream<String> getColumns() {
      return Stream.of("FCID", "Lane", "SampleID", "SampleRef", "Index", "Description", "Control", "Recipe", "Operator");
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
  CASAVA_1_8 {
    @Override
    protected Stream<String> getColumns() {
      return Stream.concat(SampleSheet.CASAVA_1_7.getColumns(), Stream.of("Project"));
    }

    @Override
    protected void makeColumns(Partition partition, PoolableElementView dilution, String userName, String[] output) {
      SampleSheet.CASAVA_1_7.makeColumns(partition, dilution, userName, output);
      output[9] = dilution.getProjectAlias().replaceAll("\\s", "");
    }
  };

  private Stream<String> createRowsForPartition(User user, List<String> columns, Partition partition) {
    return partition.getPool().getPoolableElementViews().stream()//
        .map(dilution -> {
          String[] output = new String[columns.size()];
          makeColumns(partition, dilution, user.getLoginName(), output);
          return String.join(",", output);
        });
  }

  public String createSampleSheet(Run run, User user) {
    List<String> columns = getColumns().collect(Collectors.toList());
    return Stream.concat(//
        Stream.of(String.join(",", columns)), //
        run.getSequencerPartitionContainers().stream()//
            .flatMap(container -> container.getPartitions().stream())//
            .filter(partition -> partition.getPool() != null)//
            .flatMap(partition -> createRowsForPartition(user, columns, partition)))
        .collect(Collectors.joining("\n"));
  }

  protected abstract Stream<String> getColumns();

  protected abstract void makeColumns(Partition partition, PoolableElementView dilution, String userName, String[] output);
}
