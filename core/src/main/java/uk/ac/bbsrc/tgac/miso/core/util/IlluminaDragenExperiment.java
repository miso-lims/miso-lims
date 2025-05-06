package uk.ac.bbsrc.tgac.miso.core.util;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Value;

import uk.ac.bbsrc.tgac.miso.core.data.IndexedLibrary;
import uk.ac.bbsrc.tgac.miso.core.data.InstrumentDataManglingPolicy;
import uk.ac.bbsrc.tgac.miso.core.data.LibraryIndex;
import uk.ac.bbsrc.tgac.miso.core.data.Pair;
import uk.ac.bbsrc.tgac.miso.core.data.Pool;
import uk.ac.bbsrc.tgac.miso.core.data.SequencingParameters;
import uk.ac.bbsrc.tgac.miso.core.data.impl.view.ParentLibrary;
import uk.ac.bbsrc.tgac.miso.core.data.impl.view.PoolElement;

public enum IlluminaDragenExperiment {

  BCLCONVERT("BCLConvert") {

    @Override
    protected void applySettings(Map<String, String> settings) {
      settings.put("[BCLConvert_Settings]", "");
      settings.put("FastqCompressionFormat", "gzip"); // required
      settings.put("TrimUMI", "false");
    }

    @Override
    protected void applyData(Map<String, String> data) {
      data.put("[BCLConvert_Data]", "");
    }
  },
  GERMLINE("Germline") {

    @Override
    protected void applySettings(Map<String, String> settings) {
      settings.put("[Germline_Settings]", "");
      settings.put("AppVersion", "1.2.0"); // required
      settings.put("KeepFastQ", "true"); // required
      settings.put("MapAlignOutFormat", "bam"); // required
    }

    @Override
    protected void applyData(Map<String, String> data) {}
  };

  @Value("${miso.samplesheet.dragenVersion:#{4.3.13}}")
  private String dragenVersion;

  private static final DateTimeFormatter MDY = DateTimeFormatter.ofPattern("M/d/yyyy");

  private final String description;

  private IlluminaDragenExperiment(String description) {
    this.description = description;

  }

  protected abstract void applySettings(Map<String, String> settings);

  protected abstract void applyData(Map<String, String> data);

  private Pair<String, String> buildIndex(Optional<LibraryIndex> index, int length) {
    return new Pair<>(index.map(LibraryIndex::getName).orElse("No Index"),
        pad(length, index.map(LibraryIndex::getSequence).orElse("")));
  }

  private Optional<LibraryIndex> extract(List<LibraryIndex> indices, int position) {
    return indices.stream()//
        .filter(i -> i.getPosition() == position)//
        .findFirst();
  }

  private Set<String> extractCollection(List<LibraryIndex> indices, int position) {
    return indices.stream()//
        .filter(i -> i.getPosition() == position)//
        .map(i -> i.getRealSequences())//
        .findFirst().orElse(Collections.singleton(""));
  }

  public String getDescription() {
    return description;
  }

  private int getMaxLength(List<Pool> pools, int position) {
    List<ParentLibrary> libraries = pools.stream()
        .flatMap(pool -> pool.getPoolContents().stream())
        .map(element -> element.getAliquot().getParentLibrary())
        .collect(Collectors.toList());
    if (position == 1) {
      return LimsUtils.getLongestIndex(libraries, IndexedLibrary::getIndex1);
    } else if (position == 2) {
      return LimsUtils.getLongestIndex(libraries, IndexedLibrary::getIndex2);
    } else {
      throw new IllegalArgumentException("Invalid index position: " + position);
    }
  }

  public final String makeSampleSheet(String genomeFolder, SequencingParameters parameters, String read1Primer,
      String indexPrimer,
      String read2Primer, List<Pool> pools, List<Integer> lanes) {

    final StringBuilder output = new StringBuilder();

    final Map<String, String> header = new LinkedHashMap<>();
    final Map<String, String> settings = new LinkedHashMap<>();
    final Map<String, String> data = new LinkedHashMap<>();

    // Header
    header.put("FileFormatVersion", "2");

    // TODO change / decide on runname convention
    // must only contain alphanumeric, period, dash, underscore, and whitespace characters
    header.put("RunName", (new Date()).toString().replaceAll("\\s+", ""));

    header.put("InstrumentPlatform", parameters.getInstrumentModel().getAlias().replace("Illumina NovaSeq X Plus",
        "NovaSeqXSeries"));
    header.put("IndexOrientation", "Forward");

    output.append("[Header]\n");
    writeMap(header, output);

    final int i7Length = getMaxLength(pools, 1);
    final int i5Length = getMaxLength(pools, 2);

    // Reads
    output.append("\n[Reads]\n");
    output.append("Read1Cycles,");
    output.append(parameters.getReadLength()).append("\n");
    if (parameters.getReadLength2() != 0) {
      output.append("Read2Cycles,");
      output.append(parameters.getReadLength2()).append("\n");
    }
    output.append("Index1Cycles," + i7Length).append("\n");
    if (i5Length > 0) {
      output.append("Index2Cycles," + i5Length).append("\n");
    }

    // Settings
    output.append("\n");
    applySettings(settings);
    // settings.put("SoftwareVersion,", dragenVersion); // required
    settings.put("OverrideCycles,",
        "Y" + parameters.getReadLength() + ";I" + i7Length + ";I" + i5Length + ";Y" +
            parameters.getReadLength2());
    writeMap(settings, output);

    // Data
    output.append("\n");
    applyData(data);
    writeMap(data, output);
    if (lanes.size() > 1) {
      output.append("Lane,");
    }
    output.append("Sample_ID,Index");
    if (i5Length > 0) {
      output.append(",Index2");
    }
    output.append("\n");
    for (int lane = 0; lane < pools.size(); lane++) {
      for (final PoolElement element : pools.get(lane).getPoolContents()) {
        ParentLibrary library = element.getAliquot().getParentLibrary();
        final List<LibraryIndex> indices = Stream.of(library.getIndex1(), library.getIndex2())
            .filter(Objects::nonNull)
            .collect(Collectors.toList());
        final List<Pair<Pair<String, String>, Pair<String, String>>> outputIndicies;
        if (indices.isEmpty()) {
          outputIndicies = Collections.singletonList(
              new Pair<>(new Pair<>("No Index", String.join("", Collections.nCopies(i7Length, "N"))),
                  new Pair<>("No Index", String.join("", Collections.nCopies(i5Length, "N")))));
        } else if (indices.get(0).getFamily().hasFakeSequence()) {
          final Set<String> i5s = extractCollection(indices, 2);
          outputIndicies = new ArrayList<>();
          for (final String i7 : extractCollection(indices, 1)) {
            for (final String i5 : i5s) {
              outputIndicies.add(new Pair<>(new Pair<>(indices.get(0).getName(), i7), //
                  new Pair<>(indices.size() > 1 ? indices.get(1).getName() : "No Index", i5)));
            }
          }
        } else {
          outputIndicies = Collections
              .singletonList(
                  new Pair<>(buildIndex(extract(indices, 1), i7Length), buildIndex(extract(indices, 2), i5Length)));
        }
        int suffix = 0;
        for (final Pair<Pair<String, String>, Pair<String, String>> paddedIndices : outputIndicies) {
          if (pools.size() > 1) {
            output.append(lanes.get(lane));
          }
          output.append(",").append(element.getAliquot().getAlias());
          if (outputIndicies.size() > 1) {
            output.append("_").append(++suffix);
          }
          output
              .append(",")//
              .append(paddedIndices.getKey().getValue());
          if (i5Length > 0) {
            output
                .append(",")//
                .append(parameters.getInstrumentModel().getDataManglingPolicy() == InstrumentDataManglingPolicy.I5_RC
                    ? SampleSheet.reverseComplement(paddedIndices.getValue().getValue())
                    : paddedIndices.getValue().getValue());
          }
          output.append("\n");
        }
      }
    }

    return output.toString();

    // TODO Cloud Settings

  }

  private void escape(StringBuilder output, String input) {
    if (input == null || input.isEmpty()) {
      output.append("na");
      return;
    }
    if (input.contains("\"") || input.contains(",")) {
      output.append("\"").append(input.replace("\"", "\"\"")).append("\"");
    } else {
      output.append(input);
    }
  }

  private String pad(int length, String sequence) {
    if (sequence.length() >= length) {
      return sequence;
    }
    final StringBuilder paddedSequence = new StringBuilder(sequence);
    while (paddedSequence.length() < length) {
      paddedSequence.append("N");
    }
    return paddedSequence.toString();
  }

  private void writeMap(final Map<String, String> input, final StringBuilder output) {
    for (Entry<String, String> entry : input.entrySet()) {
      output.append(entry.getKey()).append(",");
      if (entry.getValue().contains(",")) {
        output.append("\"").append(entry.getValue()).append("\"");
      } else {
        output.append(entry.getValue());
      }
      output.append("\n");
    }
  }

  private void writeList(final List<String> input, final StringBuilder output) {
    for (String entry : input) {
      if (entry.contains(",")) {
        output.append("\"").append(entry).append("\"");
      } else {
        output.append(entry);
      }
      output.append("\n");
    }
  }
}
