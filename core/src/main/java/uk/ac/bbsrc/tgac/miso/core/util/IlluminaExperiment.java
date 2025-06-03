package uk.ac.bbsrc.tgac.miso.core.util;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import uk.ac.bbsrc.tgac.miso.core.data.IndexedLibrary;
import uk.ac.bbsrc.tgac.miso.core.data.InstrumentDataManglingPolicy;
import uk.ac.bbsrc.tgac.miso.core.data.LibraryIndex;
import uk.ac.bbsrc.tgac.miso.core.data.Pair;
import uk.ac.bbsrc.tgac.miso.core.data.Pool;
import uk.ac.bbsrc.tgac.miso.core.data.SequencingParameters;
import uk.ac.bbsrc.tgac.miso.core.data.impl.view.ParentLibrary;
import uk.ac.bbsrc.tgac.miso.core.data.impl.view.PoolElement;

public enum IlluminaExperiment {

  CLONE_CHECKING("Clone Checking", false) {

    @Override
    protected void applyHeader(
        Map<String, String> header, String experimentName, String instrument,
        String indexAdapters,
        String chemistry, String novaSeqXSeriesMapping) {
      applyIlluminaHeader(header, experimentName, instrument, indexAdapters, chemistry);
      header.put("Workflow", "GenerateFASTQ");
      header.put("Application", "Clone Checking");
      header.put("Assay", "Nextera XT");
    }

    @Override
    protected void applySettings(Map<String, String> settings, String read1Primer, String indexPrimer,
        String read2Primer, String overrideCycles,
        String dragenVersion, String trimUMI,
        String fastqCompression) {
      settings.put("\n[Settings]", "");
      settings.put("Adapter", "CTGTCTCTTATACACATCT");
      applyIlluminaSettings(settings, read1Primer, indexPrimer, read2Primer);
    }

    @Override
    protected void applyData(Map<String, String> data, List<List<String>> dataColumns, List<String> headers,
        List<String> sampleIdsCol,
        List<String> laneCol, List<String> samplePlateCol, List<String> sampleWellCol,
        List<String> index1IdCol, List<String> index1Col, List<String> index2IdCol, List<String> index2Col,
        List<String> projectCol,
        List<String> genomeFolderCol, List<String> descriptionCol, List<String> libraryCol,
        List<String> libraryPrepKitCol) {
      data.put("\n[Data]", "");
      applyIlluminaData(dataColumns, headers, sampleIdsCol, laneCol, samplePlateCol, sampleWellCol, index1IdCol,
          index1Col, index2IdCol, index2Col, projectCol, genomeFolderCol, descriptionCol);
    }
  },
  LIBRARY_QC("Library QC", false) {

    @Override
    protected void applyHeader(Map<String, String> header, String experimentName, String instrument,
        String indexAdapters, String chemistry, String novaSeqXSeriesMapping) {
      applyIlluminaHeader(header, experimentName, instrument, indexAdapters, chemistry);
      header.put("Workflow", "LibraryQC");
      header.put("Application", "Library QC");
      header.put("Assay", "Nextera DNA");

    }

    @Override
    protected void applySettings(Map<String, String> settings, String read1Primer, String indexPrimer,
        String read2Primer, String overrideCycles,
        String dragenVersion, String trimUMI,
        String fastqCompression) {
      settings.put("\n[Settings]", "");
      settings.put("FlagPCRDuplicates", "1");
      settings.put("ReverseComplement", "0");
      settings.put("RunBwaAln", "0");
      settings.put("Adapter", "CTGTCTCTTATACACATCT");
      applyIlluminaSettings(settings, read1Primer, indexPrimer, read2Primer);
    }

    @Override
    protected void applyData(Map<String, String> data, List<List<String>> dataColumns, List<String> headers,
        List<String> sampleIdsCol,
        List<String> laneCol, List<String> samplePlateCol, List<String> sampleWellCol,
        List<String> index1IdCol, List<String> index1Col, List<String> index2IdCol, List<String> index2Col,
        List<String> projectCol,
        List<String> genomeFolderCol, List<String> descriptionCol, List<String> libraryCol,
        List<String> libraryPrepKitCol) {
      data.put("\n[Data]", "");
      applyIlluminaData(dataColumns, headers, sampleIdsCol, laneCol, samplePlateCol, sampleWellCol, index1IdCol,
          index1Col, index2IdCol, index2Col, projectCol, genomeFolderCol, descriptionCol);
    }
  },
  METAGENOMICS_16S("Metagenomics 16S rRNA", false) {

    @Override
    protected void applyHeader(
        Map<String, String> header, String experimentName, String instrument,
        String indexAdapters,
        String chemistry, String novaSeqXSeriesMapping) {
      applyIlluminaHeader(header, experimentName, instrument, indexAdapters, chemistry);
      header.put("Workflow", "Metagenomics");
      header.put("Application", "Metagenomics 16S rRNA");
      header.put("Assay", "TruSeq DNA PCR-Free");
    }

    @Override
    protected void applySettings(Map<String, String> settings, String read1Primer, String indexPrimer,
        String read2Primer, String overrideCycles,
        String dragenVersion, String trimUMI,
        String fastqCompression) {
      settings.put("\n[Settings]", "");
      settings.put("Adapter", "AGATCGGAAGAGCACACGTCTGAACTCCAGTCA");
      settings.put("AdapterRead2", "AGATCGGAAGAGCGTCGTGTAGGGAAAGAGTGT");
      applyIlluminaSettings(settings, read1Primer, indexPrimer, read2Primer);
    }

    @Override
    protected void applyData(Map<String, String> data, List<List<String>> dataColumns, List<String> headers,
        List<String> sampleIdsCol,
        List<String> laneCol, List<String> samplePlateCol, List<String> sampleWellCol,
        List<String> index1IdCol, List<String> index1Col, List<String> index2IdCol, List<String> index2Col,
        List<String> projectCol,
        List<String> genomeFolderCol, List<String> descriptionCol, List<String> libraryCol,
        List<String> libraryPrepKitCol) {
      data.put("\n[Data]", "");
      applyIlluminaData(dataColumns, headers, sampleIdsCol, laneCol, samplePlateCol, sampleWellCol, index1IdCol,
          index1Col, index2IdCol, index2Col, projectCol, genomeFolderCol, descriptionCol);
    }

    @Override
    protected void applyCloud(Map<String, String> data, List<List<String>> dataColumns, List<String> headers,
        List<String> sampleIdsCol, List<String> laneCol, List<String> index1Col,
        List<String> projectCol, List<String> libraryCol, List<String> libraryPrepKitCol) {}
  },
  FASTQ_ONLY_NEXTERA_XT("FASTQ Only (Nextera XT)", false) {

    @Override
    protected void applyHeader(
        Map<String, String> header, String experimentName, String instrument,
        String indexAdapters,
        String chemistry, String novaSeqXSeriesMapping) {
      applyIlluminaHeader(header, experimentName, instrument, indexAdapters, chemistry);
      header.put("Workflow", "GenerateFASTQ");
      header.put("Application", "FASTQ Only");
      header.put("Assay", "Nextera XT");
    }

    @Override
    protected void applySettings(Map<String, String> settings, String read1Primer, String indexPrimer,
        String read2Primer, String overrideCycles,
        String dragenVersion, String trimUMI,
        String fastqCompression) {
      settings.put("\n[Settings]", "");
      settings.put("ReverseComplement", "0");
      settings.put("Adapter", "CTGTCTCTTATACACATCT");
      applyIlluminaSettings(settings, read1Primer, indexPrimer, read2Primer);
    }

    @Override
    protected void applyData(Map<String, String> data, List<List<String>> dataColumns, List<String> headers,
        List<String> sampleIdsCol,
        List<String> laneCol, List<String> samplePlateCol, List<String> sampleWellCol,
        List<String> index1IdCol, List<String> index1Col, List<String> index2IdCol, List<String> index2Col,
        List<String> projectCol,
        List<String> genomeFolderCol, List<String> descriptionCol, List<String> libraryCol,
        List<String> libraryPrepKitCol) {
      data.put("\n[Data]", "");
      applyIlluminaData(dataColumns, headers, sampleIdsCol, laneCol, samplePlateCol, sampleWellCol, index1IdCol,
          index1Col, index2IdCol, index2Col, projectCol, genomeFolderCol, descriptionCol);
    }
  },
  FASTQ_ONLY_TRUSEQ_NANO_DNA("FASTQ Only (TruSeq Nano DNA)", false) {

    @Override
    protected void applyHeader(
        Map<String, String> header, String experimentName, String instrument,
        String indexAdapters,
        String chemistry, String novaSeqXSeriesMapping) {
      applyIlluminaHeader(header, experimentName, instrument, indexAdapters, chemistry);
      header.put("Workflow", "GenerateFASTQ");
      header.put("Application", "FASTQ Only");
      header.put("Assay", "TruSeq Nano DNA");
    }

    @Override
    protected void applySettings(Map<String, String> settings, String read1Primer, String indexPrimer,
        String read2Primer, String overrideCycles,
        String dragenVersion, String trimUMI,
        String fastqCompression) {
      settings.put("\n[Settings]", "");
      settings.put("ReverseComplement", "0");
      settings.put("Adapter", "AGATCGGAAGAGCACACGTCTGAACTCCAGTCA");
      settings.put("AdapterRead2", "AGATCGGAAGAGCGTCGTGTAGGGAAAGAGTGT");
      applyIlluminaSettings(settings, read1Primer, indexPrimer, read2Primer);
    }

    @Override
    protected void applyData(Map<String, String> data, List<List<String>> dataColumns, List<String> headers,
        List<String> sampleIdsCol,
        List<String> laneCol, List<String> samplePlateCol, List<String> sampleWellCol,
        List<String> index1IdCol, List<String> index1Col, List<String> index2IdCol, List<String> index2Col,
        List<String> projectCol, List<String> genomeFolderCol, List<String> descriptionCol, List<String> libraryCol,
        List<String> libraryPrepKitCol) {
      data.put("\n[Data]", "");
      applyIlluminaData(dataColumns, headers, sampleIdsCol, laneCol, samplePlateCol, sampleWellCol, index1IdCol,
          index1Col, index2IdCol, index2Col, projectCol, genomeFolderCol, descriptionCol);
    }

    @Override
    protected void applyCloud(Map<String, String> data, List<List<String>> dataColumns, List<String> headers,
        List<String> sampleIdsCol, List<String> laneCol, List<String> index1Col,
        List<String> projectCol, List<String> libraryCol, List<String> libraryPrepKitCol) {}
  },
  BCLCONVERT("BCL Convert", true) {
    protected void applyHeader(Map<String, String> header, String experimentName, String instrument,
        String indexAdapters, String chemistry, String novaSeqXSeriesMapping) {
      applyDragenHeader(header, experimentName, instrument, novaSeqXSeriesMapping);
    }

    protected void applySettings(Map<String, String> settings, String read1Primer, String indexPrimer,
        String read2Primer, String overrideCycles, String dragenVersion, String trimUMI, String fastqCompression) {
      settings.put("\n[BCLConvert_Settings]", "");
      settings.put("FastqCompressionFormat", fastqCompression);
      settings.put("TrimUMI", trimUMI);
      applyDragenSettings(settings, dragenVersion, overrideCycles);
    }

    protected void applyData(Map<String, String> data, List<List<String>> dataColumns, List<String> headers,
        List<String> sampleIdsCol,
        List<String> laneCol, List<String> samplePlateCol, List<String> sampleWellCol,
        List<String> index1IdCol, List<String> index1Col, List<String> index2IdCol, List<String> index2Col,
        List<String> projectCol,
        List<String> genomeFolderCol, List<String> descriptionCol, List<String> libraryCol,
        List<String> libraryPrepKitCol) {
      data.put("\n[BCLConvert_Data]", "");
      applyDragenData(dataColumns, headers, sampleIdsCol, laneCol, index1Col, index2Col);
    }

    protected void applyReads(Map<String, String> reads, Integer Read1Cycles, Integer Read2Cycles,
        Integer Index1Cycles,
        Integer Index2Cycles) {
      applyIlluminaReadsIndexes(reads, Read1Cycles, Read2Cycles, Index1Cycles, Index2Cycles);
    }

    protected void applyCloud(Map<String, String> data, List<List<String>> dataColumns, List<String> headers,
        List<String> sampleIdsCol, List<String> laneCol, List<String> index1Col,
        List<String> projectCol, List<String> libraryCol, List<String> libraryPrepKitCol) {
      data.put("\n[Cloud_Settings]", "");

      Collections.addAll(headers, "Lane", "Sample_ID", "ProjectName", "LibraryName", "LibraryPrepKitName",
          "IndexAdapterKitName");
      Collections.addAll(dataColumns, laneCol, sampleIdsCol, projectCol, libraryCol, libraryPrepKitCol, index1Col);
    }
  };

  private static final DateTimeFormatter MDY = DateTimeFormatter.ofPattern("M/d/yyyy");

  private final String description;
  private final boolean dragen;

  private IlluminaExperiment(String description, boolean dragen) {
    this.description = description;
    this.dragen = dragen;
  }

  protected abstract void applyHeader(Map<String, String> header, String experimentName, String instrument,
      String indexAdapters, String chemistry, String novaSeqXSeriesMapping);

  protected abstract void applySettings(Map<String, String> settings, String read1Primer, String indexPrimer,
      String read2Primer, String overrideCycles, String dragenVersion, String trimUMI, String fastqCompression);

  protected abstract void applyData(Map<String, String> data, List<List<String>> dataColumns, List<String> headers,
      List<String> sampleIdsCol,
      List<String> laneCol, List<String> samplePlateCol, List<String> sampleWellCol,
      List<String> index1IdCol, List<String> index1Col, List<String> index2IdCol, List<String> index2Col,
      List<String> projectCol,
      List<String> genomeFolderCol, List<String> descriptionCol, List<String> libraryCol,
      List<String> libraryPrepKitCol);

  protected void applyReads(Map<String, String> reads, Integer Read1Cycles, Integer Read2Cycles,
      Integer Index1Cycles, Integer Index2Cycles) {
    reads.put(String.valueOf(Read1Cycles), "");
    if (Read2Cycles != 0) {
      reads.put(String.valueOf(Read2Cycles), "");
    }
  }

  protected void applyCloud(Map<String, String> data, List<List<String>> dataColumns, List<String> headers,
      List<String> sampleIdsCol, List<String> laneCol, List<String> index1Col,
      List<String> projectCol, List<String> libraryCol, List<String> libraryPrepKitCol) {};

  protected void applyIlluminaHeader(Map<String, String> header, String experimentName, String instrument,
      String indexAdapters, String chemistry) {
    header.put("IEMFileVersion", "5");
    header.put("Experiment Name", experimentName);
    header.put("Date", ZonedDateTime.now().format(MDY));
    header.put("Instrument Type", instrument.replace("Illumina ", ""));
    header.put("Index Adapters", indexAdapters);
    header.put("Chemistry", chemistry);
  }

  protected void applyDragenHeader(Map<String, String> header, String experimentName, String instrument,
      String novaSeqXSeriesMapping) {
    header.put("FileFormatVersion", "2");
    header.put("RunName", experimentName + ZonedDateTime.now().format(MDY));
    header.put("InstrumentPlatform", instrument.replace(novaSeqXSeriesMapping,
        "NovaSeqXSeries"));
    header.put("IndexOrientation", "Forward");
  }

  public static void applyIlluminaSettings(Map<String, String> settings, String read1Primer, String indexPrimer,
      String read2Primer) {
    if (!LimsUtils.isStringBlankOrNull(read1Primer)) {
      settings.put("CustomRead1PrimerMix", read1Primer);
    }
    if (!LimsUtils.isStringBlankOrNull(indexPrimer)) {
      settings.put("CustomIndexPrimerMix", indexPrimer);
    }
    if (!LimsUtils.isStringBlankOrNull(read2Primer)) {
      settings.put("CustomRead2PrimerMix", read2Primer);
    }
  }

  public static void applyDragenSettings(Map<String, String> settings, String dragenVersion,
      String overrideCycles) {
    settings.put("SoftwareVersion", dragenVersion);
    settings.put("OverrideCycles", overrideCycles);
  }

  public static void applyIlluminaReadsIndexes(Map<String, String> reads, Integer read1, Integer read2, Integer index1,
      Integer index2) {
    reads.put("Read1Cycles", String.valueOf(read1));
    if (read2 != 0) {
      reads.put("Read2Cycles", String.valueOf(read2));
    }
    reads.put("Index1Cycles", String.valueOf(index1));
    if (index2 != 0) {
      reads.put("Index2Cycles", String.valueOf(index2));
    }

  }

  protected void applyIlluminaData(List<List<String>> data, List<String> headers, List<String> sampleIdsCol,
      List<String> laneCol, List<String> samplePlateCol, List<String> sampleWellCol,
      List<String> index1IdCol, List<String> index1Col, List<String> index2IdCol, List<String> index2Col,
      List<String> projectCol,
      List<String> genomeFolderCol, List<String> descriptionCol) {

    headers.add("Sample_ID");
    data.add(sampleIdsCol);
    if (laneCol.size() > 0) {
      headers.add("Lane");
      data.add(laneCol);
    }
    Collections.addAll(headers, "Sample_Plate", "Sample_Well", "I7_Index_ID", "index");
    Collections.addAll(data, samplePlateCol, sampleWellCol, index1IdCol, index1Col);
    if (index2IdCol.size() > 0 && index2Col.size() > 0) {
      Collections.addAll(headers, "I5_Index_ID", "index2");
      Collections.addAll(data, index2IdCol, index2Col);
    }
    Collections.addAll(headers, "GenomeFolder", "Sample_Project", "Description");
    Collections.addAll(data, genomeFolderCol, projectCol, descriptionCol);
  }

  protected void applyDragenData(List<List<String>> data, List<String> headers, List<String> sampleIdsCol,
      List<String> laneCol, List<String> index1Col, List<String> index2Col) {

    // DRAGEN expects na instead of empty entries
    Collections.replaceAll(laneCol, "", "na");
    Collections.replaceAll(sampleIdsCol, "", "na");
    Collections.replaceAll(index1Col, "", "na");

    Collections.addAll(headers, "Lane", "Sample_ID", "Index");
    Collections.addAll(data, laneCol, sampleIdsCol, index1Col);
    if (index2Col.size() > 0) {
      Collections.addAll(headers, "Index2");
      Collections.replaceAll(index2Col, "", "na");
      Collections.addAll(data, index2Col);
    }
  }

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

  public boolean isDragen() {
    return dragen;
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

  public final String makeSampleSheet(String genomeFolder, SequencingParameters parameters,
      String read1Primer,
      String indexPrimer,
      String read2Primer, List<Pool> pools, List<Integer> lanes,
      String dragenVersion,
      String trimUMI,
      String fastqCompressionFormat,
      String novaSeqXSeriesMapping) {
    final Map<String, String> header = new LinkedHashMap<>();
    final Map<String, String> settings = new LinkedHashMap<>();
    final Map<String, String> reads = new LinkedHashMap<>();
    final Map<String, String> data = new LinkedHashMap<>();
    final List<List<String>> dataColumns = new ArrayList<>();
    final Map<String, String> cloudData = new LinkedHashMap<>();
    final List<List<String>> cloudDataColumns = new ArrayList<>();
    final StringBuilder output = new StringBuilder();

    // Header Section
    String chemistry;
    if (pools.stream()//
        .flatMap(pool -> pool.getPoolContents().stream())
        .anyMatch(element -> element.getAliquot().getParentLibrary().getIndex2() != null)) {
      chemistry = "Amplicon";
    } else {
      chemistry = "Default";
    }

    header.put("Index Adapters", pools.stream()//
        .flatMap(pool -> pool.getPoolContents().stream())//
        .map(element -> element.getAliquot().getParentLibrary().getIndex1())//
        .filter(Objects::nonNull)
        .map(i -> i.getFamily().getName())//
        .distinct()//
        .sorted()//
        .collect(Collectors.joining("/")));

    applyHeader(header, pools.stream().map(Pool::getAlias).collect(Collectors.joining("/")),
        parameters.getInstrumentModel().getAlias(),
        pools.stream().flatMap(pool -> pool.getPoolContents().stream())
            .map(element -> element.getAliquot().getParentLibrary().getIndex1()).filter(Objects::nonNull)
            .map(i -> i.getFamily().getName()).distinct().sorted().collect(Collectors.joining("/")),
        chemistry, novaSeqXSeriesMapping);

    final int i7Length = getMaxLength(pools, 1);
    final int i5Length = getMaxLength(pools, 2);

    output.append("[Header]\n");
    writeMap(header, output);

    // Reads Section
    output.append("\n[Reads]\n");
    final int read1Length = parameters.getReadLength();
    final int read2Length = parameters.getReadLength2();
    applyReads(reads, read1Length, read2Length, i7Length, i5Length);
    writeMap(reads, output);

    // Settings Section
    applySettings(settings, read1Primer, indexPrimer, read2Length == 0 ? "" : read2Primer,
        "Y" + read1Length + ";I" + i7Length + ";I" + i5Length + ";Y" + read2Length, dragenVersion, trimUMI,
        fastqCompressionFormat);
    writeMap(settings, output);

    // Data Section
    // Store all info as separate lists b/c the order (but not content) of columns varies by samplesheet
    List<String> dataHeaders = new ArrayList<>();
    List<String> cloudHeaders = new ArrayList<>();
    List<String> sampleIdsCol = new ArrayList<>();
    List<String> laneCol = new ArrayList<>();
    List<String> samplePlateCol = new ArrayList<>();
    List<String> sampleWellCol = new ArrayList<>();
    List<String> index1IdCol = new ArrayList<>();
    List<String> index1Col = new ArrayList<>();
    List<String> index2IdCol = new ArrayList<>();
    List<String> index2Col = new ArrayList<>();
    List<String> projectCol = new ArrayList<>();
    List<String> genomeFolderCol = new ArrayList<>();
    List<String> descriptionCol = new ArrayList<>();
    List<String> libraryCol = new ArrayList<>();
    List<String> libraryPrepKitCol = new ArrayList<>();

    // Iterate over pools and their contents to generate sample sheet data
    for (int lane = 0; lane < pools.size(); lane++) {
      for (final PoolElement element : pools.get(lane).getPoolContents()) {
        ParentLibrary library = element.getAliquot().getParentLibrary();

        // Collect non-null indices from the parent library
        final List<LibraryIndex> indices = Stream.of(library.getIndex1(), library.getIndex2())
            .filter(Objects::nonNull)
            .collect(Collectors.toList());
        final List<Pair<Pair<String, String>, Pair<String, String>>> outputIndicies;

        // If no indices are found, create a default "No Index" entry
        // Otherwise, build the indices using the extracted values
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

        // Iterate over the generated output indices and add values to the data and cloud data sections
        for (final Pair<Pair<String, String>, Pair<String, String>> paddedIndices : outputIndicies) {

          libraryCol.add(library.getName());
          libraryPrepKitCol.add("");
          samplePlateCol.add("");
          sampleWellCol.add("");

          String sampleId = element.getAliquot().getAlias();
          if (outputIndicies.size() > 1) {
            sampleId = sampleId + "_" + suffix;
          }
          sampleIdsCol.add(sampleId);

          if (pools.size() > 1) {
            laneCol.add(String.valueOf(lanes.get(lane) + 1));
          }

          index1IdCol.add(paddedIndices.getKey().getKey()); // IndexAdapterKitName
          index1Col.add(paddedIndices.getKey().getValue());

          if (i5Length > 0) {
            index2IdCol.add(paddedIndices.getValue().getKey());
            index2Col.add(parameters.getInstrumentModel().getDataManglingPolicy() == InstrumentDataManglingPolicy.I5_RC
                ? SampleSheet.reverseComplement(paddedIndices.getValue().getValue())
                : paddedIndices.getValue().getValue());
          }

          genomeFolderCol.add(genomeFolder);
          projectCol.add(element.getAliquot().getProjectCode());
          descriptionCol
              .add(element.getAliquot().getAliquotBarcode() != null ? element.getAliquot().getAliquotBarcode() : "");

        }
      }
    }
    applyData(data, dataColumns, dataHeaders, sampleIdsCol, laneCol, samplePlateCol, sampleWellCol, index1IdCol,
        index1Col,
        index2IdCol, index2Col, projectCol, genomeFolderCol, descriptionCol, libraryCol, libraryPrepKitCol);
    applyCloud(cloudData, cloudDataColumns, cloudHeaders, sampleIdsCol, laneCol, index1Col,
        projectCol, libraryCol, libraryPrepKitCol);


    writeMap(data, output);
    writeRows(dataHeaders, output);
    writeListByColumns(dataColumns, output);
    writeMap(cloudData, output);
    writeRows(cloudHeaders, output);
    writeListByColumns(cloudDataColumns, output);


    return output.toString();

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

  private void writeRows(final List<String> row, final StringBuilder output) {
    if (!row.isEmpty()) {
      for (String entry : row) {
        if (entry.contains(",")) {
          output.append("\"").append(entry).append("\"").append(",");
        } else {
          output.append(entry).append(",");
        }
      }
      output.append("\n");
    }
  }

  // Take in a list of columns and append to output
  private void writeListByColumns(final List<List<String>> columns, final StringBuilder output) {
    if (!columns.isEmpty()) {
      for (int itemIndex = 0; itemIndex < columns.get(0).size(); itemIndex++) {
        for (int colIndex = 0; colIndex < columns.size(); colIndex++) {

          String entry = columns.get(colIndex).get(itemIndex);
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

}
