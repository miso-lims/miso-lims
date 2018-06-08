package uk.ac.bbsrc.tgac.miso.service.impl;

import java.io.IOException;
import java.util.Collection;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.eaglegenomics.simlims.core.User;

import uk.ac.bbsrc.tgac.miso.core.data.QC;
import uk.ac.bbsrc.tgac.miso.core.data.QcTarget;
import uk.ac.bbsrc.tgac.miso.core.data.QualityControlEntity;
import uk.ac.bbsrc.tgac.miso.core.data.type.QcType;
import uk.ac.bbsrc.tgac.miso.core.store.ChangeLoggableStore;
import uk.ac.bbsrc.tgac.miso.core.store.ContainerQcStore;
import uk.ac.bbsrc.tgac.miso.core.store.LibraryQcStore;
import uk.ac.bbsrc.tgac.miso.core.store.PoolQcStore;
import uk.ac.bbsrc.tgac.miso.core.store.QcTargetStore;
import uk.ac.bbsrc.tgac.miso.core.store.QualityControlTypeStore;
import uk.ac.bbsrc.tgac.miso.core.store.SampleQcStore;
import uk.ac.bbsrc.tgac.miso.service.QualityControlService;
import uk.ac.bbsrc.tgac.miso.service.security.AuthorizationManager;

@Service
@Transactional(rollbackFor = Exception.class)
public class DefaultQualityControlService implements QualityControlService {
  @Autowired
  private AuthorizationManager authorizationManager;
  @Autowired
  private LibraryQcStore libraryQcStore;
  @Autowired
  private PoolQcStore poolQcStore;
  @Autowired
  private ContainerQcStore containerQcStore;
  @Autowired
  private QualityControlTypeStore qcTypeStore;
  @Autowired
  private SampleQcStore sampleQcStore;
  @Autowired
  private ChangeLoggableStore changeLoggableStore;

  @Override
  public QC createQC(QC qc) throws IOException {
    QcTargetStore handler = getHandler(qc.getType().getQcTarget());

    QualityControlEntity entity = handler.getEntity(qc.getEntity().getId());
    authorizationManager.throwIfNotWritable(entity);
    User user = authorizationManager.getCurrentUser();
    qc.setCreator(user);
    qc.setCreationTime(new Date());
    qc.setLastModified(qc.getCreationTime());

    QcType type = qcTypeStore.get(qc.getType().getQcTypeId());
    if (!type.getQcTarget().equals(entity.getQcTarget())) {
      throw new IllegalArgumentException("QC type and entity are mismatched.");
    }
    entity.setChangeDetails(user);
    changeLoggableStore.update(entity);

    long id = handler.save(qc);
    return handler.get(id);
  }

  @Override
  public QC updateQc(QC qc) throws IOException {
    QcTargetStore handler = getHandler(qc.getType().getQcTarget());

    QualityControlEntity entity = handler.getEntity(qc.getEntity().getId());
    authorizationManager.throwIfNotWritable(entity);
    User user = authorizationManager.getCurrentUser();

    QC original = handler.get(qc.getId());
    if (original.getType().getQcTypeId() != qc.getType().getQcTypeId()) {
      throw new IllegalArgumentException("QC type has changed");
    }
    original.setResults(qc.getResults());
    original.setLastModified(new Date());

    entity.setChangeDetails(user);
    changeLoggableStore.update(entity);

    handler.save(original);
    return original;
  }

  @Override
  public QC get(QcTarget target, Long id) throws IOException {
    QC qc = getHandler(target).get(id);
    authorizationManager.throwIfNotReadable(qc.getEntity());
    return qc;
  }

  @Override
  public QualityControlEntity getEntity(QcTarget target, long ownerId) throws IOException {
    QualityControlEntity entity = getHandler(target).getEntity(ownerId);
    authorizationManager.throwIfNotReadable(entity);
    return entity;
  }

  private QcTargetStore getHandler(QcTarget target) {
    switch (target) {
    case Library:
      return libraryQcStore;
    case Pool:
      return poolQcStore;
    case Sample:
      return sampleQcStore;
    case Container:
      return containerQcStore;
    default:
      throw new IllegalArgumentException("Unknown QC target: " + target);
    }
  }

  @Override
  public Collection<? extends QC> listQCsFor(QcTarget target, long ownerId) throws IOException {
    Collection<? extends QC> unfiltered = getHandler(target).listForEntity(ownerId);
    return authorizationManager.filterUnreadable(unfiltered, QC::getEntity);
  }

  @Override
  public Collection<QcType> listQcTypes() throws IOException {
    return qcTypeStore.list();
  }

  public void setAuthorizationManager(AuthorizationManager authorizationManager) {
    this.authorizationManager = authorizationManager;
  }

  public void setLibraryQcStore(LibraryQcStore libraryQcStore) {
    this.libraryQcStore = libraryQcStore;
  }

  public void setPoolQcStore(PoolQcStore poolQcStore) {
    this.poolQcStore = poolQcStore;
  }

  public void setContainerQcStore(ContainerQcStore containerQcStore) {
    this.containerQcStore = containerQcStore;
  }

  public void setQcTypeStore(QualityControlTypeStore qcTypeStore) {
    this.qcTypeStore = qcTypeStore;
  }

  public void setSampleQcStore(SampleQcStore sampleQcStore) {
    this.sampleQcStore = sampleQcStore;
  }

  @Override
  public QC save(QC qc) throws IOException {
    return createQC(qc);
  }

}
