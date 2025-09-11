package uk.ac.bbsrc.tgac.miso.core.util;

import java.util.List;
import java.util.Map;
import java.util.Set;


public class LibraryQc extends AbstractPoolSampleSheet {

    public LibraryQc() {
        super("Library QC", false, "LIBRARY_QC");
    }

    @Override
    public void applyHeader(Map<String, String> header, String experimentName, String instrument,
                               String indexAdapters, String chemistry, String novaSeqXSeriesMapping) {
        PoolSampleSheetUtils.applyIlluminaHeader(header, experimentName, instrument, indexAdapters, chemistry);
        header.put("Workflow", "LibraryQC");
        header.put("Application", "Library QC");
        header.put("Assay", "Nextera DNA");

    }

    @Override
    public void applySettings(Map<String, String> settings, String read1Primer, String indexPrimer,
                                 String read2Primer, String overrideCycles,
                                 String dragenVersion, String trimUMI,
                                 String fastqCompression) {
        settings.put("\n[Settings]", "");
        settings.put("FlagPCRDuplicates", "1");
        settings.put("ReverseComplement", "0");
        settings.put("RunBwaAln", "0");
        settings.put("Adapter", "CTGTCTCTTATACACATCT");
        PoolSampleSheetUtils.applyIlluminaSettings(settings, read1Primer, indexPrimer, read2Primer);
    }

    @Override
    public void applyData(Map<String, String> data, List<List<String>> dataRows, List<String> headers,
                             List<PoolSampleSheetRecord> samples) {
        data.put("\n[Data]", "");
        PoolSampleSheetUtils.applyIlluminaData(dataRows, headers, samples);
    }

    public void applyCloud(Map<String, String> data, Set<List<String>> dataRows, List<String> headers,
                           List<PoolSampleSheetRecord> dataSection){}
}