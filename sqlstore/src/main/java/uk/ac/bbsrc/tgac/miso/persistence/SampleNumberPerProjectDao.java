package uk.ac.bbsrc.tgac.miso.persistence;

import com.eaglegenomics.simlims.core.User;

import uk.ac.bbsrc.tgac.miso.core.data.Project;
import uk.ac.bbsrc.tgac.miso.core.data.SampleNumberPerProject;

public interface SampleNumberPerProjectDao extends ProviderDao<SampleNumberPerProject> {

  SampleNumberPerProject getSampleNumberPerProject(Long id);

  SampleNumberPerProject getByProject(Project project);

  Long addSampleNumberPerProject(SampleNumberPerProject sampleNumberPerProject);

  void update(SampleNumberPerProject sampleNumberPerProject);

  void delete(SampleNumberPerProject sampleNumberPerProject);

  String nextNumber(Project project, User user, String partialAlias);
}
