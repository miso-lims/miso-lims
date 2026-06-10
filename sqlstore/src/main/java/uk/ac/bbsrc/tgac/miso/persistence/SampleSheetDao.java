package uk.ac.bbsrc.tgac.miso.persistence;

import java.io.IOException;
import java.util.List;

import uk.ac.bbsrc.tgac.miso.core.data.impl.samplesheet.SampleSheet;
import uk.ac.bbsrc.tgac.miso.core.data.type.PlatformType;

public interface SampleSheetDao extends SaveDao<SampleSheet> {

  SampleSheet getByName(String name) throws IOException;

  List<SampleSheet> listByPlatform(PlatformType platform) throws IOException;

}
