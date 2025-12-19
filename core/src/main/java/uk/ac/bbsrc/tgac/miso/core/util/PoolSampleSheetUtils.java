package uk.ac.bbsrc.tgac.miso.core.util;

import uk.ac.bbsrc.tgac.miso.core.data.Pair;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public final class PoolSampleSheetUtils {
    private PoolSampleSheetUtils() { /* no instantiation */ }

    private static final DateTimeFormatter MDY = DateTimeFormatter.ofPattern("M/d/yyyy");
    private static final DateTimeFormatter MDY_HMS = DateTimeFormatter.ofPattern("yyyyddMMHHmmss");



    protected static void applyIlluminaHeader(Map<String, String> header, String experimentName, String instrument,
                                       String indexAdapters, String chemistry) {
        header.put("IEMFileVersion", "5");
        header.put("Experiment Name", experimentName);
        header.put("Date", ZonedDateTime.now().format(MDY));
        header.put("Instrument Type", instrument.replace("Illumina ", ""));
        header.put("Index Adapters", indexAdapters);
        header.put("Chemistry", chemistry);
    }

    protected static void applyDragenHeader(Map<String, String> header, String experimentName, String instrument,
                                     String novaSeqXSeriesMapping) {
        header.put("FileFormatVersion", "2");
        header.put("RunName", ZonedDateTime.now().format(MDY_HMS));
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

    public static void applyIlluminaReadsIndexes(List<Pair<String, String>> reads, Integer read1, Integer read2, Integer index1,
                                                 Integer index2) {
        reads.add(new Pair<>("Read1Cycles", String.valueOf(read1)));
        if (read2 != 0) {
            reads.add(new Pair<>("Read2Cycles", String.valueOf(read2)));
        }
        reads.add(new Pair<>("Index1Cycles", String.valueOf(index1)));
        if (index2 != 0) {
            reads.add(new Pair<>("Index2Cycles", String.valueOf(index2)));
        }

    }

    protected static void applyIlluminaData(List<List<String>> data, List<String> headers,
                                     List<PoolSampleSheetRecord> dataSections) {

        headers.add("Sample_ID");
        boolean includeLane = false;

        if (dataSections.get(0).lane > 0) {
            includeLane = true;
            headers.add("Lane");
        }
        Collections.addAll(headers, "Sample_Plate", "Sample_Well", "I7_Index_ID", "index");

        boolean includeIndex2 = false;

        if (dataSections.get(0).index2Id != null && dataSections.get(0).index2 != null) {
            includeIndex2 = true;
            Collections.addAll(headers, "I5_Index_ID", "index2");
        }
        Collections.addAll(headers, "GenomeFolder", "Sample_Project", "Description");

        for (PoolSampleSheetRecord dataSection : dataSections) {
            List<String> currRow = new ArrayList<>();
            currRow.add(dataSection.sampleId);
            if (includeLane) {
                currRow.add(String.valueOf(dataSection.lane));
            }
            Collections.addAll(currRow, dataSection.plate, dataSection.well,
                    dataSection.index1Id, dataSection.index1);
            if (includeIndex2) {
                Collections.addAll(currRow, dataSection.index2Id, dataSection.index2);
            }
            Collections.addAll(currRow, dataSection.genomeFolder, dataSection.project,
                    dataSection.description);

            data.add(currRow);
        }
    }

    protected static void applyDragenData(List<List<String>> data, List<String> headers, List<PoolSampleSheetRecord> dataSections) {
        Collections.addAll(headers, "Lane", "Sample_ID", "Index");

        boolean includeIndex2 = false;
        if (dataSections.get(0).index2Id != null && dataSections.get(0).index2 != null) {
            includeIndex2 = true;
            Collections.addAll(headers, "Index2");
        }

        for (PoolSampleSheetRecord dataSection : dataSections) {

            List<String> currRow = new ArrayList<>();
            Collections.addAll(currRow, String.valueOf(dataSection.lane), dataSection.sampleId, dataSection.index1);
            if (includeIndex2) {
                Collections.addAll(currRow, dataSection.index2);
            }
            data.add(currRow);
        }
    }
}
