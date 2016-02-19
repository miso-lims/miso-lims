package uk.ac.bbsrc.tgac.miso.persistence;

import java.util.List;

import com.eaglegenomics.simlims.core.User;

import uk.ac.bbsrc.tgac.miso.core.data.Project;
import uk.ac.bbsrc.tgac.miso.core.data.SampleNumberPerProject;

public interface SampleNumberPerProjectDao {

  List<SampleNumberPerProject> getSampleNumberPerProject();

  SampleNumberPerProject getSampleNumberPerProject(Long id);

  Long addSampleNumberPerProject(SampleNumberPerProject sampleNumberPerProject);

  void deleteSampleNumberPerProject(SampleNumberPerProject sampleNumberPerProject);

  void update(SampleNumberPerProject sampleNumberPerProject);

  String nextNumber(Project project, User user);
}