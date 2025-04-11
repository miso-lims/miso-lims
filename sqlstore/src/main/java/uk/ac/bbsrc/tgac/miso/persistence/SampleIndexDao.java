package uk.ac.bbsrc.tgac.miso.persistence;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

import uk.ac.bbsrc.tgac.miso.core.data.impl.SampleIndex;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SampleIndexFamily;

public interface SampleIndexDao extends SaveDao<SampleIndex> {

  SampleIndex getByName(String name) throws IOException;

  SampleIndex getByFamilyAndName(SampleIndexFamily family, String name) throws IOException;

  List<SampleIndex> listByIdList(Collection<Long> ids) throws IOException;

}
