package uk.ac.bbsrc.tgac.miso.persistence;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

import uk.ac.bbsrc.tgac.miso.core.data.impl.ProbeSet;

public interface ProbeSetDao extends SaveDao<ProbeSet> {

  ProbeSet getByName(String name) throws IOException;

  List<ProbeSet> searchByName(String name) throws IOException;

  List<ProbeSet> listByIdList(Collection<Long> idList) throws IOException;

}
