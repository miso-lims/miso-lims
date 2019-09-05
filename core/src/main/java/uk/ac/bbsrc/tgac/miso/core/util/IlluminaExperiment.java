package uk.ac.bbsrc.tgac.miso.core.util;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import uk.ac.bbsrc.tgac.miso.core.data.Index;
import uk.ac.bbsrc.tgac.miso.core.data.InstrumentDataManglingPolicy;
import uk.ac.bbsrc.tgac.miso.core.data.Pair;
import uk.ac.bbsrc.tgac.miso.core.data.Pool;
import uk.ac.bbsrc.tgac.miso.core.data.SequencingParameters;
import uk.ac.bbsrc.tgac.miso.core.data.impl.view.PoolElement;

public enum IlluminaExperiment {
  CLONE_CHECKING("Clone Checking") {

    @Override
    protected void applyAttribute(Map<String, String> header, Map<String, String> settings) {
      header.put("Workflow", "GenerateFASTQ");
      header.put("Application", "Clone Checking");
      header.put("Assay", "Nextera XT");
      header.put("Chemistry", "Amplicon");
      settings.put("Adapter", "CTGTCTCTTATACACATCT");

    }

  },
  LIBRARY_QC("Library QC") {

    @Override
    protected void applyAttribute(Map<String, String> header, Map<String, String> settings) {
      header.put("Workflow", "LibraryQC");
      header.put("Application", "Library QC");
      header.put("Assay", "Nextera DNA");
      header.put("Chemistry", "Default");

      settings.put("FlagPCRDuplicates", "1");
      settings.put("ReverseComplement", "0");
      settings.put("RunBwaAln", "0");
      settings.put("Adapter", "CTGTCTCTTATACACATCT");

    }

  },
  METAGENOMICS_16S("Metagenomics 16S rRNA") {

    @Override
    protected void applyAttribute(Map<String, String> header, Map<String, String> settings) {
      header.put("Workflow", "Metagenomics");
      header.put("Application", "Metagenomics 16S rRNA");
      header.put("Assay", "TruSeq DNA PCR-Free");
      header.put("Chemistry", "Default");

      settings.put("Adapter", "AGATCGGAAGAGCACACGTCTGAACTCCAGTCA");
      settings.put("AdapterRead2", "AGATCGGAAGAGCGTCGTGTAGGGAAAGAGTGT");

    }
  };
  private static final DateTimeFormatter MDY = DateTimeFormatter.ofPattern("M/d/yyyy");

  private final String description;

  private IlluminaExperiment(String description) {
    this.description = description;

  }

  protected abstract void applyAttribute(Map<String, String> header, Map<String, String> settings);

  private Pair<String, String> buildIndex(Optional<Index> index, int length) {
    return new Pair<>(index.map(Index::getName).orElse("No Index"), pad(length, index.map(Index::getSequence).orElse("")));
  }

  private Optional<Index> extract(List<Index> indices, int position) {
    return indices.stream()//
        .filter(i -> i.getPosition() == position)//
        .findFirst();
  }

  private List<String> extractCollection(List<Index> indices, int position) {
    return indices.stream()//
        .filter(i -> i.getPosition() == position)//
        .map(i -> i.getRealSequences())//
        .findFirst().orElse(Collections.singletonList(""));
  }

  public String getDescription() {
    return description;
  }

  private int getMaxLength(List<Pool> pools, int position) {
    return pools.stream()//
        .flatMap(pool -> pool.getPoolContents().stream())//
        .flatMap(element -> element.getPoolableElementView().getIndices().stream())//
        .filter(i -> i.getPosition() == position)//
        .flatMap(i -> i.getFamily().hasFakeSequence() ? i.getRealSequences().stream() : Stream.of(i.getSequence()))//
        .mapToInt(String::length)//
        .max().orElse(0);
  }

  public final String makeSampleSheet(String genomeFolder, SequencingParameters parameters, List<Pool> pools) {
    final Map<String, String> header = new HashMap<>();
    final Map<String, String> settings = new HashMap<>();
    applyAttribute(header, settings);
    header.put("IEMFileVersion", "5");
    header.put("Experiment Name", pools.stream().map(Pool::getAlias).collect(Collectors.joining("/")));
    header.put("Date", ZonedDateTime.now().format(MDY));
    header.put("Instrument Type", parameters.getInstrumentModel().getAlias().replace("Illumina ", ""));
    header.put("Index Adapters", pools.stream()//
        .flatMap(pool -> pool.getPoolContents().stream())//
        .flatMap(element -> element.getPoolableElementView().getIndices().stream())//
        .map(i -> i.getFamily().getName())//
        .distinct()//
        .sorted()//
        .collect(Collectors.joining("/")));

    final StringBuilder output = new StringBuilder();
    output.append("[Header]\n");
    writeMap(header, output);
    output.append("\n[Reads]\n").append(parameters.getReadLength()).append("\n");
    if (parameters.isPaired()) {
      output.append(parameters.getReadLength()).append("\n");
    }

    output.append("\n[Settings]\n");
    writeMap(settings, output);

    final int i7Length = getMaxLength(pools, 1);
    final int i5Length = getMaxLength(pools, 2);
    output.append("\n[Data]\nSample_ID,");
    if (pools.size() > 1) {
      output.append("Lane,");
    }
    output.append("Sample_Plate,Sample_Well,I7_Index_ID,index,");
    if (i5Length > 0) {
      output.append("I5_Index_ID,index2,");

    }
    output.append("GenomeFolder,Sample_Project,Description\n");
    for (int lane = 0; lane < pools.size(); lane++) {
      for (final PoolElement element : pools.get(lane).getPoolContents()) {
        final List<Index> indices = element.getPoolableElementView().getIndices();
        final List<Pair<Pair<String, String>, Pair<String, String>>> outputIndicies;
        if (indices.isEmpty()) {
          outputIndicies = Collections.singletonList(
              new Pair<>(new Pair<>("No Index", String.join("", Collections.nCopies(i7Length, "N"))),
                  new Pair<>("No Index", String.join("", Collections.nCopies(i5Length, "N")))));
        } else if (indices.get(0).getFamily().hasFakeSequence()) {
          final List<String> i5s = extractCollection(indices, 2);
          outputIndicies = new ArrayList<>();
          for (final String i7 : extractCollection(indices, 1)) {
            for (final String i5 : i5s) {
              outputIndicies.add(new Pair<>(new Pair<>(indices.get(0).getName(), i7), //
                  new Pair<>(indices.size() > 1 ? indices.get(1).getName() : "No Index", i5)));
            }
          }
        } else {
          outputIndicies = Collections
              .singletonList(new Pair<>(buildIndex(extract(indices, 1), i7Length), buildIndex(extract(indices, 2), i5Length)));
        }
        int suffix = 0;
        for (final Pair<Pair<String, String>, Pair<String, String>> paddedIndices : outputIndicies) {
          output.append(element.getPoolableElementView().getAliquotAlias());
          if (outputIndicies.size() > 1) {
            output.append("_").append(++suffix);
          }
          if (pools.size() > 1) {
            output.append(",").append(lane + 1);
          }
          output.append(",,,");
          escape(output, paddedIndices.getKey().getKey());
          output
              .append(",")//
              .append(paddedIndices.getKey().getValue())//
              .append(",");
          if (i5Length > 0) {
            escape(output, paddedIndices.getValue().getKey());
            output
                .append(",")//
                .append(parameters.getInstrumentModel().getDataManglingPolicy() == InstrumentDataManglingPolicy.I5_RC
                    ? SampleSheet.reverseComplement(paddedIndices.getValue().getValue())
                    : paddedIndices.getValue().getValue());
          }
          output.append(",")//
              .append(genomeFolder)//
              .append(",")//
              .append(element.getPoolableElementView().getProjectShortName())//
              .append(",");
          if (element.getPoolableElementView().getAliquotBarcode() != null) {
            escape(output, element.getPoolableElementView().getAliquotBarcode());
          }
          output.append("\n");
        }
      }
    }

    return output.toString();
  }

  private void escape(StringBuilder output, String input) {
    if (input == null || input.isEmpty()) {
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
}
