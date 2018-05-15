package uk.ac.bbsrc.tgac.miso.service.impl;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import uk.ac.bbsrc.tgac.miso.core.data.Pool;
import uk.ac.bbsrc.tgac.miso.core.data.SequencerPartitionContainer;
import uk.ac.bbsrc.tgac.miso.core.data.workflow.WorkflowExecutor;
import uk.ac.bbsrc.tgac.miso.service.ContainerService;
import uk.ac.bbsrc.tgac.miso.service.PoolService;

@Service
@Transactional(rollbackFor = Exception.class)
public class DefaultWorkflowExecutor implements WorkflowExecutor {
  @Autowired
  PoolService poolService;

  @Autowired
  ContainerService containerService;

  @Override
  public Pool save(Pool pool) throws IOException {
    return poolService.get(poolService.save(pool));
  }

  @Override
  public SequencerPartitionContainer save(SequencerPartitionContainer spc) throws IOException {
    return containerService.save(spc);
  }
}
