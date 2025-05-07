package uk.ac.bbsrc.tgac.miso.core.util;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
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

  @Value("${miso.pools.samplesheet.dragenVersion:4.3.13}")
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
    return indices.stream()
        .filter(i -> i.getPosition() == position)
        .findFirst();
  }

  private Set<String> extractCollection(List<LibraryIndex> indices, int position) {
    return indices.stream()
        .filter(i -> i.getPosition() == position)
        .map(i -> i.getRealSequences())
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
    final List<List<String>> cloudData = new ArrayList<>();

    // Header Section
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

    // Reads Section
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

    // Settings Section
    output.append("\n");
    applySettings(settings);
    if (dragenVersion != null) {
      settings.put("SoftwareVersion", dragenVersion); // required
    }
    settings.put("OverrideCycles",
        "Y" + parameters.getReadLength() + ";I" + i7Length + ";I" + i5Length + ";Y" +
            parameters.getReadLength2());
    writeMap(settings, output);

    cloudData.add(new ArrayList<>(
        Arrays.asList("Lane", "Sample_ID", "ProjectName", "LibraryName", "LibraryPrepKitName", "IndexAdapterKitName")));

    // Data Section
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

    // Iterate over pools and their contents to generate sample sheet data
    for (int lane = 0; lane < pools.size(); lane++) {

      for (final PoolElement element : pools.get(lane).getPoolContents()) {

        ParentLibrary library = element.getAliquot().getParentLibrary();

        // Collect non-null indices from the parent library
        final List<LibraryIndex> indices = Stream.of(library.getIndex1(), library.getIndex2())
            .filter(Objects::nonNull)
            .collect(Collectors.toList());
        final List<Pair<Pair<String, String>, Pair<String, String>>> outputIndices;

        // If no indices are found, create a default "No Index" entry
        // Otherwise, build the indices using the extracted values
        if (indices.isEmpty()) {
          outputIndices = Collections.singletonList(
              new Pair<>(new Pair<>("No Index", String.join("", Collections.nCopies(i7Length, "N"))),
                  new Pair<>("No Index", String.join("", Collections.nCopies(i5Length, "N")))));
        } else if (indices.get(0).getFamily().hasFakeSequence()) {
          final Set<String> i5s = extractCollection(indices, 2);
          outputIndices = new ArrayList<>();
          for (final String i7 : extractCollection(indices, 1)) {
            for (final String i5 : i5s) {
              outputIndices.add(new Pair<>(new Pair<>(indices.get(0).getName(), i7), //
                  new Pair<>(indices.size() > 1 ? indices.get(1).getName() : "No Index", i5)));
            }
          }
        } else {
          outputIndices = Collections
              .singletonList(
                  new Pair<>(buildIndex(extract(indices, 1), i7Length), buildIndex(extract(indices, 2), i5Length)));
        }
        int suffix = 0;

        // Iterate over the generated output indices and add values to the data and cloud data sections
        for (final Pair<Pair<String, String>, Pair<String, String>> paddedIndices : outputIndices) {
          List<String> tempCloudList = new ArrayList<>();

          // Get the actual lane if any were skipped.
          if (lanes.size() > 1) {
            output.append(lanes.get(lane)); // Lane
            tempCloudList.add(lanes.get(lane).toString()); // Lane
          }

          output.append(",").append(element.getAliquot().getAlias()); // Sample ID
          tempCloudList.add(element.getAliquot().getAlias()); // Sample ID
          if (outputIndices.size() > 1) {
            output.append("_").append(++suffix); // Sample ID suffix
          }
          tempCloudList.add(element.getAliquot().getProjectCode()); // Project Name
          tempCloudList.add(""); // LibraryName
          tempCloudList.add(""); // LibraryPrep Kit
          tempCloudList.add(paddedIndices.getKey().getKey()); // IndexAdapterKitName
          output
              .append(",")
              .append(paddedIndices.getKey().getValue()); // Index
          if (i5Length > 0) {
            output
                .append(",")
                .append(parameters.getInstrumentModel().getDataManglingPolicy() == InstrumentDataManglingPolicy.I5_RC
                    ? SampleSheet.reverseComplement(paddedIndices.getValue().getValue())
                    : paddedIndices.getValue().getValue()); // Index2
          }
          output.append("\n");
          cloudData.add(tempCloudList);
        }
      }
    }

    output.append("\n[Cloud_Settings]\n");
    writeList(cloudData, output);

    return output.toString();

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

  private void writeList(final List<List<String>> input, final StringBuilder output) {
    for (List<String> listInput : input) {
      for (String entry : listInput) {
        if (entry.contains(",")) {
          output.append("\"").append(entry).append("\"").append(",");
        } else {
          output.append(entry).append(",");
        }
      }
      output.append("\n");
    }
  }
}
