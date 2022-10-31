package uk.ac.bbsrc.tgac.miso.service.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

import com.eaglegenomics.simlims.core.User;

import uk.ac.bbsrc.tgac.miso.core.data.ChangeLog;
import uk.ac.bbsrc.tgac.miso.core.data.qc.*;
import uk.ac.bbsrc.tgac.miso.core.data.type.QcType;
import uk.ac.bbsrc.tgac.miso.core.security.AuthorizationManager;
import uk.ac.bbsrc.tgac.miso.core.service.BulkQcSaveOperation;
import uk.ac.bbsrc.tgac.miso.core.service.ChangeLogService;
import uk.ac.bbsrc.tgac.miso.core.service.InstrumentService;
import uk.ac.bbsrc.tgac.miso.core.service.KitDescriptorService;
import uk.ac.bbsrc.tgac.miso.core.service.QcTypeService;
import uk.ac.bbsrc.tgac.miso.core.service.QualityControlService;
import uk.ac.bbsrc.tgac.miso.core.service.exception.BulkValidationException;
import uk.ac.bbsrc.tgac.miso.core.service.exception.ValidationError;
import uk.ac.bbsrc.tgac.miso.core.service.exception.ValidationException;
import uk.ac.bbsrc.tgac.miso.core.store.DeletionStore;
import uk.ac.bbsrc.tgac.miso.core.util.PrometheusAsyncMonitor;
import uk.ac.bbsrc.tgac.miso.core.util.ThrowingFunction;
import uk.ac.bbsrc.tgac.miso.persistence.ChangeLoggableStore;
import uk.ac.bbsrc.tgac.miso.persistence.ContainerQcStore;
import uk.ac.bbsrc.tgac.miso.persistence.LibraryQcStore;
import uk.ac.bbsrc.tgac.miso.persistence.PoolQcStore;
import uk.ac.bbsrc.tgac.miso.persistence.QcTargetStore;
import uk.ac.bbsrc.tgac.miso.persistence.QualityControlTypeStore;
import uk.ac.bbsrc.tgac.miso.persistence.RequisitionQcStore;
import uk.ac.bbsrc.tgac.miso.persistence.SampleQcStore;

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
  private RequisitionQcStore requisitionQcStore;
  @Autowired
  private QcTypeService qcTypeService;
  @Autowired
  private KitDescriptorService kitDescriptorService;
  @Autowired
  private ChangeLogService changeLogService;
  @Autowired
  private QualityControlTypeStore qcTypeStore;
  @Autowired
  private SampleQcStore sampleQcStore;
  @Autowired
  private ChangeLoggableStore changeLoggableStore;
  @Autowired
  private InstrumentService instrumentService;
  @Autowired
  private DeletionStore deletionStore;
  @Autowired
  private TransactionTemplate transactionTemplate;

  @Override
  public QC create(QC qc) throws IOException {
    loadChildEntities(qc);
    QcTargetStore handler = getHandler(qc.getType().getQcTarget());

    QualityControlEntity entity = handler.getEntity(qc.getEntity().getId());
    validateChange(qc, null);

    User user = authorizationManager.getCurrentUser();
    qc.setCreator(user);
    qc.setCreationTime(new Date());
    qc.setLastModified(qc.getCreationTime());

    if (!qc.getType().getQcTarget().equals(entity.getQcTarget())) {
      throw new IllegalArgumentException("QC type and entity are mismatched.");
    }

    if (qc.getType().isAutoUpdateField() && qc.getType().getCorrespondingField() != QcCorrespondingField.NONE) {
      handler.updateEntity(qc.getEntity().getId(), qc.getType().getCorrespondingField(), qc.getResults(), qc.getType().getUnits());
    }

    entity.setChangeDetails(user);
    changeLoggableStore.update(entity);

    long id = handler.save(qc);
    saveControlRuns(id, qc, handler);
    return handler.get(id);
  }

  @Override
  public QC update(QC qc) throws IOException {
    loadChildEntities(qc);
    QcTargetStore handler = getHandler(qc.getType().getQcTarget());

    QualityControlEntity entity = handler.getEntity(qc.getEntity().getId());
    User user = authorizationManager.getCurrentUser();

    QC managed = handler.get(qc.getId());
    if (managed.getType().getId() != qc.getType().getId()) {
      throw new IllegalArgumentException("QC type has changed");
    }
    validateChange(qc, managed);
    applyChanges(managed, qc);

    entity.setChangeDetails(user);
    changeLoggableStore.update(entity);

    handler.save(managed);
    saveControlRuns(managed.getId(), qc, handler);
    return managed;
  }

  private void saveControlRuns(long savedId, QC from, QcTargetStore handler) throws IOException {
    QC to = get(from.getEntity().getQcTarget(), from.getId());
    List<QcControlRun> toDelete = new ArrayList<>();
    for (QcControlRun toControl : to.getControls()) {
      if (!toControl.isSaved()) {
        // newly created QC
        continue;
      }
      QcControlRun fromControl = from.getControls().stream()
          .filter(fc -> fc.getId() == toControl.getId())
          .findFirst().orElse(null);
      if (fromControl == null) {
        toDelete.add(toControl);
      } else {
        toControl.setControl(fromControl.getControl());
        toControl.setLot(fromControl.getLot());
        toControl.setQcPassed(fromControl.isQcPassed());
        handler.updateControlRun(toControl);
      }
    }

    for (QcControlRun control : toDelete) {
      handler.deleteControlRun(control);
    }

    for (QcControlRun fromControl : from.getControls()) {
      if (!fromControl.isSaved()) {
        switch (to.getType().getQcTarget()) {
        case Container:
          ((ContainerQcControlRun) fromControl).setQc((ContainerQC) to);
          break;
        case Library:
          ((LibraryQcControlRun) fromControl).setQc((LibraryQC) to);
          break;
        case Pool:
          ((PoolQcControlRun) fromControl).setQc((PoolQC) to);
          break;
        case Sample:
          ((SampleQcControlRun) fromControl).setQc((SampleQC) to);
          break;
        default:
          throw new IllegalArgumentException("Unhandled QC target: " + to.getType().getQcTarget());
        }
        handler.createControlRun(fromControl);
      }
    }
  }

  private void loadChildEntities(QC qc) throws IOException {
    ValidationUtils.loadChildEntity(qc::setType, qc.getType(), qcTypeService, "typeName");
    ValidationUtils.loadChildEntity(qc::setInstrument, qc.getInstrument(), instrumentService, "instrument");
    ValidationUtils.loadChildEntity(qc::setKit, qc.getKit(), kitDescriptorService, "kit");
  }

  private void applyChanges(QC to, QC from) {
    to.setResults(from.getResults());
    to.setLastModified(new Date());
    to.setInstrument(from.getInstrument());
    to.setKit(from.getKit());
    to.setKitLot(from.getKitLot());
    to.setDescription(from.getDescription());
    to.setDate(from.getDate());
  }

  private void validateChange(QC qc, QC beforeChange) throws IOException {
    List<ValidationError> errors = new ArrayList<>();

    if (beforeChange != null && ValidationUtils.isSetAndChanged(QC::getType, qc, beforeChange)) {
      errors.add(new ValidationError("typeName", "Cannot be changed"));
    }

    if (qc.getType().getInstrumentModel() != null) {
      if (qc.getInstrument() == null) {
        errors.add(new ValidationError("instrument", "Must be specified"));
      } else if (qc.getInstrument().getInstrumentModel().getId() != qc.getType().getInstrumentModel().getId()) {
        errors.add(new ValidationError("instrument", "Invalid instrument model"));
      }
    }
    if (qc.getType().getKitDescriptors() != null && !qc.getType().getKitDescriptors().isEmpty()) {
      if (qc.getKit() == null) {
        errors.add(new ValidationError("kitDescriptorId", "Must be specified"));
      } else if (qc.getType().getKitDescriptors().stream().noneMatch(kit -> kit.getId() == qc.getKit().getId())) {
        errors.add(new ValidationError("kitDescriptorId", "Kit is not valid for this QC type"));
      }
      if (qc.getKitLot() == null) {
        errors.add(new ValidationError("kitLot", "Must be specified"));
      }
    }

    for (QcControlRun controlRun : qc.getControls()) {
      if (qc.getType().getControls().stream().noneMatch(control -> control.getId() == controlRun.getControl().getId())) {
        errors.add(new ValidationError("controlId", "Invalid control for this QC type"));
      }
    }

    if (!errors.isEmpty()) {
      throw new ValidationException(errors);
    }
  }

  @Override
  public QC get(QcTarget target, Long id) throws IOException {
    return getHandler(target).get(id);
  }

  @Override
  public QualityControlEntity getEntity(QcTarget target, long ownerId) throws IOException {
    return getHandler(target).getEntity(ownerId);
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
    case Requisition:
      return requisitionQcStore;
    default:
      throw new IllegalArgumentException("Unknown QC target: " + target);
    }
  }

  @Override
  public Collection<? extends QC> listQCsFor(QcTarget target, long ownerId) throws IOException {
    Collection<? extends QC> unfiltered = getHandler(target).listForEntity(ownerId);
    return unfiltered;
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

  public void setRequisitionQcStore(RequisitionQcStore requisitionQcStore) {
    this.requisitionQcStore = requisitionQcStore;
  }

  public void setQcTypeStore(QualityControlTypeStore qcTypeStore) {
    this.qcTypeStore = qcTypeStore;
  }

  public void setSampleQcStore(SampleQcStore sampleQcStore) {
    this.sampleQcStore = sampleQcStore;
  }

  @Override
  public List<? extends QC> listByIdList(QcTarget qcTarget, List<Long> ids) throws IOException {
    QcTargetStore handler = getHandler(qcTarget);
    return handler.listByIdList(ids);
  }

  @Override
  public BulkQcSaveOperation startBulkCreate(List<QC> items) throws IOException {
    return startBulkOperation(items, x -> PrometheusAsyncMonitor.monitor(getClass().getSimpleName(), "create", this::create, x));
  }

  @Override
  public BulkQcSaveOperation startBulkUpdate(List<QC> items) throws IOException {
    return startBulkOperation(items, x -> PrometheusAsyncMonitor.monitor(getClass().getSimpleName(), "update", this::update, x));
  }

  private BulkQcSaveOperation startBulkOperation(List<QC> items, ThrowingFunction<QC, QC, IOException> action) throws IOException {
    QcTarget qcTarget = qcTypeService.get(items.get(0).getType().getId()).getQcTarget();
    BulkQcSaveOperation operation = new BulkQcSaveOperation(qcTarget, items, authorizationManager.getCurrentUser());
    // Authentication is tied to the thread, so use this same auth in the new thread
    Authentication auth = SecurityContextHolder.getContextHolderStrategy().getContext().getAuthentication();
    Thread thread = new Thread(() -> {
      SecurityContextHolder.getContextHolderStrategy().getContext().setAuthentication(auth);
      try {
        transactionTemplate.execute(new TransactionCallbackWithoutResult() {

          @Override
          protected void doInTransactionWithoutResult(TransactionStatus status) {
            while (operation.hasMore()) {
              try {
                QC item = operation.getNextItem();
                QC saved = action.apply(item);
                operation.addSuccess(saved.getId());
              } catch (ValidationException e) {
                operation.addFailure(e);
                status.setRollbackOnly();
              } catch (Exception e) {
                operation.setFailed(e);
                status.setRollbackOnly();
              }
            }
          }
        });
      } catch (Exception e) {
        // Exception during transaction commit
        operation.setFailed(e);
      }

      if (operation.isFailed()) {
        Exception exception = operation.getException();
        if (!(exception instanceof BulkValidationException)) {
          LoggerFactory.getLogger(DefaultQualityControlService.class).error("Bulk save failed", exception);
        }
      }
      operation.setComplete();
    });

    thread.start();
    return operation;
  }

  @Override
  public DeletionStore getDeletionStore() {
    return deletionStore;
  }

  @Override
  public AuthorizationManager getAuthorizationManager() {
    return authorizationManager;
  }

  @Override
  public QC get(long id) throws IOException {
    throw new UnsupportedOperationException("Cannot retrieve QC without QC target");
  }

  @Override
  public void authorizeDeletion(QC object) throws IOException {
    authorizationManager.throwIfNonAdminOrMatchingOwner(object.getCreator());
  }

  @Override
  public QC getManaged(QC object) throws IOException {
    return get(object.getEntity().getQcTarget(), object.getId());
  }

  @Override
  public void beforeDelete(QC object) throws IOException {
    QcTargetStore handler = getHandler(object.getType().getQcTarget());
    QualityControlEntity entity = handler.getEntity(object.getEntity().getId());
    ChangeLog change = entity.createChangeLog("QC deleted: " + object.getType().getName(), "", authorizationManager.getCurrentUser());
    changeLogService.create(change);
  }

}
