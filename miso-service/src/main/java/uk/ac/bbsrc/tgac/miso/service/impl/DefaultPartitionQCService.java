package uk.ac.bbsrc.tgac.miso.service.impl;

import java.io.IOException;
import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import uk.ac.bbsrc.tgac.miso.core.data.Partition;
import uk.ac.bbsrc.tgac.miso.core.data.PartitionQC;
import uk.ac.bbsrc.tgac.miso.core.data.PartitionQCType;
import uk.ac.bbsrc.tgac.miso.core.data.Run;
import uk.ac.bbsrc.tgac.miso.core.store.PartitionQcStore;
import uk.ac.bbsrc.tgac.miso.service.ContainerService;
import uk.ac.bbsrc.tgac.miso.service.security.AuthorizationManager;

@Service
@Transactional(rollbackFor = Exception.class)
public class DefaultPartitionQCService implements PartitionQCService {

  @Autowired
  private AuthorizationManager authorizationManager;
  @Autowired
  private ContainerService containerService;
  @Autowired
  private PartitionQcStore partitionQcDao;
  @Autowired
  private RunService runService;

  @Override
  public PartitionQC get(Run run, Partition partition) throws IOException {
    Run managedRun = runService.get(run.getId());
    authorizationManager.throwIfNotReadable(managedRun);
    return partitionQcDao.get(managedRun, partition);
  }

  @Override
  public PartitionQCType getType(long qcTypeId) throws IOException {
    return partitionQcDao.getType(qcTypeId);
  }

  @Override
  public Collection<PartitionQCType> listTypes() throws IOException {
    return partitionQcDao.listTypes();
  }

  @Override
  public void save(PartitionQC qc) throws IOException {
    PartitionQC managedQc = get(qc.getRun(), qc.getPartition());
    Run managedRun = runService.get(qc.getRun().getId());
    authorizationManager.throwIfNotWritable(managedRun);
    if (managedQc == null) {
      qc.setRun(managedRun);
      qc.setPartition(containerService.getPartition(qc.getPartition().getId()));
      qc.setType(getType(qc.getType().getId()));
      partitionQcDao.create(qc);
    } else {
      managedQc.setType(getType(qc.getType().getId()));
      managedQc.setNotes(qc.getNotes());
      partitionQcDao.update(managedQc);
    }
  }
}
