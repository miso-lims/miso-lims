package uk.ac.bbsrc.tgac.miso.persistence;

import java.util.List;

import uk.ac.bbsrc.tgac.miso.core.data.workflow.Progress;
import uk.ac.bbsrc.tgac.miso.core.data.workflow.ProgressStep;

public interface ProgressStore {
  /**
   * The Progress object and all member entities will not be tracked by Hibernate
   */
  Progress get(long id);

  /**
   * Should only be used for saving/updating Progress objects.
   *
   * @return Hibernate-managed Progress instance
   */
  Progress getManaged(long id);

  List<Progress> listByUserId(long id);

  Progress save(Progress progress);

  void delete(Progress progress);

  void delete(ProgressStep step);
}
