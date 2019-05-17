package uk.ac.bbsrc.tgac.miso.service;

import java.io.IOException;
import java.util.Collection;

import uk.ac.bbsrc.tgac.miso.core.data.Submission;
import uk.ac.bbsrc.tgac.miso.core.service.SaveService;

public interface SubmissionService extends SaveService<Submission> {

  Collection<Submission> list() throws IOException;

}
