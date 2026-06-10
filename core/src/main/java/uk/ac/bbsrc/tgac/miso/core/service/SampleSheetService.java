package uk.ac.bbsrc.tgac.miso.core.service;

import java.io.IOException;
import java.util.List;

import uk.ac.bbsrc.tgac.miso.core.data.impl.samplesheet.SampleSheet;
import uk.ac.bbsrc.tgac.miso.core.data.type.PlatformType;

public interface SampleSheetService
        extends DeleterService<SampleSheet>, ListService<SampleSheet>, SaveService<SampleSheet> {

    List<SampleSheet> listByPlatform(PlatformType platform) throws IOException;

}
