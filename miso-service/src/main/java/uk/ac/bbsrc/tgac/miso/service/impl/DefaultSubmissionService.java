package uk.ac.bbsrc.tgac.miso.service.impl;

import java.io.IOException;
import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import uk.ac.bbsrc.tgac.miso.core.data.Experiment;
import uk.ac.bbsrc.tgac.miso.core.data.Submission;
import uk.ac.bbsrc.tgac.miso.core.store.SubmissionStore;
import uk.ac.bbsrc.tgac.miso.core.util.WhineyFunction;
import uk.ac.bbsrc.tgac.miso.service.ExperimentService;
import uk.ac.bbsrc.tgac.miso.service.SubmissionService;

@Transactional(rollbackFor = Exception.class)
@Service
public class DefaultSubmissionService implements SubmissionService {
  @Autowired
  private SubmissionStore submissionStore;
  @Autowired
  private ExperimentService experimentService;

  @Override
  public Submission get(long id) throws IOException {
    return submissionStore.get(id);
  }

  @Override
  public Map<String, Integer> getColumnSizes() throws IOException {
    return submissionStore.getSubmissionColumnSizes();
  }

  @Override
  public Collection<Submission> list() throws IOException {
    return submissionStore.listAll();
  }

  @Override
  public long save(Submission submission) throws IOException {
    if (submission.getId() == Submission.UNSAVED_ID) {
      submission.setExperiments(submission.getExperiments().stream().map(Experiment::getId)
          .map(WhineyFunction.rethrow(experimentService::get))
          .collect(Collectors.toSet()));
      return submissionStore.save(submission);
    } else {
      Submission managed = submissionStore.get(submission.getId());
      managed.setAccession(submission.getAccession());
      managed.setAlias(submission.getAlias());
      managed.setCompleted(submission.isCompleted());
      managed.setDescription(submission.getDescription());
      managed.setSubmissionDate(submission.getSubmissionDate());
      managed.setTitle(submission.getTitle());
      managed.setVerified(submission.isVerified());
      return submissionStore.save(submission);
    }
  }

}
