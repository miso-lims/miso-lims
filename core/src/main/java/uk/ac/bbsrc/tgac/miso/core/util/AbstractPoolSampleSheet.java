package uk.ac.bbsrc.tgac.miso.core.util;

import uk.ac.bbsrc.tgac.miso.core.data.*;
import uk.ac.bbsrc.tgac.miso.core.data.impl.view.ParentLibrary;
import uk.ac.bbsrc.tgac.miso.core.data.impl.view.PoolElement;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public abstract class AbstractPoolSampleSheet implements PoolSampleSheet {


    protected final String description;
    protected final boolean dragen;
    protected final String name;

    protected AbstractPoolSampleSheet(String description, boolean dragen, String name) {
        this.description = description;
        this.dragen = dragen;
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public boolean isDragen() {
        return dragen;
    }

    public String getName() {
        return name;
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
        final List<Pair<String, String>> reads = new ArrayList<>(); //Can contain duplicate lines key/value pairs)
        final Map<String, String> data = new LinkedHashMap<>();
        final List<List<String>> dataRows = new ArrayList<>();
        final Map<String, String> cloudData = new LinkedHashMap<>();
        final Set<List<String>> cloudDataRows = new HashSet<>(); // Doesn't contain lane info so needs to be deduplicated
        final List<PoolSampleSheetRecord> allSamples = new ArrayList<>();
        final StringBuilder output = new StringBuilder();

        String nullReplacement = dragen ? "na" : "";

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
        writeList(reads, output);


        // Get info for Data Section
        boolean anyUmis = false;
        List<String> dataHeaders = new ArrayList<>();
        List<String> cloudHeaders = new ArrayList<>();

        // Iterate over pools and their contents to generate sample sheet data
        for (int lane = 0; lane < pools.size(); lane++) {
            for (final PoolElement element : pools.get(lane).getPoolContents()) {
                ParentLibrary library = element.getAliquot().getParentLibrary();

                if (library.getUmis()) {
                    anyUmis = true;
                }

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

                    String sampleId = element.getAliquot().getAlias();
                    if (outputIndicies.size() > 1) {
                        sampleId = sampleId + "_" + suffix;
                    }

                    int currLane = 0;
                    if (pools.size() > 1 || dragen) {
                        currLane = lanes.get(lane);
                    }

                    String index2Id = null;
                    String index2 = null;
                    if (i5Length > 0) {
                        index2Id = paddedIndices.getValue().getKey();
                        index2 = parameters.getInstrumentModel().getDataManglingPolicy() == InstrumentDataManglingPolicy.I5_RC
                                ? SampleSheet.reverseComplement(paddedIndices.getValue().getValue())
                                : paddedIndices.getValue().getValue();
                    }


                    allSamples.add(new PoolSampleSheetRecord(
                            sampleId, // Sample
                            currLane, // Lane
                            null, // Plate
                            null, // Well
                            paddedIndices.getKey().getKey(), // Index1 ID
                            paddedIndices.getKey().getValue(), // Index 1
                            index2Id, // Index2 ID
                            index2, // Index2
                            element.getAliquot().getProjectCode(), // Project
                            genomeFolder, // Genome Folder
                            element.getAliquot().getAliquotBarcode() != null ? element.getAliquot().getAliquotBarcode()
                                    : null, // Description
                            element.getAliquot().getName(), // Library
                            null // Library Prep Kit
                    ));

                }
            }
        }

        if (!anyUmis) {
            trimUMI = null;
        }
        // Settings Section
        applySettings(settings, read1Primer, indexPrimer, read2Length == 0 ? "" : read2Primer,
                "Y" + read1Length + ";I" + i7Length + ";I" + i5Length + ";Y" + read2Length, dragenVersion, trimUMI,
                fastqCompressionFormat);
        writeMap(settings, output);

        // Data Section
        applyData(data, dataRows, dataHeaders, allSamples);
        applyCloud(cloudData, cloudDataRows, cloudHeaders, allSamples);

        writeMap(data, output);
        writeRow(dataHeaders, output, nullReplacement);
        writeRows(dataRows, output, nullReplacement);
        writeMap(cloudData, output);
        writeRow(cloudHeaders, output, nullReplacement);
        writeRows(new ArrayList<>(cloudDataRows), output, nullReplacement);

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
        for (Map.Entry<String, String> entry : input.entrySet()) {
            output.append(entry.getKey()).append(",");
            if (entry.getValue().contains(",")) {
                output.append("\"").append(entry.getValue()).append("\"");
            } else {
                output.append(entry.getValue());
            }
            output.append("\n");
        }
    }

    private void writeList(final List<Pair<String, String>> input, final StringBuilder output) {
        for (Pair<String, String> entry : input) {
            output.append(entry.getKey()).append(",");
            if (entry.getValue().contains(",")) {
                output.append("\"").append(entry.getValue()).append("\"");
            } else {
                output.append(entry.getValue());
            }
            output.append("\n");
        }
    }

    private void writeRows(final List<List<String>> rows, final StringBuilder output, final String nullReplacement) {
        for (List<String> row : rows) {
            writeRow(row, output, nullReplacement);
        }
    }

    // Take in a list of strings and append to output in a single row
    private void writeRow(final List<String> row, final StringBuilder output, final String nullReplacement) {
        if (!row.isEmpty()) {
            for (String entry : row) {
                if (entry == null) {
                    output.append(nullReplacement).append(",");
                } else if (entry.contains(",")) {
                    output.append("\"").append(entry).append("\"").append(",");
                } else {
                    output.append(entry).append(",");
                }
            }
            output.append("\n");
        }
    }
}
