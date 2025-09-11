package uk.ac.bbsrc.tgac.miso.core.util;

public class PoolSampleSheetRecord {
    public final String sampleId;
    public final int lane;
    public final String plate;
    public final String well;
    public final String index1Id;
    public final String index1;
    public final String index2Id;
    public final String index2;
    public final String project;
    public final String genomeFolder;
    public final String description;
    public final String library;
    public final String libraryPrepKit;

    public PoolSampleSheetRecord(
            String sampleId,
            int lane,
            String plate,
            String well,
            String index1Id,
            String index1,
            String index2Id,
            String index2,
            String project,
            String genomeFolder,
            String description,
            String library,
            String libraryPrepKit
    ) {
        this.sampleId = sampleId;
        this.lane = lane;
        this.plate = plate;
        this.well = well;
        this.index1Id = index1Id;
        this.index1 = index1;
        this.index2Id = index2Id;
        this.index2 = index2;
        this.project = project;
        this.genomeFolder = genomeFolder;
        this.description = description;
        this.library = library;
        this.libraryPrepKit = libraryPrepKit;

    }
}