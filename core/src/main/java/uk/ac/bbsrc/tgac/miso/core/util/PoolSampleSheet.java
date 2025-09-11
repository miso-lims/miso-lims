package uk.ac.bbsrc.tgac.miso.core.util;

import uk.ac.bbsrc.tgac.miso.core.data.Pair;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface PoolSampleSheet {
    public enum SampleSheetType {
        CLONE_CHECKING,METAGENOMICS_16S,FASTQ_ONLY_TRUSEQ_NANO_DNA,FASTQ_ONLY_NEXTERA_XT,BCL_CONVERT
    }
    void applyHeader(Map<String, String> header, String experimentName, String instrument,
                     String indexAdapters, String chemistry, String novaSeqXSeriesMapping);

    void applySettings(Map<String, String> settings, String read1Primer, String indexPrimer,
                       String read2Primer, String overrideCycles, String dragenVersion, String trimUMI, String fastqCompression);

    void applyData(Map<String, String> data, List<List<String>> dataRows, List<String> headers,
                   List<PoolSampleSheetRecord> dataSection);

    default void applyCloud(Map<String, String> data, Set<List<String>> dataRows, List<String> headers,
                              List<PoolSampleSheetRecord> dataSection) {};

    default void applyReads(List<Pair<String, String>> reads, Integer Read1Cycles, Integer Read2Cycles,
                              Integer Index1Cycles, Integer Index2Cycles) {
        PoolSampleSheetUtils.applyIlluminaReadsIndexes(reads, Read1Cycles, Read2Cycles, Index1Cycles, Index2Cycles);
    }


    String getDescription();
    boolean isDragen();
    String getName();
}
