package uk.ac.bbsrc.tgac.miso.core.util;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.eaglegenomics.simlims.core.User;

import uk.ac.bbsrc.tgac.miso.core.data.IlluminaRun;
import uk.ac.bbsrc.tgac.miso.core.data.Index;
import uk.ac.bbsrc.tgac.miso.core.data.InstrumentDataManglingPolicy;
import uk.ac.bbsrc.tgac.miso.core.data.InstrumentModel;
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
        reads = String.format("[Reads]\n%d\n%d\n\n", r.getSequencingParameters().getReadLength(),
            r.getSequencingParameters().getReadLength2());
      }
      return String.format("[Header]\nDate,%s\n\n%s[Data]\n", DateTimeFormatter.ISO_DATE.format(ZonedDateTime.now()), reads);
    }

    @Override
    protected String header(InstrumentModel model) {
      return String.format("[Header]\nDate,%s\n\n[Reads]\nXREADLENGTHX\nXPAIREDX\n\n[Data]\n",
          DateTimeFormatter.ISO_DATE.format(ZonedDateTime.now()));
    }

    @Override
    protected Stream<String> makeColumns(boolean needsSuffix, InstrumentModel model, int partitionNumber, String partitionBarcode,
        PoolableElementView aliquot,
        List<String> index,
        String userName) {
      final Optional<Index> firstIndex = aliquot.getIndices().stream().filter(i -> i.getPosition() == 1).findFirst();
      final Optional<Index> secondIndex = aliquot.getIndices().stream().filter(i -> i.getPosition() == 2).findFirst();
      return Stream.of(
          aliquot.getLibraryAlias() + (needsSuffix ? ("_" + String.join("_", index)) : ""), //
          aliquot.getLibraryName(), //
          firstIndex.map(Index::getName).orElse(""), //
          index.size() > 0 ? index.get(0) : "", //
          secondIndex.map(Index::getName).orElse(""), //
          index.size() > 1 ? index.get(1) : "");
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
    protected String header(InstrumentModel model) {
      return "";
    }

    @Override
    protected Stream<String> makeColumns(boolean suffixNeeded, InstrumentModel model, int partitionNumber, String partitionBarcode,
        PoolableElementView aliquot,
        List<String> index,
        String userName) {
      return Stream.of(partitionBarcode, //
          Integer.toString(partitionNumber), //
          String.format("%s_%s%s", aliquot.getLibraryName(), //
              aliquot.getAliquotName(), suffixNeeded ? ("_" + String.join("_", index)) : ""), //

          aliquot.getSampleAlias().replaceAll("\\s", ""), //
          String.join("-", index), //
          aliquot.getLibraryDescription(), //
          "N", //
          "NA", //
          userName);
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
    protected String header(InstrumentModel model) {
      return "";
    }

    @Override
    protected Stream<String> makeColumns(boolean suffixNeeded, InstrumentModel model, int partitionNumber, String partitionBarcode,
        PoolableElementView aliquot,
        List<String> index,
        String userName) {
      return Stream.concat(
          SampleSheet.CASAVA_1_7.makeColumns(suffixNeeded, model, partitionNumber, partitionBarcode, aliquot, index, userName),
          Stream.of(aliquot.getProjectAlias().replaceAll("\\s", "")));
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
    protected String header(InstrumentModel model) {
      return "[Data]\n";
    }

    @Override
    protected Stream<String> makeColumns(boolean suffixNeeded, InstrumentModel model, int partitionNumber, String partitionBarcode,
        PoolableElementView aliquot, List<String> index,
        String userName) {
      return Stream.of(Integer.toString(partitionNumber), //
          aliquot.getAliquotName(), //
          aliquot.getLibraryAlias(), //
          String.join(",", index), //
          aliquot.getProjectShortName());
    }

    @Override
    protected Stream<String> flattenRows(InstrumentModel model, int partitionNumber, String partitionBarcode,
        PoolableElementView aliquot,
        String userName) {
      return Stream.of(makeColumns(false, model, partitionNumber, partitionBarcode, aliquot,
          aliquot.getIndices().stream().map(Index::getSequence).collect(Collectors.toList()), userName).collect(Collectors.joining(",")));
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
        .flatMap(aliquot -> flattenRows(run.getSequencer().getInstrumentModel(), partition.getPartitionNumber(),
            partition.getSequencerPartitionContainer().getIdentificationBarcode(), aliquot, user.getLoginName()));
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

  protected Stream<String> flattenRows(InstrumentModel model, int partitionNumber, String partitionBarcode,
      PoolableElementView aliquot,
      String userName) {
    Set<Integer> positions = aliquot.getIndices().stream().map(Index::getPosition).collect(Collectors.toCollection(TreeSet::new));
    if (positions.isEmpty()) {
      return Stream.of(makeColumns(false, model, partitionNumber, partitionBarcode, aliquot, Collections.emptyList(), userName)
          .collect(Collectors.joining(",")));
    }
    List<List<String>> indices = null;
    for (int position : positions) {
      List<String> suffixes = aliquot.getIndices().stream().filter(i -> i.getPosition() == position)
          .flatMap(i -> i.getFamily().hasFakeSequence() ? i.getRealSequences().stream() : Stream.of(i.getSequence()))
          .map(
              model.getDataManglingPolicy() == InstrumentDataManglingPolicy.I5_RC
                  && position == 2 ? SampleSheet::reverseComplement : Function.identity())
          .collect(Collectors.toList());
      if (indices == null) {
        indices = suffixes.stream().map(Collections::singletonList).collect(Collectors.toList());
      } else {
        indices = indices.stream()//
            .flatMap(prefix -> suffixes.stream()//
                .map(suffix -> Stream.concat(prefix.stream(), //
                    Stream.of(suffix))//
                    .collect(Collectors.toList())))//
            .collect(Collectors.toList());
      }
    }
    final boolean needsSuffix = indices.size() > 1;
    return indices.stream()
        .map(index -> makeColumns(needsSuffix, model, partitionNumber, partitionBarcode, aliquot, index, userName)
            .collect(Collectors.joining(",")));

  }

  protected abstract Stream<String> getColumns();

  protected abstract String header(Run run);

  protected abstract String header(InstrumentModel model);

  protected abstract Stream<String> makeColumns(boolean suffixNeeded, InstrumentModel model, int partitionNumber, String partitionBarcode,
      PoolableElementView aliquot, List<String> index,
      String userName);
}
