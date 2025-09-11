package uk.ac.bbsrc.tgac.miso.core.util;

import java.util.List;
import java.util.Map;


public class Metagenomics16S extends AbstractPoolSampleSheet {

    public Metagenomics16S() {
        super("Metagenomics 16S rRNA", false, "METAGENOMICS_16S");
    }

    @Override
    public void applyHeader(Map<String, String> header,
                            String experimentName,
                            String instrument,
                            String indexAdapters,
                            String chemistry,
                            String novaSeqXSeriesMapping) {
        PoolSampleSheetUtils.applyIlluminaHeader(header,
                experimentName,
                instrument,
                indexAdapters,
                chemistry);
        header.put("Workflow", "Metagenomics");
        header.put("Application", "Metagenomics 16S rRNA");
        header.put("Assay", "TruSeq DNA PCR-Free");
    }

    @Override
    public void applySettings(Map<String, String> settings, String read1Primer, String indexPrimer,
                              String read2Primer, String overrideCycles,
                              String dragenVersion, String trimUMI,
                              String fastqCompression) {
        settings.put("\n[Settings]", "");
        settings.put("Adapter", "AGATCGGAAGAGCACACGTCTGAACTCCAGTCA");
        settings.put("AdapterRead2", "AGATCGGAAGAGCGTCGTGTAGGGAAAGAGTGT");
        PoolSampleSheetUtils.applyIlluminaSettings(settings, read1Primer, indexPrimer, read2Primer);   }

    @Override
    public void applyData(Map<String, String> data,
                          List<List<String>> dataRows,
                          List<String> headers,
                          List<PoolSampleSheetRecord> samples) {
        data.put("\n[Data]", "");
        PoolSampleSheetUtils.applyIlluminaData(dataRows, headers, samples);
    }

}