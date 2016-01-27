package uk.ac.bbsrc.tgac.miso.core.store;

import java.io.IOException;
import java.util.Map;

import uk.ac.bbsrc.tgac.miso.core.data.Submission;

public interface SubmissionStore extends Store<Submission> {

  /**
   * @return a map containing all column names and max lengths from the Submission table
   * @throws IOException
   */
  public Map<String, Integer> getSubmissionColumnSizes() throws IOException;
  
}
