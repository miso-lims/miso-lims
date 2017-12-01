package uk.ac.bbsrc.tgac.miso.service;

import java.io.IOException;
import java.util.Collection;
import java.util.Map;

import uk.ac.bbsrc.tgac.miso.core.data.Submission;

public interface SubmissionService {
  Submission get(long id) throws IOException;

  Collection<Submission> list() throws IOException;
  long save(Submission submission) throws IOException;

  Map<String, Integer> getColumnSizes() throws IOException;
}
