package uk.ac.bbsrc.tgac.miso.core.util;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public class PoolSampleSheetFactory {

    private static final Map<String, PoolSampleSheet> REGISTRY = new LinkedHashMap<>();

    static {
        REGISTRY.put("CLONE_CHECKING", new CloneChecking());
        REGISTRY.put("METAGENOMICS_16S",     new Metagenomics16S());
        REGISTRY.put("FASTQ_ONLY_TRUSEQ_NANO_DNA", new FastqOnlyTruseqNanoDna());
        REGISTRY.put("FASTQ_ONLY_NEXTERA_XT",     new FastqOnlyNexteraXT());
        REGISTRY.put("BCL_CONVERT",     new BclConvert());
        REGISTRY.put("LIBRARY_QC",     new LibraryQc());
        REGISTRY.put("REVIO_PAC_BIO",     new PacBioRevio());
    }

    public static PoolSampleSheet of(String type) {
        PoolSampleSheet exp = REGISTRY.get(type.toUpperCase());
        if (exp == null) {
            throw new IllegalArgumentException("Unknown experiment type: " + type);
        }
        return exp;
    }

    public static Map<String, PoolSampleSheet> values() {
        return Collections.unmodifiableMap(REGISTRY);
    }
}
