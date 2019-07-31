package uk.ac.bbsrc.tgac.miso.core.util;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.eaglegenomics.simlims.core.User;

import uk.ac.bbsrc.tgac.miso.core.data.IlluminaRun;
import uk.ac.bbsrc.tgac.miso.core.data.Index;
import uk.ac.bbsrc.tgac.miso.core.data.InstrumentDataManglingPolicy;
import uk.ac.bbsrc.tgac.miso.core.data.Partition;
import uk.ac.bbsrc.tgac.miso.core.data.Run;
import uk.ac.bbsrc.tgac.miso.core.data.impl.view.PoolElement;
import uk.ac.bbsrc.tgac.miso.core.data.impl.view.PoolableElementView;

public enum SampleSheet {
  BCL2FASTQ("BCL2FASTQ") {

    @Override
    public boolean allowedFor(Run run) {
      return run instanceof IlluminaRun;
    }

    @Override
    protected Stream<String> getColumns() {
      return Stream.of("Sample_ID", "Sample_Name", "I7_Index_ID", "index", "I5_Index_ID", "index2");
    }

    @Override
    protected String header(Run run) {
      final IlluminaRun r = (IlluminaRun) run;
      String reads = "";
      if (r.getSequencingParameters() != null) {
        final String readLength = Integer.toString(r.getSequencingParameters().getReadLength());
        reads = String.format("[Reads]\n%s\n%s\n\n", readLength,
            r.getSequencingParameters().isPaired() ? readLength : "");
      }
      return String.format("[Header]\nDate,%s\n\n%s[Data]\n", DateTimeFormatter.ISO_DATE.format(ZonedDateTime.now()), reads);
    }

    @Override
    protected void makeColumns(Run run, Partition partition, PoolableElementView aliquot, String userName, String[] output) {
      output[0] = aliquot.getLibraryName();
      output[1] = aliquot.getLibraryAlias();
      final Optional<Index> firstIndex = aliquot.getIndices().stream().filter(i -> i.getPosition() == 1).findFirst();
      output[2] = firstIndex.map(Index::getName).orElse("");
      output[3] = firstIndex.map(Index::getSequence).orElse("");
      final Optional<Index> secondIndex = aliquot.getIndices().stream().filter(i -> i.getPosition() == 2).findFirst();
      output[4] = secondIndex.map(Index::getName).orElse("");
      output[5] = secondIndex.map(Index::getSequence)
          .map(run.getSequencer().getInstrumentModel().getDataManglingPolicy() == InstrumentDataManglingPolicy.I5_RC
              ? SampleSheet::reverseComplement
              : Function.identity())
          .orElse("");
    }

  },
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
    protected void makeColumns(Run run, Partition p, PoolableElementView aliquot, String userName, String[] output) {
      output[0] = p.getSequencerPartitionContainer().getIdentificationBarcode();
      output[1] = p.getPartitionNumber().toString();
      output[2] = String.format("%d_%s_%s", p.getSequencerPartitionContainer().getId(), aliquot.getLibraryName(),
          aliquot.getAliquotName());
      output[3] = aliquot.getSampleAlias().replaceAll("\\s", "");
      output[4] = aliquot.getIndices().stream()//
          .sorted(Comparator.comparingInt(Index::getPosition))//
          .map(i -> {
            if (run.getSequencer().getInstrumentModel().getDataManglingPolicy() == InstrumentDataManglingPolicy.I5_RC
                && i.getPosition() == 2) {
              return reverseComplement(i.getSequence());
            }
            return i.getSequence();
          })//
          .collect(Collectors.joining("-"));
      output[5] = aliquot.getLibraryDescription();
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
    protected void makeColumns(Run run, Partition partition, PoolableElementView aliquot, String userName, String[] output) {
      SampleSheet.CASAVA_1_7.makeColumns(run, partition, aliquot, userName, output);
      output[9] = aliquot.getProjectAlias().replaceAll("\\s", "");
    }
  },
  CELL_RANGER("CellRanger") {
    @Override
    public boolean allowedFor(Run run) {
      return run instanceof IlluminaRun;
    }

    @Override
    protected Stream<String> getColumns() {
      return Stream.of("Lane", "Sample_ID", "Sample_Name", "index", "Sample_Project");
    }

    @Override
    protected String header(Run run) {
      return "[Data]\n";
    }

    @Override
    protected void makeColumns(Run run, Partition partition, PoolableElementView aliquot, String userName, String[] output) {
      output[0] = Integer.toString(partition.getPartitionNumber());
      output[1] = aliquot.getAliquotName();
      output[2] = aliquot.getLibraryAlias();
      output[3] = aliquot.getIndices().stream().findFirst().map(Index::getSequence).orElse("");
      output[4] = aliquot.getProjectShortName();
    }
  };
  private static char complement(char nt) {
    switch (nt) {
    case 'A':
      return 'T';
    case 'C':
      return 'G';
    case 'G':
      return 'C';
    case 'T':
    case 'U':
      return 'A';
    // Below are all the degenerate nucleotides. I hope we never need these and if we had one, the index mismatches calculations would have
    // to be the changed.
    case 'R': // AG
      return 'Y';
    case 'Y': // CT
      return 'R';
    case 'S': // CG
      return 'S';
    case 'W': // AT
      return 'W';
    case 'K': // GT
      return 'M';
    case 'M': // AC
      return 'K';
    case 'B': // CGT
      return 'V';
    case 'D': // AGT
      return 'H';
    case 'H':// ACT
      return 'D';
    case 'V':// ACG
      return 'B';
    case 'N':
      return 'N';
    default:
      return nt;
    }
  }

  public static String reverseComplement(String index) {
    if (index == null) {
      return null;
    }
    final StringBuilder buffer = new StringBuilder(index.length());
    for (int i = index.length() - 1; i >= 0; i--) {
      buffer.append(complement(Character.toUpperCase(index.charAt(i))));
    }
    return buffer.toString();
  }

  private final String alias;

  private SampleSheet(String alias) {
    this.alias = alias;
  }

  public String alias() {
    return alias;
  }

  public abstract boolean allowedFor(Run run);

  private Stream<String> createRowsForPartition(Run run, User user, List<String> columns, Partition partition) {
    return partition.getPool().getPoolContents().stream()//
        .map(PoolElement::getPoolableElementView)
        .map(aliquot -> {
          final String[] output = new String[columns.size()];
          makeColumns(run, partition, aliquot, user.getLoginName(), output);
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
            .flatMap(partition -> createRowsForPartition(run, user, columns, partition)))
        .collect(Collectors.joining("\n"));
  }

  protected abstract Stream<String> getColumns();

  protected abstract String header(Run run);

  protected abstract void makeColumns(Run run, Partition partition, PoolableElementView aliquot, String userName, String[] output);
}
