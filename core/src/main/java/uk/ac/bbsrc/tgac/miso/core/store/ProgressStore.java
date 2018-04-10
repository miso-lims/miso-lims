package uk.ac.bbsrc.tgac.miso.core.store;

import java.util.List;

import uk.ac.bbsrc.tgac.miso.core.data.workflow.Progress;

public interface ProgressStore {
  Progress get(long id);

  List<Progress> listByUserId(long id);

  Progress save(Progress progress);

  void delete(Progress progress);
}
