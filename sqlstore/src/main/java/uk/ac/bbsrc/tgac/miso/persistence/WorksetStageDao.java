package uk.ac.bbsrc.tgac.miso.persistence;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

import uk.ac.bbsrc.tgac.miso.core.data.impl.workset.WorksetStage;

public interface WorksetStageDao extends SaveDao<WorksetStage> {

  WorksetStage getByAlias(String alias) throws IOException;

  long getUsage(WorksetStage stage) throws IOException;

  List<WorksetStage> listByIdList(Collection<Long> ids) throws IOException;

}
