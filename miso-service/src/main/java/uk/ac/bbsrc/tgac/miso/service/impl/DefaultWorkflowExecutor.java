package uk.ac.bbsrc.tgac.miso.service.impl;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import uk.ac.bbsrc.tgac.miso.core.data.Pool;
import uk.ac.bbsrc.tgac.miso.core.data.workflow.WorkflowExecutor;
import uk.ac.bbsrc.tgac.miso.service.PoolService;

@Service
@Transactional(rollbackFor = Exception.class)
public class DefaultWorkflowExecutor implements WorkflowExecutor {
  @Autowired
  PoolService poolService;

  @Override
  public Pool save(Pool pool) throws IOException {
    return poolService.get(poolService.save(pool));
  }
}
