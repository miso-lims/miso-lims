package uk.ac.bbsrc.tgac.miso.core.util;

import java.util.*;


public class BclConvert extends AbstractPoolSampleSheet {

    public BclConvert() {
        super("BCL Convert", true, "BCL_CONVERT");
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
        PoolSampleSheetUtils.applyDragenHeader(header, experimentName, instrument, novaSeqXSeriesMapping);

    }

    public void applyHeader(){

    }

    @Override
    public void applySettings(Map<String, String> settings, String read1Primer, String indexPrimer,
                              String read2Primer, String overrideCycles,
                              String dragenVersion, String trimUMI,
                              String fastqCompression) {
        settings.put("\n[BCLConvert_Settings]", "");
        settings.put("FastqCompressionFormat", fastqCompression);
        if (trimUMI != null) {
            settings.put("TrimUMI", trimUMI);
        }
        PoolSampleSheetUtils.applyDragenSettings(settings, dragenVersion, overrideCycles);
    }

    @Override
    public void applyData(Map<String, String> data,
                          List<List<String>> dataRows,
                          List<String> headers,
                          List<PoolSampleSheetRecord> samples) {
        data.put("\n[Data]", "");
        PoolSampleSheetUtils.applyDragenData(dataRows, headers, samples);
    }

    @Override
    public void applyCloud(Map<String, String> data, Set<List<String>> dataRows, List<String> headers,
                           List<PoolSampleSheetRecord> samples){
        data.put("\n[Cloud_Settings]", "");
        data.put("GeneratedVersion", "1.16.0.202410292136");
        data.put("\n[Cloud_Data]", "");
        Collections.addAll(headers, "Sample_ID", "ProjectName", "LibraryName");
        for (PoolSampleSheetRecord sample : samples) {
            List<String> currRow = new ArrayList<>();
            Collections.addAll(currRow, sample.sampleId,
                    sample.project, sample.library);
            dataRows.add(currRow);
        }
    }
}